package com.example.delivery.controller.order

import com.example.core.exception.ProductAmountException
import com.example.core.exception.ProductNotFoundException
import com.example.core.exception.UserNotFoundException
import com.example.delivery.mapper.OrderProtoMapper.toCreateOrderDTO
import com.example.delivery.mapper.OrderProtoMapper.toCreateOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureCreateOrderResponse
import com.example.delivery.service.OrderService
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
    private val orderService: OrderService,
) : ProtoNatsMessageHandler<CreateOrderRequest, CreateOrderResponse> {
    override val log: Logger = LoggerFactory.getLogger(CreateOrderNatsHandler::class.java)
    override val parser: Parser<CreateOrderRequest> = CreateOrderRequest.parser()
    override val queue: String = "order_group"
    override val subject: String = NatsSubject.Order.SAVE
    override fun doOnUnexpectedError(inMsg: CreateOrderRequest?, e: Exception): Mono<CreateOrderResponse> {
        log.error("Error while executing save for order {}", inMsg, e)
        return CreateOrderResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: CreateOrderRequest): Mono<CreateOrderResponse> {
        return orderService.add(inMsg.toCreateOrderDTO())
            .map { it.toCreateOrderResponse() }
            .onErrorResume(isExpectedError) { error ->
                log.error("Error while executing save for order {}", inMsg, error)
                error.toFailureCreateOrderResponse().toMono()
            }
    }

    private val isExpectedError: (Throwable) -> Boolean =
        {
            it::class in setOf(
                UserNotFoundException::class,
                ProductNotFoundException::class,
                ProductAmountException::class
            )
        }
}
