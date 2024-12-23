package com.nikstanov.bytube_backend.config

import com.nikstanov.bytube_backend.constant.MinioBuckets
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfig{

    @Value("\${minio.url}")
    private val minioUrl: String? = null

    @Value("\${minio.username}")
    private val minioUsername: String? = null

    @Value("\${minio.password}")
    private val minioPassword: String? = null

    @Bean
    fun minio() : MinioClient{
        val minioClient = MinioClient.builder()
            .endpoint(minioUrl)
            .credentials(minioUsername, minioPassword)
            .build()
        for (bucketName in MinioBuckets.entries.map { it.name }) {
            if(!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
                )
            }
        }
        return minioClient
    }
}