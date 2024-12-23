package com.nikstanov.bytube_backend.contoroller

import com.nikstanov.bytube_backend.dto.OwnerDto
import com.nikstanov.bytube_backend.security.PersonDetails
import com.nikstanov.bytube_backend.service.UserDetailsService
import com.nikstanov.bytube_backend.service.VideoService
import com.nikstanov.bytube_backend.uttils.mapper.OwnerMapper
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(val videoService: VideoService, val userDetailsService: UserDetailsService, val ownerMapper: OwnerMapper) {

    @PostMapping("/upload", consumes = [MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestPart("file", required = true) videoFile: FilePart,
        @RequestPart("preview", required = true) previewFile: FilePart,
        @RequestPart("name", required = true) name: String,
        @RequestPart("description", required = true) description: String,
        @RequestParam("open", required = true) open: Boolean,
        authentication: Authentication
    ): Mono<UUID> {
        val details = if(authentication.details is PersonDetails) {
            authentication.details as PersonDetails
        }else{
            authentication.principal as PersonDetails
        }
        return videoService.convertAndUploadVideo(videoFile, previewFile, details.user.id , name, description, open)
    }

    @GetMapping("/public/{userId}")
    fun getUserInfo(@PathVariable userId: String): Mono<OwnerDto> {
        return userDetailsService.getUserById(UUID.fromString(userId)).map { user ->
            ownerMapper.toDto(user)
        }
    }

    @GetMapping("/currentUser")
    fun getCurrentUserInfo(authentication: Authentication): Mono<OwnerDto> {
        val details = if(authentication.details is PersonDetails) {
            authentication.details as PersonDetails
        }else{
            authentication.principal as PersonDetails
        }
        return userDetailsService.getUserById(details.user.id ).map { user ->
            ownerMapper.toDto(user)
        }
    }

    @PatchMapping("/image", consumes = [MULTIPART_FORM_DATA_VALUE])
    fun updateImage(@RequestPart("image", required = true) imageFile: FilePart, authentication: Authentication): Mono<OwnerDto> {
        val details = if(authentication.details is PersonDetails) {
            authentication.details as PersonDetails
        }else{
            authentication.principal as PersonDetails
        }
        return userDetailsService.updateImage(imageFile, details.user.id)
    }

    @PatchMapping("/nickname")
    fun updateNickname(@RequestParam("nickname", required = true) nickname: String, authentication: Authentication): Mono<OwnerDto> {
        val details = if(authentication.details is PersonDetails) {
            authentication.details as PersonDetails
        }else{
            authentication.principal as PersonDetails
        }
        return userDetailsService.updateName(nickname, details.user.id)
    }
}