package com.nikstanov.bytube_backend.security

import com.nikstanov.bytube_backend.dto.SignUpDto
import com.nikstanov.bytube_backend.service.impl.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI

@Component
class CustomSuccessHandler(
    private val userDetailsService: UserDetailsServiceImpl,

) : ServerAuthenticationSuccessHandler {

    @Value("\${frontend.uri}")
    private val frontendAddr : String = ""

    override fun onAuthenticationSuccess(
        exchange: WebFilterExchange,
        authentication: Authentication?
    ): Mono<Void> {
        if (authentication is OAuth2AuthenticationToken) {
            val oauth2User = authentication.principal as DefaultOidcUser
            val email = oauth2User.getAttribute<String>("email")
            val name = oauth2User.getAttribute<String>("name")

            return userDetailsService.findByUsername(email!!)
            .switchIfEmpty(
                // If the user does not exist, save a new user
                userDetailsService.createUserWithDetails(SignUpDto(name ?: "DefaultName", "", email), "google")
            )
                .doOnError { error ->  println(error.message) }
                .flatMap { userDetails ->
                    authentication.details = userDetails
                    Mono.empty<Void>()
                }
                .then(redirectToFrontend(exchange))
        }

        return Mono.empty()
    }

    // Helper method for redirection
    private fun redirectToFrontend(exchange: WebFilterExchange): Mono<Void> {
        val serverWebExchange: ServerWebExchange = exchange.exchange
        val response = serverWebExchange.response
        response.statusCode = HttpStatus.FOUND
        response.headers.location = URI.create(frontendAddr)
        return response.setComplete()
    }

}