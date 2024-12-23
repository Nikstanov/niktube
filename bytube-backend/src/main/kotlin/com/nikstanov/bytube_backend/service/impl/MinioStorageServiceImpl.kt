package com.nikstanov.bytube_backend.service.impl

import com.nikstanov.bytube_backend.service.MinioStorageService
import io.minio.*
import io.minio.http.Method
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.io.InputStream


@Service
class MinioStorageServiceImpl(private val minioClient: MinioClient) : MinioStorageService {

    override fun save(file: InputStream, bucketName: String, objectName: String, contentType: String, size: Long): Mono<ObjectWriteResponse> {
        val result = minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectName)
                .contentType(contentType)
                .stream(file, size, -1)
                .build()
        )
        return result.toMono()
    }

    override fun load(bucketName: String, objectName: String): Mono<InputStream> {
        val result = minioClient.getObject(
            GetObjectArgs
                .builder()
                .bucket(bucketName)
                .`object`(objectName)
                .build()
        )
        return result.toMono()
    }

    override fun generatePresignedUrl(bucketName: String, objectName: String): Mono<String> {

        return Mono.just(
            minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectName)
                    .method(Method.GET)
                    .expiry(60 * 60 * 24) // 1 hour expiry
                    .build()
            )
        )
    }
}