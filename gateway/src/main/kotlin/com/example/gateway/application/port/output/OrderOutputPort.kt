package com.example.gateway.application.port.output

import com.example.commonmodels.order.Order
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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import com.example.grpcapi.reqres.order.UpdateOrderStatusRequest as GrpcReqresOrderUpdateOrderStatusRequest

interface OrderOutputPort {
    fun create(request: CreateOrderRequest): Mono<CreateOrderResponse>
    fun findById(request: FindOrderByIdRequest): Mono<FindOrderByIdResponse>
    fun update(request: UpdateOrderRequest): Mono<UpdateOrderResponse>
    fun updateStatus(request: UpdateOrderStatusRequest): Mono<UpdateOrderStatusResponse>
    fun delete(request: DeleteOrderRequest): Mono<DeleteOrderResponse>
    fun findAllByUserId(request: FindOrdersByUserIdRequest): Mono<FindOrdersByUserIdResponse>
    fun subscribeToUpdateByUserId(request: GrpcReqresOrderUpdateOrderStatusRequest): Flux<Order>
}
