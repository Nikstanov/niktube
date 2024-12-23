package com.nikstanov.bytube_backend.dto

data class OwnerDto(
    val userId: String,
    val nickname: String,
    val email: String,
    val avatarUrl: String?
)
