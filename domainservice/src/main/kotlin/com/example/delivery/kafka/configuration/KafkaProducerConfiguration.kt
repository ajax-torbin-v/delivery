package com.example.delivery.kafka.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.sender.KafkaSender

@Configuration
class KafkaProducerConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}") bootstrapServers: String,
    kafkaProperties: KafkaProperties,
) : KafkaConfiguration(bootstrapServers, kafkaProperties) {
    @Bean
    fun kafkaUpdateStatusSender(): KafkaSender<String, ByteArray> {
        return createKafkaSender(baseProducerProperties())
    }

    @Bean
    fun updateStatusNotificationSender(): KafkaSender<String, ByteArray> {
        return createKafkaSender(baseProducerProperties())
    }
}
