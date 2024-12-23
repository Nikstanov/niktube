package com.nikstanov.bytube_backend.service

import io.minio.ObjectWriteResponse
import reactor.core.publisher.Mono
import java.io.InputStream

interface MinioStorageService {
    fun save(file: InputStream, bucketName: String, objectName: String, contentType: String, size: Long): Mono<ObjectWriteResponse>
    fun load(bucketName: String, objectName: String): Mono<InputStream>
    fun generatePresignedUrl(bucketName: String, objectName: String): Mono<String>
}