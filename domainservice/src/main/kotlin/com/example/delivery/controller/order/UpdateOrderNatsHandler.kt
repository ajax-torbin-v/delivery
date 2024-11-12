package com.example.delivery.controller.order

import com.example.core.exception.OrderNotFoundException
import com.example.delivery.mapper.OrderProtoMapper.toFailureUpdateOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toUpdateOrderDTO
import com.example.delivery.mapper.OrderProtoMapper.toUpdateOrderResponse
import com.example.delivery.service.OrderService
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
    private val orderService: OrderService,
) : ProtoNatsMessageHandler<UpdateOrderRequest, UpdateOrderResponse> {
    override val log: Logger = LoggerFactory.getLogger(UpdateOrderNatsHandler::class.java)
    override val parser: Parser<UpdateOrderRequest> = UpdateOrderRequest.parser()
    override val queue: String = "order_group"
    override val subject: String = NatsSubject.Order.UPDATE

    override fun doOnUnexpectedError(inMsg: UpdateOrderRequest?, e: Exception): Mono<UpdateOrderResponse> {
        log.error("Error while executing update for {}", inMsg, e)
        return UpdateOrderResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: UpdateOrderRequest): Mono<UpdateOrderResponse> {
        return orderService.updateOrder(inMsg.id, inMsg.toUpdateOrderDTO())
            .map { it.toUpdateOrderResponse() }
            .onErrorResume(isExpectedException) { error ->
                log.error("Error while executing update for {}", inMsg, error)
                error.toFailureUpdateOrderResponse().toMono()
            }
    }

    private val isExpectedException: (Throwable) -> Boolean = {
        it::class in setOf(OrderNotFoundException::class)
    }
}
