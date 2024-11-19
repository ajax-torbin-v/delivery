package com.example.domainservice.order.infrastructure.nats

import com.example.domainservice.order.application.port.input.OrderInputPort
import com.example.domainservice.order.infrastructure.nats.mapper.OrderProtoMapper.toCreateOrderResponse
import com.example.domainservice.order.infrastructure.nats.mapper.OrderProtoMapper.toDomain
import com.example.domainservice.order.infrastructure.nats.mapper.OrderProtoMapper.toFailureCreateOrderResponse
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.order.CreateOrderRequest
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class CreateOrderNatsHandler(
    private val orderInputPort: OrderInputPort,
) : ProtoNatsMessageHandler<CreateOrderRequest, CreateOrderResponse> {
    override val log: Logger = LoggerFactory.getLogger(CreateOrderNatsHandler::class.java)
    override val parser: Parser<CreateOrderRequest> = CreateOrderRequest.parser()
    override val queue: String = ORDER_QUEUE_GROUP
    override val subject: String = NatsSubject.Order.SAVE

    override fun doOnUnexpectedError(inMsg: CreateOrderRequest?, e: Exception): Mono<CreateOrderResponse> {
        return CreateOrderResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: CreateOrderRequest): Mono<CreateOrderResponse> {
        return orderInputPort.save(inMsg.toDomain())
            .map { it.toCreateOrderResponse() }
            .onErrorResume { error ->
                log.error("Error while executing save for order {}", inMsg, error)
                error.toFailureCreateOrderResponse().toMono()
            }
    }

    companion object {
        private const val ORDER_QUEUE_GROUP = "orderQueueGroup"
    }
}
