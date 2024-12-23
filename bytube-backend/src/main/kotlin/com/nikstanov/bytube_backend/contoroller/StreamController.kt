package com.nikstanov.bytube_backend.contoroller

import com.nikstanov.bytube_backend.constant.ContentType
import com.nikstanov.bytube_backend.constant.MinioBuckets
import com.nikstanov.bytube_backend.dto.StartStreamRequestDto
import com.nikstanov.bytube_backend.dto.StreamDto
import com.nikstanov.bytube_backend.security.PersonDetails
import com.nikstanov.bytube_backend.service.StreamService
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/streams")
class StreamController(val streamService: StreamService) {

    @PostMapping
    fun resetStream(@RequestParam name: String, authentication: Authentication): Mono<String> {
        val details = if(authentication.details is PersonDetails) {
            authentication.details as PersonDetails
        }else{
            authentication.principal as PersonDetails
        }
        return streamService.resetStream(name, details.user.id)
    }

    @PatchMapping
    fun updateStream(@RequestParam name: String, authentication: Authentication): Mono<String> {
        val details = if(authentication.details is PersonDetails) {
            authentication.details as PersonDetails
        }else{
            authentication.principal as PersonDetails
        }
        return streamService.updateStream(name, details.user.id)
    }

    @PostMapping("/public/start")
    fun startStream(@ModelAttribute body: StartStreamRequestDto): Mono<String> {
        return streamService.startStream(body.name)
    }

    @PostMapping("/public/stop")
    fun stopStream(@ModelAttribute body: StartStreamRequestDto): Mono<String> {
        return streamService.endStream(body.name)
    }

    @GetMapping("/public")
    fun getStreams(
        @RequestParam page: Int,
        @RequestParam size: Int
    ): Flux<StreamDto> {
        return streamService.getStreams(page, size)
    }

    @GetMapping("/public/{userId}")
    fun getUserStream(
        @PathVariable userId: String
    ): Flux<StreamDto> {
        return streamService.getUserStream(UUID.fromString(userId))
    }

    @GetMapping("/hls/{streamId}.m3u8")
    fun getPlaylist(@PathVariable streamId: String): Mono<ResponseEntity<Flux<DataBuffer>>> {
        val bucketName = MinioBuckets.streams.name
        return streamService.getStreamKeyId(UUID.fromString(streamId)).flatMap { streamKeyId ->
            val playlistPath = "$streamKeyId.m3u8"
            streamService.getAllData(bucketName, playlistPath).map { data ->
                ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLE)
                    .body(data)
            }
        }

    }

    @GetMapping("/hls/{streamId}-{segmentId}.ts")
    fun getSegment(
        @PathVariable streamId: String,
        @PathVariable segmentId: String,
    ): Mono<ResponseEntity<Flux<DataBuffer>>> {
        val bucketName = MinioBuckets.streams.name
        val segmentPath = "$streamId-$segmentId.ts"

        return streamService.getAllData(bucketName, segmentPath).map { data ->
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLE)
                .body(data)
        }
    }

}