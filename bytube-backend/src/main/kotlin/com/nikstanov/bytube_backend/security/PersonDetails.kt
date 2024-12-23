package com.nikstanov.bytube_backend.security

import com.nikstanov.bytube_backend.entity.UserEntity
import lombok.Getter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Getter
class PersonDetails(val user: UserEntity) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf()
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.email
    }
}