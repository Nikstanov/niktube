package com.nikstanov.bytube_backend.repository

import com.nikstanov.bytube_backend.entity.VideoEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.*

@Repository
interface VideoRepository: R2dbcRepository<VideoEntity, UUID>{
    fun findByOpen(open: Boolean, pageable: Pageable): Flux<VideoEntity>
    fun findByOpenAndName(open: Boolean, name: String): Flux<VideoEntity>
    fun findByOwnerId(ownerId: UUID, pageable: Pageable): Flux<VideoEntity>
    fun findByOwnerIdAndOpen(ownerId: UUID, open: Boolean, pageable: Pageable): Flux<VideoEntity>
}