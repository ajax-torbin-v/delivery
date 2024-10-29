package com.example.delivery.kafka

import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class OrderUpdateStatusProducer(
    private val kafkaUpdateStatusSender: KafkaSender<String, ByteArray>,
) {
    fun sendOrderUpdateStatus(response: UpdateOrderStatusResponse): Mono<Unit> {
        return kafkaUpdateStatusSender.send(
            SenderRecord.create(
                ProducerRecord(
                    "order_update_event",
                    response.success.order.userId,
                    response.toByteArray()
                ),
                null
            ).toMono()
        ).then(Unit.toMono())
    }
}
