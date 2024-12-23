package com.nikstanov.bytube_backend.repository

import com.nikstanov.bytube_backend.constant.StreamStatus
import com.nikstanov.bytube_backend.entity.StreamEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface StreamRepository: R2dbcRepository<StreamEntity, UUID> {

    fun findByOwnerId(userId: UUID): Mono<StreamEntity>
    fun findByOwnerIdAndStatus(userId: UUID, status: StreamStatus): Flux<StreamEntity>
    fun findAllByStatus(status: StreamStatus, pageable: Pageable): Flux<StreamEntity>
    fun findByIdAndStreamKey(id: UUID, streamKey: UUID): Mono<StreamEntity>
    fun findByStreamKey(streamKey: UUID): Mono<StreamEntity>
}