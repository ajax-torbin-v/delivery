package com.example.delivery.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MapperConfig {
    @Bean
    fun mapper(): ObjectMapper {
        return jacksonObjectMapper()
    }
}
