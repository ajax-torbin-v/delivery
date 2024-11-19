package com.example.domainservice

import com.example.domainservice.core.config.RedisProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(RedisProperties::class)
class DeliveryApplication

@SuppressWarnings("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<DeliveryApplication>(*args)
}