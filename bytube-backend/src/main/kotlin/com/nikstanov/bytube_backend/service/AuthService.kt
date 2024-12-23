package com.nikstanov.bytube_backend.service

import reactor.core.publisher.Mono

interface AuthService {
    fun requestResetPassword(email: String, frontEndResetUrl: String): Mono<Void>
    fun resetPassword(token: String, newPassword: String): Mono<Void>
}