package com.nikstanov.bytube_backend.repository

import com.nikstanov.bytube_backend.entity.UserEntity
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
interface UserRepository : R2dbcRepository<UserEntity, UUID> {
    fun findByUsername(username: String): Mono<UserEntity?>
    fun findByEmail(email: String): Mono<UserEntity?>
}