package com.example.domainservice.order.infrastructure.nats

import com.example.domainservice.order.application.port.input.OrderInputPort
import com.example.domainservice.order.infrastructure.nats.mapper.OrderProtoMapper.toDomain
import com.example.domainservice.order.infrastructure.nats.mapper.OrderProtoMapper.toFailureUpdateOrderResponse
import com.example.domainservice.order.infrastructure.nats.mapper.OrderProtoMapper.toUpdateOrderResponse
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.order.UpdateOrderRequest
import com.example.internal.input.reqreply.order.UpdateOrderResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class UpdateOrderNatsHandler(
    private val orderInputPort: OrderInputPort,
) : ProtoNatsMessageHandler<UpdateOrderRequest, UpdateOrderResponse> {
    override val log: Logger = LoggerFactory.getLogger(UpdateOrderNatsHandler::class.java)
    override val parser: Parser<UpdateOrderRequest> = UpdateOrderRequest.parser()
    override val queue: String = ORDER_QUEUE_GROUP
    override val subject: String = NatsSubject.Order.UPDATE

    override fun doOnUnexpectedError(inMsg: UpdateOrderRequest?, e: Exception): Mono<UpdateOrderResponse> {
        return UpdateOrderResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: UpdateOrderRequest): Mono<UpdateOrderResponse> {
        return orderInputPort.updateOrder(inMsg.toDomain())
            .map { it.toUpdateOrderResponse() }
            .onErrorResume { error ->
                log.error("Error while executing update for {}", inMsg, error)
                error.toFailureUpdateOrderResponse().toMono()
            }
    }

    companion object {
        private const val ORDER_QUEUE_GROUP = "orderQueueGroup"
    }
}
