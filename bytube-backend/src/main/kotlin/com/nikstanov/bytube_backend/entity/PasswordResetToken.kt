package com.nikstanov.bytube_backend.entity

import java.util.UUID

data class PasswordResetToken(
    val token: String,
    val userId: UUID
) {
    constructor() : this("", UUID.randomUUID())
}