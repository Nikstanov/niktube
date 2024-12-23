package com.nikstanov.bytube_backend.config

import com.fasterxml.jackson.databind.type.TypeFactory
import com.nikstanov.bytube_backend.entity.PasswordResetToken
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisConfiguration {

    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, PasswordResetToken> {
        val keySerializer = StringRedisSerializer()
        val valueSerializer: Jackson2JsonRedisSerializer<PasswordResetToken> = Jackson2JsonRedisSerializer(PasswordResetToken::class.java)
        val builder: RedisSerializationContextBuilder<String, PasswordResetToken> = RedisSerializationContext.newSerializationContext(keySerializer)
        val context: RedisSerializationContext<String, PasswordResetToken> = builder.value(valueSerializer).build()
        return ReactiveRedisTemplate(factory, context)
    }

    @Bean(name = ["resetPasswordSessionsOps"])
    fun reactiveRedisOps(template: ReactiveRedisTemplate<String, PasswordResetToken>): ReactiveValueOperations<String, PasswordResetToken> {
        return template.opsForValue()
    }

    @Bean
    fun videoSessionTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Int> {
        val keySerializer = StringRedisSerializer()
        val valueSerializer: Jackson2JsonRedisSerializer<Int> = Jackson2JsonRedisSerializer(Int::class.java)
        val builder: RedisSerializationContextBuilder<String, Int> = RedisSerializationContext.newSerializationContext(keySerializer)
        val context: RedisSerializationContext<String, Int> = builder.value(valueSerializer).build()
        return ReactiveRedisTemplate(factory, context)
    }

    @Bean(name = ["videoSessionsOps"])
    fun videoSessionOps(template: ReactiveRedisTemplate<String, Int>): ReactiveValueOperations<String, Int> {
        return template.opsForValue()
    }
}