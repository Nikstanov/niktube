package com.nikstanov.bytube_backend.contoroller

import com.nikstanov.bytube_backend.constant.ContentType
import com.nikstanov.bytube_backend.constant.MinioBuckets
import com.nikstanov.bytube_backend.dto.VideoDto
import com.nikstanov.bytube_backend.security.PersonDetails
import com.nikstanov.bytube_backend.service.VideoService
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*


@RestController
@RequestMapping("/video")
class VideoController(val videoService: VideoService) {

    @GetMapping("/public/search")
    fun searchVideos(
        @RequestParam name: String,
    ): Flux<VideoDto> {
        return videoService.searchVideos(name)
    }

    @GetMapping("/public")
    fun getPublicVideos(
        @RequestParam page: Int,
        @RequestParam size: Int
    ): Flux<VideoDto> {
        return videoService.getPublicVideos(page, size)
    }

    @GetMapping("/public/{videoId}")
    fun getPublicVideosById(
        @PathVariable videoId: String
    ): Mono<VideoDto> {
        return videoService.getPublicVideoById(UUID.fromString(videoId))
    }

    @DeleteMapping("/{videoId}")
    fun deleteVideosById(
        @PathVariable videoId: String
    ): Mono<VideoDto> {
        return videoService.getPublicVideoById(UUID.fromString(videoId))
    }

    @PatchMapping("/{videoId}")
    fun updateStatus(
        @PathVariable videoId: String
    ): Mono<VideoDto> {
        return videoService.getPublicVideoById(UUID.fromString(videoId))
    }

    @GetMapping("/user/{userId}")
    fun getUserVideos(
        @PathVariable userId: UUID,
        @RequestParam page: Int,
        @RequestParam size: Int,
        authentication: Authentication?
    ): Flux<VideoDto> {

        if (authentication != null) {
            val details = if(authentication.details is PersonDetails) {
                authentication.details as PersonDetails
            }else{
                authentication.principal as PersonDetails
            }
            if (details.user.id == userId) {
                return videoService.getOwnerVideos(userId, page, size)
            }
        }
        return videoService.getUserVideos(userId, page, size)
    }

    @GetMapping("/hls/{videoId}/master.m3u8")
    fun getMasterPlaylist(@PathVariable videoId: String): Mono<ResponseEntity<Flux<DataBuffer>>> {
        val bucketName = MinioBuckets.videos.name
        val masterPlaylistPath = "$videoId/master.m3u8"

        return videoService.getAllData(bucketName, masterPlaylistPath).map { data ->
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLE)
                .body(data)
        }
    }

    @GetMapping("/hls/{videoId}/{quality}.m3u8")
    fun getPlaylist(@PathVariable videoId: String, @PathVariable quality: String): Mono<ResponseEntity<Flux<DataBuffer>>> {
        val bucketName = MinioBuckets.videos.name
        val playlistPath = "$videoId/$quality.m3u8"

        return videoService.getAllData(bucketName, playlistPath).map { data ->
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLE)
                .body(data)
        }
    }

    @GetMapping("/hls/{videoId}/{quality}_{segmentId}.ts")
    fun getSegment(
        @PathVariable videoId: String,
        @PathVariable quality: String,
        @PathVariable segmentId: String,
        authentication: Authentication? = null
    ): Mono<ResponseEntity<Flux<DataBuffer>>> {
        val bucketName = MinioBuckets.videos.name
        val segmentPath = "$videoId/${quality}_$segmentId.ts"
        return videoService.updateViewersCount(authentication, UUID.fromString(videoId))
            .flatMap {
                videoService.getAllData(bucketName, segmentPath).map { data ->
                    ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLE)
                        .body(data)
                }
            }

    }
}