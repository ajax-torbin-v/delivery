package com.example.delivery.controller.order

import com.example.delivery.mapper.OrderProtoMapper.toFailureUpdateStatusOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toUpdateOrderStatusResponse
import com.example.delivery.service.OrderService
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.order.UpdateOrderStatusRequest
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import com.google.protobuf.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
internal class UpdateOrderStatusNatsHandler(
    private val orderService: OrderService,
) :
    ProtoNatsMessageHandler<UpdateOrderStatusRequest, UpdateOrderStatusResponse> {
    override val log: Logger = LoggerFactory.getLogger(UpdateOrderStatusNatsHandler::class.java)
    override val parser: Parser<UpdateOrderStatusRequest> = UpdateOrderStatusRequest.parser()
    override val queue: String = ORDER_QUEUE_GROUP
    override val subject: String = NatsSubject.Order.UPDATE_STATUS

    override fun doOnUnexpectedError(inMsg: UpdateOrderStatusRequest?, e: Exception): Mono<UpdateOrderStatusResponse> {
        return UpdateOrderStatusResponse.newBuilder().apply {
            failureBuilder.message = e.message.orEmpty()
        }.build().toMono()
    }

    override fun doHandle(inMsg: UpdateOrderStatusRequest): Mono<UpdateOrderStatusResponse> {
        return orderService.updateOrderStatus(inMsg.id, inMsg.status)
            .map { it.toUpdateOrderStatusResponse() }
            .onErrorResume { error ->
                log.error("Error while executing update for {}", inMsg, error)
                error.toFailureUpdateStatusOrderResponse().toMono()
            }
    }

    companion object {
        private const val ORDER_QUEUE_GROUP = "orderQueueGroup"
    }
}
