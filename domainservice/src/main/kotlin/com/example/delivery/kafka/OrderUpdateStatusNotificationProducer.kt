package com.example.delivery.kafka

import com.example.internal.commonmodels.order.OrderStatusUpdateNotification
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
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
        log.info("Notification for user ${notification.userId}")
        return updateStatusNotificationSender.send(
            SenderRecord.create(
                ProducerRecord(
                    "notifications",
                    notification.userId,
                    notification.toByteArray()
                ),
                null
            ).toMono()
        ).then(Unit.toMono())
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderUpdateStatusNotificationProducer::class.java)
    }
}
