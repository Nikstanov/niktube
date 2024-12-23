package com.nikstanov.bytube_backend.service.impl

import com.nikstanov.bytube_backend.entity.PasswordResetToken
import com.nikstanov.bytube_backend.repository.UserRepository
import com.nikstanov.bytube_backend.service.AuthService
import com.nikstanov.bytube_backend.service.EmailService
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Duration
import java.util.*

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val emailService: EmailService,
    private val passwordEncoder: PasswordEncoder,
    private val reactiveValueOps: ReactiveValueOperations<String, PasswordResetToken>,
) : AuthService {
    override fun requestResetPassword(email: String, frontEndResetUrl: String): Mono<Void> {
        return userRepository.findByEmail(email)
            .switchIfEmpty{Mono.empty()}
            .flatMap { user ->
                val token = UUID.randomUUID().toString()
                val resetToken = PasswordResetToken(token, user!!.id!!)
                reactiveValueOps.set(token,resetToken, Duration.ofMinutes(15))
                    .then(Mono.fromCallable {
                        val resetLink = "$frontEndResetUrl?token=$token"
                        emailService.sendEmailWithVerificationCode(user.email, resetLink)
                    })
            }.then()
    }

    override fun resetPassword(token: String, newPassword: String): Mono<Void> {
        return reactiveValueOps.getAndDelete(token)
            .flatMap<Void> { resetToken ->
                 userRepository.findById(resetToken.userId).flatMap { user ->
                    user.password = passwordEncoder.encode(newPassword)
                    userRepository.save(user).flatMap {
                            _ -> Mono.empty()
                    }
                 }
            }
            .doOnError { error ->  println(error.message) }

    }
}