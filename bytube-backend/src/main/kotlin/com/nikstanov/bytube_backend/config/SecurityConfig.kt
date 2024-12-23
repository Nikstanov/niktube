package com.nikstanov.bytube_backend.config

import com.nikstanov.bytube_backend.security.CustomSuccessHandler
import com.nikstanov.bytube_backend.security.LocalServerSecurityContextRepository
import com.nikstanov.bytube_backend.service.impl.UserDetailsServiceImpl
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun getSecurityContextRepository() : LocalServerSecurityContextRepository{
        return LocalServerSecurityContextRepository()
    }

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity, authenticationManager: ReactiveAuthenticationManager, successHandler: CustomSuccessHandler, localServerSecurityContextRepository: LocalServerSecurityContextRepository): SecurityWebFilterChain {
        return http
            .cors{c -> c.configurationSource(corsSource())}
            .authorizeExchange { exchanges ->
            exchanges
                .pathMatchers("/streams/secured/**").authenticated()
                .pathMatchers("/api/public").permitAll()
                .pathMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/auth/**",
                    "/video/**",
                    "/images/**",
                    "/streams/**",
                    "/users/public/**")
                .permitAll()
                .anyExchange().authenticated()
        }
            .securityContextRepository(localServerSecurityContextRepository)
            .httpBasic { auth ->
                auth.authenticationManager(authenticationManager)
            }
            .oauth2Login { oauth2 ->
                oauth2.authenticationSuccessHandler(successHandler)
            }
            .csrf{customizer -> customizer.disable()}
            .logout{logout ->
                logout.logoutUrl("/auth/logout")
            }
            .build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(passwordEncoder: PasswordEncoder?, userDetailsService: UserDetailsServiceImpl): ReactiveAuthenticationManager {
        val authManager =
            UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService)
        authManager.setPasswordEncoder(passwordEncoder)
        return authManager
    }

    @Bean
    fun corsSource() : CorsConfigurationSource {
        val corsConfig = CorsConfiguration()
        corsConfig.allowedOrigins = listOf("http://localhost:3000")
        corsConfig.maxAge = 8000L
        corsConfig.addAllowedMethod("PUT")
        corsConfig.addAllowedMethod("GET")
        corsConfig.addAllowedMethod("POST")
        corsConfig.addAllowedMethod("DELETE")
        corsConfig.addAllowedMethod("PATCH")
        corsConfig.addAllowedMethod("OPTIONS")
        corsConfig.addAllowedHeader("*")
        corsConfig.allowCredentials = true
        val source =
            UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)

        return source
    }
}