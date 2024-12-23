package com.nikstanov.bytube_backend.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.*

@Table(value = "video")
class VideoEntity(
    @Id var id: UUID?,
    @Column(value = "name") var name: String,
    @Column(value = "description") var description: String,
    @Column(value = "owner_id") var ownerId: UUID,
    @Column(value = "preview_id") var previewId: UUID?,
    @Column(value = "open") var open: Boolean,
    @Column(value = "segments_count") var segmentsCount: Int,
    @Column(value = "created_at") @CreatedDate var created: LocalDate = LocalDate.now(),
    @Column(value = "views") var views: Int = 0
){
    @Transient var author: UserEntity? = null
}