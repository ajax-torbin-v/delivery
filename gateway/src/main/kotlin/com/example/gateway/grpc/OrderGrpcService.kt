package com.example.gateway.grpc

import com.example.commonmodels.order.Order
import com.example.gateway.mapper.OrderProtoMapper.toGrpc
import com.example.gateway.mapper.OrderProtoMapper.toInternal
import com.example.grpcapi.service.ReactorOrderServiceGrpc
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import systems.ajax.nats.handler.api.NatsHandlerManager
import systems.ajax.nats.publisher.api.NatsMessagePublisher
import com.example.grpcapi.reqres.order.CreateOrderRequest as GrpcCreateOrderRequest
import com.example.grpcapi.reqres.order.CreateOrderResponse as GrpcCreateOrderResponse
import com.example.grpcapi.reqres.order.FindOrderByIdRequest as GrpcFindOrderByIdRequest
import com.example.grpcapi.reqres.order.FindOrderByIdResponse as GrpcFindOrderByIdResponse
import com.example.grpcapi.reqres.order.UpdateOrderStatusRequest as GrpcUpdateOrderStatusRequest

@GrpcService
class OrderGrpcService(
    private val natsPublisher: NatsMessagePublisher,
    private val manager: NatsHandlerManager,
) : ReactorOrderServiceGrpc.OrderServiceImplBase() {
    override fun createOrder(request: Mono<GrpcCreateOrderRequest>): Mono<GrpcCreateOrderResponse> {
        return request.map { it.toInternal() }
            .flatMap {
                natsPublisher.request(
                    subject = NatsSubject.Order.SAVE,
                    payload = it,
                    parser = CreateOrderResponse.parser(),
                )
            }.map { it.toGrpc() }
    }

    override fun getOrderById(request: Mono<GrpcFindOrderByIdRequest>): Mono<GrpcFindOrderByIdResponse> {
        return request.map { it.toInternal() }
            .flatMap {
                natsPublisher.request(
                    subject = NatsSubject.Order.FIND_BY_ID,
                    payload = it,
                    parser = FindOrderByIdResponse.parser()
                )
            }.map { it.toGrpc() }
    }

    override fun subscribeToUpdateByUserId(request: Mono<GrpcUpdateOrderStatusRequest>): Flux<Order> {
        return request.flatMapMany {
            manager.subscribe(it.userId) { message ->
                UpdateOrderStatusResponse.parseFrom(message.data).success.order
            }
        }
    }
}
