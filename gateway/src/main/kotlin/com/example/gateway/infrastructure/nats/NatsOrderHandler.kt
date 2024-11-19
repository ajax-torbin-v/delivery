package com.example.gateway.infrastructure.nats

import com.example.commonmodels.order.Order
import com.example.gateway.application.port.output.OrderOutputPort
import com.example.internal.api.NatsSubject
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
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.nats.handler.api.NatsHandlerManager
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import com.example.grpcapi.reqres.order.UpdateOrderStatusRequest as GrpcReqresOrderUpdateOrderStatusRequest

@Component
class NatsOrderHandler(
    private val natsMessagePublisher: NatsMessagePublisher,
    private val manager: NatsHandlerManager,
) : OrderOutputPort {
    override fun create(request: CreateOrderRequest): Mono<CreateOrderResponse> {
        return natsMessagePublisher.request(
            NatsSubject.Order.SAVE,
            request,
            CreateOrderResponse.parser()
        )
    }

    override fun findById(request: FindOrderByIdRequest): Mono<FindOrderByIdResponse> {
        return natsMessagePublisher.request(
            NatsSubject.Order.FIND_BY_ID,
            request,
            FindOrderByIdResponse.parser()
        )
    }

    override fun update(request: UpdateOrderRequest): Mono<UpdateOrderResponse> {
        return natsMessagePublisher.request(
            NatsSubject.Order.UPDATE,
            request,
            UpdateOrderResponse.parser()
        )
    }

    override fun updateStatus(request: UpdateOrderStatusRequest): Mono<UpdateOrderStatusResponse> {
        return natsMessagePublisher.request(
            NatsSubject.Order.UPDATE_STATUS,
            request,
            UpdateOrderStatusResponse.parser()
        )
    }

    override fun delete(request: DeleteOrderRequest): Mono<DeleteOrderResponse> {
        return natsMessagePublisher.request(
            NatsSubject.Order.DELETE,
            request,
            DeleteOrderResponse.parser()
        )
    }

    override fun findAllByUserId(request: FindOrdersByUserIdRequest): Mono<FindOrdersByUserIdResponse> {
        return natsMessagePublisher.request(
            NatsSubject.Order.FIND_ALL_BY_USER_ID,
            request,
            FindOrdersByUserIdResponse.parser()
        )
    }

    override fun subscribeToUpdateByUserId(request: GrpcReqresOrderUpdateOrderStatusRequest): Flux<Order> {
        return manager.subscribe(request.userId) { message ->
            UpdateOrderStatusResponse.parseFrom(message.data).success.order
        }
    }
}
