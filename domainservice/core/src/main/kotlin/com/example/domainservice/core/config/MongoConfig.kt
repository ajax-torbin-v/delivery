package com.example.domainservice.core.config

import org.springframework.boot.autoconfigure.domain.EntityScanner
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.MongoManagedTypes
import org.springframework.data.mongodb.core.mapping.Document

@Configuration
class MongoConfig {
    @Bean
    @Primary
    fun mongoManagedTypes(applicationContext: ApplicationContext): MongoManagedTypes {
        return MongoManagedTypes.fromIterable(EntityScanner(applicationContext).scan(Document::class.java))
    }
}
