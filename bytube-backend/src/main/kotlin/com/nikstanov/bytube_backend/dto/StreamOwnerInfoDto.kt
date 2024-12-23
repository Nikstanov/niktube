package com.nikstanov.bytube_backend.dto

import java.util.*

data class StreamOwnerInfoDto(
    var id: UUID,
    var name: String,
    var streamKey: UUID,
)
