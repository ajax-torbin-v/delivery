package com.example.delivery.controller.order

import com.example.delivery.mapper.OrderProtoMapper.toDeleteOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureDeleteOrderResponse
import com.example.delivery.service.OrderService
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.order.DeleteOrderRequest
import com.example.internal.input.reqreply.order.DeleteOrderResponse
import com.google.protobuf.Parser
import kotlin.reflect.KClass
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class DeleteOrderNatsHandler(
    private val orderService: OrderService,
) : ProtoNatsMessageHandler<DeleteOrderRequest, DeleteOrderResponse> {
    override val log: Logger = LoggerFactory.getLogger(DeleteOrderNatsHandler::class.java)
    override val parser: Parser<DeleteOrderRequest> = DeleteOrderRequest.parser()
    override val queue: String = "order_group"
    override val subject: String = NatsSubject.Order.DELETE

    override fun doOnUnexpectedError(inMsg: DeleteOrderRequest?, e: Exception): Mono<DeleteOrderResponse> {
        log.error("Error while executing delete for {}", inMsg, e)
        return DeleteOrderResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: DeleteOrderRequest): Mono<DeleteOrderResponse> {
        return orderService.deleteById(inMsg.id)
            .map { toDeleteOrderResponse() }
            .onErrorResume(isExpectedError) { error ->
                log.error("Error while executing delete for {}", inMsg, error)
                error.toFailureDeleteOrderResponse().toMono()
            }
    }

    private val isExpectedError: (Throwable) -> Boolean = {
        it::class in setOf<KClass<Throwable>>()
    }
}
