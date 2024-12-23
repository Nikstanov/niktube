package com.nikstanov.bytube_backend.contoroller

import com.nikstanov.bytube_backend.constant.ContentType
import com.nikstanov.bytube_backend.constant.MinioBuckets
import com.nikstanov.bytube_backend.service.MinioStorageService
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/images")
class ImageController(private val minioStorageService: MinioStorageService) {

    @GetMapping("/previews/{previewId}")
    fun getVideoPreview(
        @PathVariable previewId: String
    ): Mono<ResponseEntity<Flux<DataBuffer>>> {
        return minioStorageService.load(MinioBuckets.previews.name, previewId).map{ inputStream ->
            DataBufferUtils.readInputStream(
                { inputStream },
                DefaultDataBufferFactory(),
                4096
            )
        }.map { data ->
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.IMAGE)
                .body(data)
        }
    }

    @GetMapping("/avatars/{imageId}")
    fun getUserImage(
        @PathVariable imageId: String
    ): Mono<ResponseEntity<Flux<DataBuffer>>> {
        return minioStorageService.load(MinioBuckets.avatars.name, imageId).map{ inputStream ->
            DataBufferUtils.readInputStream(
                { inputStream },
                DefaultDataBufferFactory(),
                4096
            )
        }.map { data ->
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, ContentType.IMAGE)
                .body(data)
        }
    }

}