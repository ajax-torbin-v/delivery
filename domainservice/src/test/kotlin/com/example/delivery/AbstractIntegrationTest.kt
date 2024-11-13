package com.example.delivery

import com.example.delivery.kafka.KafkaTest.Companion.NOTIFICATION_GROUP
import com.example.delivery.kafka.configuration.KafkaConfiguration
import com.example.internal.api.KafkaTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import reactor.kafka.receiver.KafkaReceiver

@SpringBootTest
@ActiveProfiles("test")
@Import(AbstractIntegrationTest.MyKafkaTestConfiguration::class)
abstract class AbstractIntegrationTest {
    class MyKafkaTestConfiguration(
        @Value("\${spring.kafka.bootstrap-servers}") val bootstrapServer: String,
        kafkaProperties: KafkaProperties,
    ) : KafkaConfiguration(
        bootstrapServer,
        kafkaProperties
    ) {
        @Bean
        fun kafkaTestReceiver(): KafkaReceiver<String, ByteArray> {
            return createKafkaReceiver(
                baseConsumerProperties(),
                KafkaTopic.KafkaOrderStatusUpdateEvents.NOTIFICATIONS,
                NOTIFICATION_GROUP
            )
        }
    }
}
