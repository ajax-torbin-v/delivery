package com.example.delivery.kafka

import com.example.core.exception.NotificationException
import com.example.delivery.mapper.OrderProtoMapper.toNotification
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import org.slf4j.LoggerFactory
import org.springframework.context.event.ApplicationContextEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono

@Component
class OrderUpdateStatusProcessor(
    private val orderStatusUpdateReceiver: KafkaReceiver<String, ByteArray>,
    private val updateStatusNotificationSender: OrderUpdateStatusNotificationProducer,
) {
    @EventListener(ApplicationContextEvent::class)
    fun consume() {
        orderStatusUpdateReceiver
            .receive()
            .flatMap { record ->
                val order = UpdateOrderStatusResponse.parser().parseFrom(record.value()).success.order
                updateStatusNotificationSender.notify(order.toNotification())
                    .onErrorResume(NotificationException::class.java) { error ->
                        log.error("Error while sending notification: ", error)
                        Unit.toMono()
                    }
                    .doFinally {
                        record.receiverOffset().acknowledge()
                    }.thenReturn(record)
            }
            .subscribe()
    }

    companion object {
        private val log = LoggerFactory.getLogger(OrderUpdateStatusProcessor::class.java)
    }
}
