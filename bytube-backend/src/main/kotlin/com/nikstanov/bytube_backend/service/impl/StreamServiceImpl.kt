package com.nikstanov.bytube_backend.service.impl

import com.nikstanov.bytube_backend.constant.StreamStatus
import com.nikstanov.bytube_backend.dto.StreamDto
import com.nikstanov.bytube_backend.entity.StreamEntity
import com.nikstanov.bytube_backend.exception.NotFoundException
import com.nikstanov.bytube_backend.exception.StreamAlreadyStartedException
import com.nikstanov.bytube_backend.repository.StreamRepository
import com.nikstanov.bytube_backend.service.MinioStorageService
import com.nikstanov.bytube_backend.service.StreamService
import com.nikstanov.bytube_backend.service.UserDetailsService
import com.nikstanov.bytube_backend.uttils.mapper.StreamMapper
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.LocalDateTime
import java.util.*

@Service
class StreamServiceImpl(val streamRepository: StreamRepository, val streamMapper: StreamMapper, val minioStorageService: MinioStorageService, val userDetailsService: UserDetailsService) : StreamService {
    override fun resetStream(name: String, userId: UUID): Mono<String> {
        return streamRepository.findByOwnerId(userId).flatMap { stream ->
            if(stream.status == StreamStatus.STARTED){
                return@flatMap Mono.error(StreamAlreadyStartedException());
            }
            stream.name = name
            stream.streamKey = UUID.randomUUID()
            stream.status = StreamStatus.CREATED
            streamRepository.save(stream).flatMap {
                entity -> Mono.just(entity.streamKey.toString())
            }
        }
            .switchIfEmpty {
                val newStreamEntity = StreamEntity(
                    id = UUID.randomUUID(),
                    name = name,
                    streamKey = UUID.randomUUID(),
                    status = StreamStatus.CREATED,
                    ownerId = userId,
                    startTime = LocalDateTime.now(),
                    endTime = LocalDateTime.now()
                )
                streamRepository.save(newStreamEntity).flatMap {
                        entity -> Mono.just(entity.streamKey.toString())
                }
            }
    }

    override fun updateStream(name: String, userId: UUID): Mono<String> {
        return streamRepository.findByOwnerId(userId).flatMap { stream ->
            stream.name = name
            streamRepository.save(stream).flatMap {
                    entity -> Mono.just(entity.name)
            }
        }
    }

    override fun startStream(streamKey: String): Mono<String> {
        return streamRepository.findByStreamKey(UUID.fromString(streamKey)).flatMap { stream ->
            stream.status = StreamStatus.STARTED
            streamRepository.save(stream).flatMap {
                    _ -> Mono.just("Stream started")
            }
        }
            .switchIfEmpty {
                Mono.error(NotFoundException("stream", "stream with key $streamKey not found"))
            }
    }

    override fun endStream(streamKey: String): Mono<String> {
        return streamRepository.findByStreamKey(UUID.fromString(streamKey)).flatMap { stream ->
            stream.status = StreamStatus.STOPPED
            streamRepository.save(stream).flatMap {
                    _ -> Mono.just("Stream started")
            }
        }
            .switchIfEmpty {
                throw NotFoundException("stream", "stream with key $streamKey not found")
            }
    }

    override fun getStreams(page: Int, size: Int): Flux<StreamDto> {
        return streamRepository.findAllByStatus(StreamStatus.STARTED, PageRequest.of(page, size)).flatMap { video ->
            userDetailsService.getUserById(video.ownerId).map { user ->
                video.author = user
                streamMapper.toDto(video)
            }
        }
    }

    override fun getUserStream(userId: UUID): Flux<StreamDto> {
        return streamRepository.findByOwnerIdAndStatus(userId, StreamStatus.STARTED).flatMap { stream ->
            userDetailsService.getUserById(stream.ownerId).map { user ->
                stream.author = user
                streamMapper.toDto(stream)
            }
        }
    }

    override fun getStreamKeyId(streamId: UUID): Mono<UUID> {
        return streamRepository.findById(streamId).map {
            it.streamKey
        }
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
}