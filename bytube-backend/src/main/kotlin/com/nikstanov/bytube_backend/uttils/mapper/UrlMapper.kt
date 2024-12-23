package com.nikstanov.bytube_backend.uttils.mapper

import org.mapstruct.Named
import org.springframework.stereotype.Component
import java.util.*

@Component
class UrlMapper {

    @Named("mapAvatarIdToUrl")
    fun mapAvatarIdToUrl(imageId: String?): String {
        return imageId?.let { "http://localhost:8080/images/avatars/$it.png" } ?: ""
    }

    @Named("mapPreviewIdToUrl")
    fun mapPreviewIdToUrl(previewId: UUID?): String {
        return previewId?.let { "http://localhost:8080/images/previews/$it" } ?: ""
    }

    @Named("mapUrlToPreviewId")
    fun mapUrlToPreviewId(thumbnailUrl: String): UUID? {
        return thumbnailUrl.split("/").lastOrNull()?.let { UUID.fromString(it) }
    }

}