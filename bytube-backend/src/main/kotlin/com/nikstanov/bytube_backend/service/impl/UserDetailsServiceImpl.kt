package com.nikstanov.bytube_backend.service.impl

import com.nikstanov.bytube_backend.constant.ContentType
import com.nikstanov.bytube_backend.constant.MinioBuckets
import com.nikstanov.bytube_backend.dto.OwnerDto
import com.nikstanov.bytube_backend.dto.SignUpDto
import com.nikstanov.bytube_backend.entity.UserEntity
import com.nikstanov.bytube_backend.repository.UserRepository
import com.nikstanov.bytube_backend.security.PersonDetails
import com.nikstanov.bytube_backend.service.MinioStorageService
import com.nikstanov.bytube_backend.service.UserDetailsService
import com.nikstanov.bytube_backend.uttils.mapper.OwnerMapper
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.File
import java.util.*

@Service
class UserDetailsServiceImpl(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val ownerMapper: OwnerMapper,
    val minioStorageService: MinioStorageService
) : UserDetailsService {
    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository.findByEmail(username)
            .handle { user, sink ->
                if (user == null) {
                    sink.error(UsernameNotFoundException("User not found"))
                    return@handle
                }
                sink.next(PersonDetails(user))
            }
    }

    override fun getUserById(id: UUID): Mono<UserEntity> {
        return userRepository.findById(id)
    }

    override fun getUserByEmail(email: String): Mono<OwnerDto> {
        return userRepository.findByEmail(email)
            .flatMap { user ->
                if (user == null) Mono.error(Exception("User not exists"))
                else Mono.just(ownerMapper.toDto(user))
            }
    }

    override fun createUser(user: SignUpDto, provider: String): Mono<OwnerDto> {
        val userEntity = UserEntity(UUID.randomUUID(), user.username, passwordEncoder.encode(user.password), user.email, provider, null, null)
        return userRepository.save(userEntity).map {entity -> ownerMapper.toDto(entity)}
    }

    override fun createUserWithDetails(user: SignUpDto, provider: String): Mono<UserDetails> {
        val userEntity = UserEntity(UUID.randomUUID(), user.username, passwordEncoder.encode(user.password), user.email, provider, null, null)
        return userRepository.save(userEntity).map {entity -> PersonDetails(entity)}
    }

    override fun updateImage(image: FilePart, userId: UUID): Mono<OwnerDto> {
        return userRepository.findById(userId)
            .publishOn(Schedulers.boundedElastic())
            .flatMap { user ->
                val imageFile = File(System.getProperty("java.io.tmpdir"), image.filename())
                image.transferTo(imageFile).then(Mono.defer {
                    var imageId = UUID.randomUUID()
                    if (user.imageID != null) {
                        imageId = user.imageID
                    } else {
                        user.imageID = imageId
                    }
                    minioStorageService.save(
                        imageFile.inputStream(),
                        MinioBuckets.avatars.name,
                        "$imageId.png",
                        ContentType.IMAGE,
                        imageFile.length()
                    ).flatMap { userRepository.save(user).map { ownerMapper.toDto(user) } }
                })

            }
    }

    override fun updateName(name: String, userId: UUID): Mono<OwnerDto> {
        return userRepository.findById(userId)
            .flatMap { user ->
                user.username = name
                userRepository.save(user).map { ownerMapper.toDto(it) }
            }
    }
}
