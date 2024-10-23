package com.example.delivery.controller

import com.example.delivery.annotaion.NatsController
import com.example.delivery.annotaion.NatsHandler
import com.example.delivery.controller.OrderNatsController.Companion.QUEUE_GROUP
import com.example.delivery.mapper.OrderProtoMapper.toCreateOrderDTO
import com.example.delivery.mapper.OrderProtoMapper.toCreateOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toDeleteOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureCreateOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureDeleteOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureFindOrderByIdResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureFindOrdersByUserIdResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureUpdateOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureUpdateStatusOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toFindOrderByIdResponse
import com.example.delivery.mapper.OrderProtoMapper.toFindOrdersByUserIdResponse
import com.example.delivery.mapper.OrderProtoMapper.toUpdateOrderDTO
import com.example.delivery.mapper.OrderProtoMapper.toUpdateOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toUpdateOrderStatusResponse
import com.example.delivery.service.OrderService
import com.example.internal.api.subject.NatsSubject
import com.example.internal.input.reqreply.order.CreateOrderRequest
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.DeleteOrderRequest
import com.example.internal.input.reqreply.order.DeleteOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdRequest
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.FindOrdersByUserIdRequest
import com.example.internal.input.reqreply.order.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.UpdateOrderRequest
import com.example.internal.input.reqreply.order.UpdateOrderResponse
import com.example.internal.input.reqreply.order.UpdateOrderStatusRequest
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import io.nats.client.Connection
import io.nats.client.Dispatcher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
@NatsController(queueGroup = QUEUE_GROUP)
class OrderNatsController(
    private val orderService: OrderService,
    connection: Connection,
    dispatcher: Dispatcher,
) : AbstractNatsController(connection, dispatcher) {

    @NatsHandler(subject = NatsSubject.Order.SAVE)
    fun add(request: CreateOrderRequest): Mono<CreateOrderResponse> {
        return orderService.add(request.toCreateOrderDTO())
            .map { it.toCreateOrderResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureCreateOrderResponse().toMono()
            }
    }

    @NatsHandler(subject = NatsSubject.Order.FIND_BY_ID)
    fun findById(request: FindOrderByIdRequest): Mono<FindOrderByIdResponse> {
        return orderService.getById(request.id)
            .map { it.toFindOrderByIdResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureFindOrderByIdResponse().toMono()
            }
    }

    @NatsHandler(subject = NatsSubject.Order.UPDATE)
    fun update(request: UpdateOrderRequest): Mono<UpdateOrderResponse> {
        return orderService.updateOrder(request.id, request.toUpdateOrderDTO())
            .map { it.toUpdateOrderResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureUpdateOrderResponse().toMono()
            }
    }

    @NatsHandler(subject = NatsSubject.Order.UPDATE_STATUS)
    fun updateStatus(request: UpdateOrderStatusRequest): Mono<UpdateOrderStatusResponse> {
        return orderService.updateOrderStatus(request.id, request.status)
            .map { it.toUpdateOrderStatusResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureUpdateStatusOrderResponse().toMono()
            }
    }

    @NatsHandler(subject = NatsSubject.Order.DELETE)
    fun delete(request: DeleteOrderRequest): Mono<DeleteOrderResponse> {
        return orderService.deleteById(request.id)
            .map { toDeleteOrderResponse() }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureDeleteOrderResponse().toMono()
            }
    }

    @NatsHandler(subject = NatsSubject.Order.FIND_ALL_BY_USER_ID)
    fun findAllByUserId(request: FindOrdersByUserIdRequest): Mono<FindOrdersByUserIdResponse> {
        return orderService.getAllByUserId(request.id)
            .collectList()
            .map { orders -> toFindOrdersByUserIdResponse(orders) }
            .onErrorResume { error ->
                log.error("Error while executing", error)
                error.toFailureFindOrdersByUserIdResponse().toMono()
            }
    }

    companion object {
        const val QUEUE_GROUP = "order_group"
        private val log = LoggerFactory.getLogger(OrderNatsController::class.java)
    }
}
