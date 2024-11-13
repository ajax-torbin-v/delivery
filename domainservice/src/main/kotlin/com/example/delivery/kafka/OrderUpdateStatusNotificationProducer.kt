package com.example.delivery.kafka

import com.example.commonmodels.order.OrderStatusUpdateNotification
import com.example.internal.api.KafkaTopic
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
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
        ).then(Unit.toMono())
    }
}
