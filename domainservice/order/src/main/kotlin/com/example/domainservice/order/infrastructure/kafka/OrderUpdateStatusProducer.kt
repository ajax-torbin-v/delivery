package com.example.domainservice.order.infrastructure.kafka

import com.example.domainservice.order.application.port.output.OrderUpdateStatusProducerOutputPort
import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.infrastructure.kafka.mapper.OrderMapper.toUpdateOrderStatusResponse
import com.example.internal.api.KafkaTopic
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.kafka.publisher.KafkaPublisher

@Component
class OrderUpdateStatusProducer(
    private val kafkaPublisher: KafkaPublisher,
) : OrderUpdateStatusProducerOutputPort {
    override fun sendOrderUpdateStatus(msg: DomainOrder): Mono<Unit> {
        val updateOrderResponse = msg.toUpdateOrderStatusResponse()
        return kafkaPublisher.publish(
            KafkaTopic.KafkaOrderStatusUpdateEvents.UPDATE,
            updateOrderResponse.success.order.userId,
            updateOrderResponse.toByteArray()
        ).thenReturn(Unit)
    }
}
