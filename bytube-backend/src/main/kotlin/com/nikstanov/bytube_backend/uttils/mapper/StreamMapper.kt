package com.nikstanov.bytube_backend.uttils.mapper

import com.nikstanov.bytube_backend.dto.StreamDto
import com.nikstanov.bytube_backend.dto.StreamOwnerInfoDto
import com.nikstanov.bytube_backend.dto.VideoDto
import com.nikstanov.bytube_backend.entity.StreamEntity
import com.nikstanov.bytube_backend.entity.VideoEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", uses = [OwnerMapper::class])
interface StreamMapper {

    @Mapping(source = "author", target = "owner")
    fun toDto(streamEntity: StreamEntity): StreamDto

    fun toOwnerDto(streamEntity: StreamEntity): StreamOwnerInfoDto
}
