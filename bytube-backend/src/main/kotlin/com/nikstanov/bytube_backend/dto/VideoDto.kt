package com.nikstanov.bytube_backend.dto

import java.time.LocalDate
import java.util.*

data class VideoDto(
    var videoId: UUID?,
    var title: String,
    var description: String,
    var owner:OwnerDto,
    var thumbnail: String,
    var open: Boolean,
    var created: LocalDate,
    var views: Int
)
