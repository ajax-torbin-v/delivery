package com.example.domainservice.order.infrastructure.kafka

import com.example.commonmodels.order.OrderStatusUpdateNotification
import com.example.internal.api.KafkaTopic
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.kafka.publisher.KafkaPublisher

@Component
class OrderUpdateStatusNotificationProducer(
    private val kafkaPublisher: KafkaPublisher,
) {
    fun notify(notification: OrderStatusUpdateNotification): Mono<Unit> {
        return kafkaPublisher.publish(
            KafkaTopic.KafkaOrderStatusUpdateEvents.NOTIFICATIONS,
            notification.userId,
            notification.toByteArray()
        ).thenReturn(Unit)
    }
}
