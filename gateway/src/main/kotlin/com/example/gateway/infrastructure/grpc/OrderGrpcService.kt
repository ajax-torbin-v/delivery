package com.example.gateway.infrastructure.grpc

import com.example.commonmodels.order.Order
import com.example.gateway.application.port.output.OrderOutputPort
import com.example.gateway.infrastructure.mapper.OrderProtoMapper.toGrpc
import com.example.gateway.infrastructure.mapper.OrderProtoMapper.toInternal
import com.example.grpcapi.service.ReactorOrderServiceGrpc
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import com.example.grpcapi.reqres.order.CreateOrderRequest as GrpcCreateOrderRequest
import com.example.grpcapi.reqres.order.CreateOrderResponse as GrpcCreateOrderResponse
import com.example.grpcapi.reqres.order.FindOrderByIdRequest as GrpcFindOrderByIdRequest
import com.example.grpcapi.reqres.order.FindOrderByIdResponse as GrpcFindOrderByIdResponse
import com.example.grpcapi.reqres.order.UpdateOrderStatusRequest as GrpcUpdateOrderStatusRequest

@GrpcService
class OrderGrpcService(
    private val orderOutputPort: OrderOutputPort,
) : ReactorOrderServiceGrpc.OrderServiceImplBase() {
    override fun createOrder(request: Mono<GrpcCreateOrderRequest>): Mono<GrpcCreateOrderResponse> {
        return request.map { it.toInternal() }
            .flatMap {
                orderOutputPort.create(it)
            }.map { it.toGrpc() }
    }

    override fun getOrderById(request: Mono<GrpcFindOrderByIdRequest>): Mono<GrpcFindOrderByIdResponse> {
        return request.map { it.toInternal() }
            .flatMap {
                orderOutputPort.findById(it)
            }.map { it.toGrpc() }
    }

    override fun subscribeToUpdateByUserId(request: Mono<GrpcUpdateOrderStatusRequest>): Flux<Order> {
        return request.flatMapMany {
            orderOutputPort.subscribeToUpdateByUserId(it)
        }
    }
}
