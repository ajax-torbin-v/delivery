package com.example.delivery

import com.example.delivery.kafka.KafkaTest.Companion.NOTIFICATION_GROUP
import com.example.delivery.kafka.configuration.KafkaConfiguration
import com.example.internal.api.KafkaTopic
import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import reactor.kafka.receiver.KafkaReceiver
import java.time.Duration

@SpringBootTest
@ActiveProfiles("test")
@Import(AbstractIntegrationTest.MyKafkaTestConfiguration::class)
abstract class AbstractIntegrationTest {
    @Autowired
    private lateinit var natsConnection: Connection

    fun <RequestT : GeneratedMessage, ResponseT : GeneratedMessage> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = natsConnection.requestWithTimeout(
            subject,
            payload.toByteArray(),
            Duration.ofSeconds(10)
        )
        return parser.parseFrom(response.get().data)
    }

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
