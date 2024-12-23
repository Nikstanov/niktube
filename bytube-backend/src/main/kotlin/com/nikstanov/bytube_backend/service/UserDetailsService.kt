package com.nikstanov.bytube_backend.service

import com.nikstanov.bytube_backend.dto.OwnerDto
import com.nikstanov.bytube_backend.dto.SignUpDto
import com.nikstanov.bytube_backend.entity.UserEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono
import java.util.*

interface UserDetailsService: ReactiveUserDetailsService {
    fun getUserById(id: UUID): Mono<UserEntity>
    fun getUserByEmail(email: String): Mono<OwnerDto>
    fun createUser(user: SignUpDto, provider: String): Mono<OwnerDto>
    fun createUserWithDetails(user: SignUpDto, provider: String): Mono<UserDetails>
    fun updateImage(image: FilePart, userId: UUID):Mono<OwnerDto>
    fun updateName(name: String, userId: UUID):Mono<OwnerDto>
}