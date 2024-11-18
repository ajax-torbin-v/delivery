package com.example.delivery.kafka

import com.example.internal.api.KafkaTopic
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.kafka.publisher.KafkaPublisher

@Component
class OrderUpdateStatusProducer(
    private val kafkaPublisher: KafkaPublisher,
) {
    fun sendOrderUpdateStatus(response: UpdateOrderStatusResponse): Mono<Unit> {
        return kafkaPublisher.publish(
            KafkaTopic.KafkaOrderStatusUpdateEvents.UPDATE,
            response.success.order.userId,
            response.toByteArray()
        ).thenReturn(Unit)
    }
}
