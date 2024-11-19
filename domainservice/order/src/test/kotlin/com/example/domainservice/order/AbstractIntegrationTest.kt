package com.example.domainservice.order

import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import systems.ajax.kafka.autoconfiguration.handler.KafkaHandlerAutoConfiguration
import systems.ajax.kafka.autoconfiguration.publisher.KafkaPublisherAutoConfiguration
import systems.ajax.kafka.handler.notifier.KafkaGlobalExceptionHandler
import systems.ajax.nats.configuration.NatsAutoConfiguration

@Import(AbstractIntegrationTest.MyKafkaTestConfiguration::class)
@ContextConfiguration(
    classes = [
        KafkaAutoConfiguration::class,
        NatsAutoConfiguration::class,
        MongoReactiveAutoConfiguration::class,
        MongoReactiveDataAutoConfiguration::class,
        MongoDataAutoConfiguration::class,
        MongoAutoConfiguration::class,
        RedisReactiveAutoConfiguration::class,
        KafkaPublisherAutoConfiguration::class,
        KafkaHandlerAutoConfiguration::class,
        KafkaGlobalExceptionHandler::class,
    ]
)
@ComponentScan(
    "com.example.domainservice.order",
    "com.example.domainservice.product",
    "com.example.domainservice.user",
    "com.example.domainservice.core",
)
@SpringBootTest
@ActiveProfiles("test")
abstract class AbstractIntegrationTest {
    class MyKafkaTestConfiguration {
        @Bean
        fun adminClient(kafkaAdmin: KafkaAdmin): Admin =
            KafkaAdminClient.create(kafkaAdmin.configurationProperties)

        @Bean
        fun consumerFactory(kafkaProperties: KafkaProperties): ConsumerFactory<String, ByteArray> =
            DefaultKafkaConsumerFactory(
                kafkaProperties.buildConsumerProperties(null),
                StringDeserializer(),
                ByteArrayDeserializer()
            )
    }
}
