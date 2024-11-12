package com.example.delivery.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class RedisProperties(
    @Value("\${spring.redis.host}")
    val host: String,
    @Value("\${spring.redis.port}")
    val port: Int,
    @Value("\${spring.redis.timeout}")
    val timeout: Long,
    @Value("\${spring.redis.database}")
    val database: Int,
)
