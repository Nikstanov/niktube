package com.nikstanov.bytube_backend.contoroller

import com.nikstanov.bytube_backend.dto.OwnerDto
import com.nikstanov.bytube_backend.dto.SignInDto
import com.nikstanov.bytube_backend.dto.SignUpDto
import com.nikstanov.bytube_backend.security.LocalServerSecurityContextRepository
import com.nikstanov.bytube_backend.service.AuthService
import com.nikstanov.bytube_backend.service.UserDetailsService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/auth")
class AuthController(
    private val userDetailsService: UserDetailsService,
    private val authService: AuthService,
    private val authenticationManager: ReactiveAuthenticationManager,
    private val serverSecurityContextRepository: ServerSecurityContextRepository
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signUp")
    fun signUp(@RequestBody authDto: SignUpDto) : Mono<OwnerDto> {
        return userDetailsService.createUser(authDto, "app")
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/signIn")
    fun signIn(@RequestBody authDto: SignInDto, exchange: ServerWebExchange) : Mono<OwnerDto> {
        return exchange.session.flatMap { session -> // Access or create the session
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(authDto.email, authDto.password)
            ).flatMap { auth ->
                SecurityContextHolder.getContext().authentication = auth
                val context = SecurityContextImpl(auth)
                // Save SecurityContext in the repository
                serverSecurityContextRepository.save(exchange, context).then(
                    Mono.defer {
                        // Optionally set session attributes
                        session.attributes["userEmail"] = authDto.email
                        session.attributes["sessionId"] = session.id // Store the session ID for reference

                        // Fetch user details
                        userDetailsService.getUserByEmail(authDto.email)
                    }
                )
            }
        }.onErrorResume { error ->
            when (error) {
                is BadCredentialsException -> Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"))
                is UsernameNotFoundException -> Mono.error(ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                else -> Mono.error(ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error"))
            }
        }
    }

    @PostMapping("/request-reset")
    fun requestResetPassword(@RequestParam email: String, @RequestParam frontEndResetUrl: String): Mono<Void> {
        return authService.requestResetPassword(email, frontEndResetUrl)
    }

    @PostMapping("/reset")
    fun resetPassword(@RequestParam token: String, @RequestParam newPassword: String): Mono<Void> {
        return authService.resetPassword(token, newPassword)
    }
}