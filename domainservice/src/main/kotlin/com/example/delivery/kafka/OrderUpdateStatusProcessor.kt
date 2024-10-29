package com.example.delivery.kafka

import com.example.core.exception.NotificationException
import com.example.delivery.mapper.OrderProtoMapper.toNotification
import com.example.internal.commonmodels.order.Order
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverRecord
import reactor.kotlin.core.publisher.toMono

@Component
class OrderUpdateStatusProcessor(
    private val orderStatusUpdateReceiver: KafkaReceiver<String, ByteArray>,
    private val updateStatusNotificationSender: OrderUpdateStatusNotificationProducer,
) {
    @EventListener(ApplicationReadyEvent::class)
    fun consume() {
        orderStatusUpdateReceiver
            .receive()
            .flatMap {
                val order = UpdateOrderStatusResponse.parser().parseFrom(it.value()).success.order
                sendNotification(order, it)
            }
            .subscribe()
    }

    private fun sendNotification(
        order: Order,
        record: ReceiverRecord<String, ByteArray>,
    ): Mono<ReceiverRecord<String, ByteArray>> =
        updateStatusNotificationSender.notify(order.toNotification())
            .onErrorResume(NotificationException::class.java) { error ->
                log.error("Error while sending notification: ", error)
                Unit.toMono()
            }
            .doFinally {
                record.receiverOffset().acknowledge()
            }.thenReturn(record)

    companion object {
        private val log = LoggerFactory.getLogger(OrderUpdateStatusProcessor::class.java)
    }
}
