package com.example.delivery.config

import io.lettuce.core.ClientOptions
import io.lettuce.core.TimeoutOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
class RedisConfig(
    private val redisProperties: RedisProperties,
) {
    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        val config = RedisStandaloneConfiguration(redisProperties.host, redisProperties.port)
        config.database = redisProperties.database

        val clientOptions: ClientOptions = ClientOptions.builder()
            .timeoutOptions(
                TimeoutOptions.builder()
                    .timeoutCommands(true)
                    .fixedTimeout(Duration.ofMillis(redisProperties.timeout))
                    .build()
            ).build()

        return LettuceConnectionFactory(
            config,
            LettuceClientConfiguration.builder().clientOptions(clientOptions).build()
        )
    }

    @Bean
    fun reactiveRedisTemplate(
        reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, ByteArray> {
        val context = RedisSerializationContext
            .newSerializationContext<String, ByteArray>()
            .key(StringRedisSerializer())
            .value(RedisSerializer.byteArray())
            .hashKey(StringRedisSerializer())
            .hashValue(RedisSerializer.byteArray())
            .build()
        return ReactiveRedisTemplate(reactiveRedisConnectionFactory, context)
    }
}
