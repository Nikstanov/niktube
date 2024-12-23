package com.nikstanov.bytube_backend.security

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

class LocalServerSecurityContextRepository : ServerSecurityContextRepository {

    private val contextStore = ConcurrentHashMap<String, SecurityContext>()

    override fun save(exchange: ServerWebExchange, context: SecurityContext?): Mono<Void> {
        val sessionId = getSessionId(exchange)
        if (sessionId != null && context != null) {
            contextStore[sessionId] = context
        }
        return Mono.empty()
    }

    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val sessionId = getSessionId(exchange)
        return if (sessionId != null) {
            Mono.justOrEmpty(contextStore[sessionId])
        } else {
            Mono.empty()
        }
    }

    private fun getSessionId(exchange: ServerWebExchange): String? {
        // Example: Use a cookie or header to identify the session.
        return exchange.request.cookies["SESSION"]?.firstOrNull()?.value
    }
}
