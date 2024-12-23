package com.nikstanov.bytube_backend.dto

import java.time.LocalDateTime
import java.util.*

data class StreamDto(
    var id: UUID,
    var name: String,
    var owner: OwnerDto,
    var startTime: LocalDateTime,
)
