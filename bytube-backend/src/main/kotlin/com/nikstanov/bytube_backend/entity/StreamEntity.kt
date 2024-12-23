package com.nikstanov.bytube_backend.entity

import com.nikstanov.bytube_backend.constant.StreamStatus
import lombok.Data
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Data
@Table(name = "stream")
class StreamEntity(
    @Id var id: UUID?,
    @Column(value = "name") var name: String,
    @Column(value = "status") var status: StreamStatus,
    @Column(value = "stream_key") var streamKey: UUID,
    @Column(value = "owner_id") var ownerId: UUID,
    @Column(value = "start_time") var startTime: LocalDateTime,
    @Column(value = "end_time") var endTime: LocalDateTime,
){
    @Transient var author: UserEntity? = null
}