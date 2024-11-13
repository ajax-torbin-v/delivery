package com.example.delivery.controller.order

import com.example.delivery.mapper.OrderProtoMapper.toFailureFindOrdersByUserIdResponse
import com.example.delivery.mapper.OrderProtoMapper.toFindOrdersByUserIdResponse
import com.example.delivery.service.OrderService
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.order.FindOrdersByUserIdRequest
import com.example.internal.input.reqreply.order.FindOrdersByUserIdResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class FindAllByUserIdOrderNatsHandler(
    private val orderService: OrderService,
) :
    ProtoNatsMessageHandler<FindOrdersByUserIdRequest, FindOrdersByUserIdResponse> {
    override val log: Logger = LoggerFactory.getLogger(FindAllByUserIdOrderNatsHandler::class.java)
    override val parser: Parser<FindOrdersByUserIdRequest> = FindOrdersByUserIdRequest.parser()
    override val queue: String = ORDER_QUEUE_GROUP
    override val subject: String = NatsSubject.Order.FIND_ALL_BY_USER_ID

    override fun doOnUnexpectedError(
        inMsg: FindOrdersByUserIdRequest?,
        e: Exception,
    ): Mono<FindOrdersByUserIdResponse> {
        return FindOrdersByUserIdResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: FindOrdersByUserIdRequest): Mono<FindOrdersByUserIdResponse> {
        return orderService.getAllByUserId(inMsg.id)
            .collectList()
            .map { orders -> toFindOrdersByUserIdResponse(orders) }
            .onErrorResume { error ->
                log.error("Error while executing findAll for {}", inMsg, error)
                error.toFailureFindOrdersByUserIdResponse().toMono()
            }
    }

    companion object {
        private const val ORDER_QUEUE_GROUP = "orderQueueGroup"
    }
}
