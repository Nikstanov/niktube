package com.nikstanov.bytube_backend.service.impl

import com.nikstanov.bytube_backend.constant.ContentType
import com.nikstanov.bytube_backend.constant.MinioBuckets
import com.nikstanov.bytube_backend.dto.VideoDto
import com.nikstanov.bytube_backend.entity.PasswordResetToken
import com.nikstanov.bytube_backend.entity.VideoEntity
import com.nikstanov.bytube_backend.repository.VideoRepository
import com.nikstanov.bytube_backend.security.PersonDetails
import com.nikstanov.bytube_backend.service.HLSService
import com.nikstanov.bytube_backend.service.MinioStorageService
import com.nikstanov.bytube_backend.service.UserDetailsService
import com.nikstanov.bytube_backend.service.VideoService
import com.nikstanov.bytube_backend.uttils.mapper.VideoMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import java.io.File
import java.util.*
import java.util.stream.Collectors

@Service
class VideoServiceImpl(
    val minioStorageService: MinioStorageService,
    val userDetailsService: UserDetailsService,
    val videoMapper: VideoMapper,
    val videoRepository: VideoRepository,
    val HLSService: HLSService,
    @Qualifier("videoSessionsOps") val videoSessionOps: ReactiveValueOperations<String, Int>
) : VideoService {
    override fun uploadDirectoryToMinIO(directory: File, fileName: String) : Mono<Int> {
        var count = 0
        directory.listFiles()?.forEach { file ->
            val objectName = "$fileName/${file.name}"
            file.inputStream().use { inputStream ->
                minioStorageService.save(inputStream, MinioBuckets.videos.name, objectName, ContentType.getContentType(file.name), file.length())
                println("Uploaded ${file.name} to MinIO bucket \"videos\"")
            }
            if(file.name.endsWith(".ts")) {
                val parts = file.name.split("_")
                val index = parts[1].removeSuffix(".ts").toInt()
                if(count < index){
                    count = index
                }
            }
        }
        return Mono.just(count + 1)
    }

    override fun getAllData(bucketName: String, segmentPath: String): Mono<Flux<DataBuffer>> {
        return minioStorageService.load(bucketName, segmentPath).map{ inputStream ->
            DataBufferUtils.readInputStream(
                { inputStream },
                DefaultDataBufferFactory(),
                4096
            )
        }
    }

    @Transactional
    override fun convertAndUploadVideo(videoFile: FilePart, previewFile: FilePart, userId: UUID, name: String, description: String, open: Boolean): Mono<UUID> {
        val previewId = UUID.randomUUID()
        return videoRepository.save(VideoEntity(null, name, description, userId, previewId, open, 0)).publishOn(Schedulers.boundedElastic()).flatMap { videoEntity ->
            val inputFile = File(System.getProperty("java.io.tmpdir"), videoFile.filename())
            val preview = File(System.getProperty("java.io.tmpdir"), previewFile.filename())
            val outputDir = File(System.getProperty("java.io.tmpdir"), "hls_output").apply { mkdirs() }
            previewFile.transferTo(preview).block()
            videoFile.transferTo(inputFile).block()
            val inputResolution = HLSService.getVideoResolution(inputFile)
            val resolutions = listOf(
                Triple("1440", 8000, 1440),
                Triple("1080", 4500, 1080),
                Triple("720", 2500, 720),
                Triple("480", 1000, 480),
            ).filter { it.third <= inputResolution }
            val playlists = resolutions.parallelStream().map { (height, bitrate, _) ->
                HLSService.generateHLS(inputFile, outputDir, height, bitrate)
            }.collect(Collectors.toList())
            HLSService.generateMasterPlaylist(playlists, outputDir)
            uploadDirectoryToMinIO(outputDir, videoEntity.id!!.toString())
                .map{res ->
                    videoEntity.segmentsCount = res
                    videoRepository.save(videoEntity)
                }
                .flatMap { _ ->
                    minioStorageService.save(
                        preview.inputStream(),
                        MinioBuckets.previews.name,
                        previewId.toString(),
                        ContentType.IMAGE,
                        preview.length()
                    ).flatMap { Mono.just(videoEntity.id!!) }
                }
        }
    }

    override fun updateViewersCount(authentication: Authentication?, videoId: UUID): Mono<Int> {
        if(authentication != null){
            val details = if(authentication.details is PersonDetails) {
                authentication.details as PersonDetails
            }else{
                authentication.principal as PersonDetails
            }
            val key = "$videoId:${details.user.id}"
            return videoSessionOps.get(key)
                .switchIfEmpty(Mono.just(0))
                .flatMap { viewedSegments ->

                    val updatedSegments = viewedSegments + 1
                    videoSessionOps.set(key, updatedSegments).flatMap { _ ->
                        videoRepository.findById(videoId).flatMap{videoEntity ->
                            if(videoEntity.segmentsCount / 3 == viewedSegments){
                                videoEntity.views += 1
                                return@flatMap videoRepository.save(videoEntity).flatMap { _ ->
                                    Mono.just(updatedSegments)
                                }
                            }
                            Mono.just(updatedSegments)
                        }
                    }
                }
        }
        return Mono.just(0)
    }

    override fun getPublicVideos(page: Int, size: Int): Flux<VideoDto> {
        return videoRepository.findByOpen(true, PageRequest.of(page, size)).flatMap { video ->
            userDetailsService.getUserById(video.ownerId).map { user ->
                video.author = user
                videoMapper.toDto(video)
            }

        }
    }

    override fun searchVideos(name: String): Flux<VideoDto> {
        return videoRepository.findByOpenAndName(true, name).flatMap { video ->
            userDetailsService.getUserById(video.ownerId).map { user ->
                video.author = user
                videoMapper.toDto(video)
            }
        }
    }

    override fun getOwnerVideos(userId: UUID, page: Int, size: Int): Flux<VideoDto> {
        return videoRepository.findByOwnerId(userId, PageRequest.of(page, size)).flatMap { video ->
            userDetailsService.getUserById(video.ownerId).map { user ->
                video.author = user
                videoMapper.toDto(video)
            }
        }
    }

    override fun getPublicVideoById(videoId: UUID): Mono<VideoDto> {
        return videoRepository.findById(videoId).flatMap { video ->
            userDetailsService.getUserById(video.ownerId).map { user ->
                video.author = user
                videoMapper.toDto(video)
            }
        }
    }

    override fun deleteVideo(videoId: UUID): Mono<Boolean> {
        return videoRepository.deleteById(videoId).then(Mono.defer { Mono.just(true) })
    }

    override fun updateStatus(videoId: UUID): Mono<VideoDto> {
        return videoRepository.findById(videoId).flatMap { video ->
            video.open = ! video.open;
            videoRepository.save(video).flatMap { video ->
                userDetailsService.getUserById(video.ownerId).map { user ->
                    video.author = user
                    videoMapper.toDto(video)
                }
            }

        }
    }

    override fun getUserVideos(userId: UUID, page: Int, size: Int): Flux<VideoDto> {
        return videoRepository.findByOwnerIdAndOpen(userId, true, PageRequest.of(page, size)).flatMap { video ->
            userDetailsService.getUserById(video.ownerId).map { user ->
                video.author = user
                videoMapper.toDto(video)
            }
        }
    }
}