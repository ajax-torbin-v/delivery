package com.example.delivery.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.redis")
data class RedisProperties(
    val host: String,
    val port: Int,
    val timeout: Long,
    val database: Int,
)
