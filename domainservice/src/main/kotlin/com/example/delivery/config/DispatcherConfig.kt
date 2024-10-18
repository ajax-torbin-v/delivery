package com.example.delivery.config

import io.nats.client.Connection
import io.nats.client.Dispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DispatcherConfig(private val connection: Connection) {

    @Bean
    fun createDispatcher(): Dispatcher {
        return connection.createDispatcher()
    }
}
