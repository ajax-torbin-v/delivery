package com.example.delivery.kafka.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver

@Configuration
class KafkaConsumerConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}") bootstrapServers: String,
    kafkaProperties: KafkaProperties,
) : KafkaConfiguration(bootstrapServers, kafkaProperties) {
    @Bean
    fun orderStatusUpdateReceiver(): KafkaReceiver<String, ByteArray> {
        return createKafkaReceiver(baseConsumerProperties(), "order_update_event", "order_update_group")
    }
}
