package com.example.delivery.kafka

import com.example.commonmodels.order.Order
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import io.nats.client.Connection
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import reactor.kotlin.core.publisher.toMono

@Component
class OrderUpdateStatusConsumerForNats(
    private val orderUpdateReceiverForNats: KafkaReceiver<String, ByteArray>,
    private val natsConnection: Connection,
) {
    @EventListener(ApplicationReadyEvent::class)
    fun consume() {
        orderUpdateReceiverForNats.receive()
            .flatMap { record ->
                sendUpdate(UpdateOrderStatusResponse.parser().parseFrom(record.value()).success.order)
                    .doFinally { record.receiverOffset().acknowledge() }
            }.subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun sendUpdate(order: Order): Mono<Unit> {
        return natsConnection.publish(
            NatsSubject.Order.getUpdateStatusByUserId(order.userId),
            order.toByteArray()
        ).toMono()
    }
}
