package com.nikstanov.bytube_backend.service

import com.nikstanov.bytube_backend.dto.VideoDto
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.Authentication
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.util.*

interface VideoService {
    fun uploadDirectoryToMinIO(directory: File, fileName: String) : Mono<Int>
    fun getAllData(bucketName: String, segmentPath: String) : Mono<Flux<DataBuffer>>
    fun convertAndUploadVideo(videoFile: FilePart, previewFile: FilePart, userId: UUID, name: String, description: String, open: Boolean) : Mono<UUID>
    fun updateViewersCount(authentication: Authentication?, videoId: UUID) : Mono<Int>
    fun getPublicVideos(page: Int, size: Int): Flux<VideoDto>
    fun searchVideos(name: String): Flux<VideoDto>
    fun getOwnerVideos(userId: UUID, page: Int, size: Int): Flux<VideoDto>
    fun getUserVideos(userId: UUID, page: Int, size: Int): Flux<VideoDto>
    fun getPublicVideoById(videoId: UUID): Mono<VideoDto>
    fun deleteVideo(videoId: UUID): Mono<Boolean>
    fun updateStatus(videoId: UUID): Mono<VideoDto>
}