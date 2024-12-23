package com.nikstanov.bytube_backend.uttils.mapper

import com.nikstanov.bytube_backend.dto.VideoDto
import com.nikstanov.bytube_backend.entity.VideoEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", uses = [OwnerMapper::class, UrlMapper::class])
interface VideoMapper {

    @Mapping(source = "id", target = "videoId")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "previewId", target = "thumbnail", qualifiedByName = ["mapPreviewIdToUrl"])
    @Mapping(source = "author", target = "owner")
    fun toDto(videoEntity: VideoEntity): VideoDto
}
