package com.nikstanov.bytube_backend.uttils.mapper

import com.nikstanov.bytube_backend.dto.OwnerDto
import com.nikstanov.bytube_backend.entity.UserEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", uses = [UrlMapper::class])
interface OwnerMapper {

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "username", target = "nickname")
    @Mapping(source = "imageID", target = "avatarUrl", qualifiedByName = ["mapAvatarIdToUrl"])
    fun toDto(owner: UserEntity): OwnerDto
}
