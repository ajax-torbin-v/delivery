package com.example.gateway.service

import com.example.gateway.OrderProtoFixture
import com.example.gateway.OrderProtoFixture.findOrderByIdResponse
import com.example.gateway.OrderProtoFixture.grpcFindOrderByIdResponse
import com.example.gateway.client.NatsClient
import com.example.gateway.grpc.OrderGrpcService
import com.example.gateway.mapper.OrderProtoMapper.toGrpc
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.google.protobuf.Parser
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
class OrderGrpcServiceTest {
    @MockK
    private lateinit var natsClient: NatsClient

    @InjectMockKs
    private lateinit var orderGrpcService: OrderGrpcService

    @Test
    fun `createOrder should return created order`() {
        // GIVEN
        val request = OrderProtoFixture.grpcCreateOrderRequest
        every {
            natsClient.doRequest(
                any(),
                any(),
                any<Parser<CreateOrderResponse>>()
            )
        } returns OrderProtoFixture.createOrderResponse.toMono()

        // WHEN
        val actual = orderGrpcService.createOrder(request)

        // THEN
        actual.test()
            .expectNext(OrderProtoFixture.createOrderResponse.toGrpc())
    }

    @Test
    fun `getOrderById should return order`() {
        // GIVEN
        val request = OrderProtoFixture.grpcFindOrderByIdRequest
        every {
            natsClient.doRequest(
                any(),
                any(),
                any<Parser<FindOrderByIdResponse>>()
            )
        } returns findOrderByIdResponse.toMono()

        // WHEN
        val actual = orderGrpcService.getOrderById(request)

        // THEN
        actual.test()
            .assertNext {
                assertEquals(findOrderByIdResponse.toGrpc(), grpcFindOrderByIdResponse)
            }
            .verifyComplete()
    }
}
