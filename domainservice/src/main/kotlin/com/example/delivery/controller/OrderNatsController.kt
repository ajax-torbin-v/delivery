package com.example.delivery.controller

import com.example.delivery.annotaion.NatsController
import com.example.delivery.annotaion.NatsHandler
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
import com.example.internal.api.subject.OrdersNatsSubject
import com.example.internal.input.reqreply.order.create.CreateOrderRequest
import com.example.internal.input.reqreply.order.create.CreateOrderResponse
import com.example.internal.input.reqreply.order.delete.DeleteOrderRequest
import com.example.internal.input.reqreply.order.delete.DeleteOrderResponse
import com.example.internal.input.reqreply.order.find.FindOrderByIdRequest
import com.example.internal.input.reqreply.order.find.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.find_by_user_id.FindOrdersByUserIdRequest
import com.example.internal.input.reqreply.order.find_by_user_id.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.update.UpdateOrderRequest
import com.example.internal.input.reqreply.order.update.UpdateOrderResponse
import com.example.internal.input.reqreply.order.update_status.UpdateOrderStatusRequest
import com.example.internal.input.reqreply.order.update_status.UpdateOrderStatusResponse
import io.nats.client.Connection
import io.nats.client.Dispatcher
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
@NatsController(subjectPrefix = OrdersNatsSubject.ORDER_PREFIX, queueGroup = OrdersNatsSubject.QUEUE_GROUP)
class OrderNatsController(
    private val orderService: OrderService,
    connection: Connection,
    dispatcher: Dispatcher,
) : AbstractNatsController(connection, dispatcher) {

    @NatsHandler(subject = OrdersNatsSubject.SAVE)
    fun add(request: CreateOrderRequest): Mono<CreateOrderResponse> {
        return orderService.add(request.toCreateOrderDTO())
            .map { it.toCreateOrderResponse() }
            .onErrorResume { error -> Mono.just(error.toFailureCreateOrderResponse()) }
    }

    @NatsHandler(subject = OrdersNatsSubject.FIND_BY_ID)
    fun findById(request: FindOrderByIdRequest): Mono<FindOrderByIdResponse> {
        return orderService.getById(request.id)
            .map { it.toFindOrderByIdResponse() }
            .onErrorResume { error -> Mono.just(error.toFailureFindOrderByIdResponse()) }
    }

    @NatsHandler(subject = OrdersNatsSubject.UPDATE)
    fun update(request: UpdateOrderRequest): Mono<UpdateOrderResponse> {
        return orderService.updateOrder(request.id, request.toUpdateOrderDTO())
            .map { it.toUpdateOrderResponse() }
            .onErrorResume { error -> Mono.just(error.toFailureUpdateOrderResponse()) }
    }

    @NatsHandler(subject = OrdersNatsSubject.UPDATE_STATUS)
    fun updateStatus(request: UpdateOrderStatusRequest): Mono<UpdateOrderStatusResponse> {
        return orderService.updateOrderStatus(request.id, request.status)
            .map { it.toUpdateOrderStatusResponse() }
            .onErrorResume { error -> Mono.just(error.toFailureUpdateStatusOrderResponse()) }
    }

    @NatsHandler(subject = OrdersNatsSubject.DELETE)
    fun delete(request: DeleteOrderRequest): Mono<DeleteOrderResponse> {
        return orderService.deleteById(request.id)
            .map { toDeleteOrderResponse() }
            .onErrorResume { error -> Mono.just(error.toFailureDeleteOrderResponse()) }
    }

    @NatsHandler(subject = OrdersNatsSubject.FIND_ALL_BY_USER_ID)
    fun findAllByUserId(request: FindOrdersByUserIdRequest): Mono<FindOrdersByUserIdResponse> {
        return orderService.getAllByUserId(request.id)
            .collectList()
            .map { orders -> toFindOrdersByUserIdResponse(orders) }
            .onErrorResume { error -> Mono.just(error.toFailureFindOrdersByUserIdResponse()) }
    }
}
