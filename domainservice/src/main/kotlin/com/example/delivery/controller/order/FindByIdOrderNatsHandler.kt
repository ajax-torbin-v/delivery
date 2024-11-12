package com.example.delivery.controller.order

import com.example.core.exception.OrderNotFoundException
import com.example.delivery.mapper.OrderProtoMapper.toFailureFindOrderByIdResponse
import com.example.delivery.mapper.OrderProtoMapper.toFindOrderByIdResponse
import com.example.delivery.service.OrderService
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.order.FindOrderByIdRequest
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class FindByIdOrderNatsHandler(
    private val orderService: OrderService,
) : ProtoNatsMessageHandler<FindOrderByIdRequest, FindOrderByIdResponse> {
    override val log: Logger = LoggerFactory.getLogger(FindByIdOrderNatsHandler::class.java)
    override val parser: Parser<FindOrderByIdRequest> = FindOrderByIdRequest.parser()
    override val queue: String = "order_group"
    override val subject: String = NatsSubject.Order.FIND_BY_ID

    override fun doOnUnexpectedError(inMsg: FindOrderByIdRequest?, e: Exception): Mono<FindOrderByIdResponse> {
        log.error("Error while executing findById for {}", inMsg, e)
        return FindOrderByIdResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: FindOrderByIdRequest): Mono<FindOrderByIdResponse> {
        return orderService.getById(inMsg.id)
            .map { it.toFindOrderByIdResponse() }
            .onErrorResume(isExpectedException) { error ->
                log.error("Error while executing findById for {}", inMsg, error)
                error.toFailureFindOrderByIdResponse().toMono()
            }
    }

    private val isExpectedException: (Throwable) -> Boolean = { it::class in setOf(OrderNotFoundException::class) }
}
