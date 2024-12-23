package com.nikstanov.bytube_backend.entity

import lombok.Data
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Data
@Table(value = "user_details")
class UserEntity(
    @Id var id: UUID,

    @Column(value = "username") var username: String,
    @Column(value = "password") var password: String,
    @Column(value = "email") var email: String,
    @Column(value = "provider") var provider: String = "app",
    @Column(value = "image_id") var imageID: UUID?,
    @Version var version: Long?
)