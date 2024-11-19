package com.example.domainservice.core.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.redis")
class RedisProperties {
    lateinit var host: String
    var port: Int = 0
    var timeout: Long = 0
    var database: Int = 0
}
