package com.nikstanov.bytube_backend.service

import com.nikstanov.bytube_backend.dto.StreamDto
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.security.core.Authentication
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface StreamService {
    fun resetStream(name: String, userId: UUID) : Mono<String>
    fun updateStream(name: String, userId: UUID) : Mono<String>

    fun startStream(streamKey: String): Mono<String>
    fun endStream(streamKey: String) : Mono<String>
    fun getStreams(page: Int, size: Int): Flux<StreamDto>
    fun getUserStream(userId: UUID): Flux<StreamDto>
    fun getStreamKeyId(streamId: UUID): Mono<UUID>
    fun getAllData(bucketName: String, segmentPath: String): Mono<Flux<DataBuffer>>
}