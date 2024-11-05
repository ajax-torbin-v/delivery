package com.example.delivery.kafka

import com.example.commonmodels.order.OrderStatusUpdateNotification
import com.example.internal.api.KafkaTopic
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class OrderUpdateStatusNotificationProducer(
    private val updateStatusNotificationSender: KafkaSender<String, ByteArray>,
) {
    fun notify(notification: OrderStatusUpdateNotification): Mono<Unit> {
        return updateStatusNotificationSender.send(
            createMessage(notification)
        ).then(Unit.toMono())
    }

    private fun createMessage(notification: OrderStatusUpdateNotification) =
        SenderRecord.create(
            ProducerRecord(
                KafkaTopic.KafkaOrderStatusUpdateEvents.NOTIFICATIONS,
                notification.userId,
                notification.toByteArray()
            ),
            null
        ).toMono()
}
