package com.example.gateway.service

import com.example.gateway.OrderProtoFixture
import com.example.gateway.OrderProtoFixture.findOrderByIdResponse
import com.example.gateway.OrderProtoFixture.grpcFindOrderByIdResponse
import com.example.gateway.application.port.output.OrderOutputPort
import com.example.gateway.infrastructure.grpc.OrderGrpcService
import com.example.gateway.infrastructure.mapper.OrderProtoMapper.toGrpc
import com.example.gateway.infrastructure.mapper.OrderProtoMapper.toInternal
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import systems.ajax.nats.handler.api.NatsHandlerManager

@ExtendWith(MockKExtension::class)
class OrderGrpcServiceTest {
    @MockK
    private lateinit var orderOutputPort: OrderOutputPort

    @SuppressWarnings("UnusedPrivateProperty")
    @MockK
    private lateinit var natsHandlerManager: NatsHandlerManager

    @InjectMockKs
    private lateinit var orderGrpcService: OrderGrpcService

    @Test
    fun `createOrder should return created order`() {
        // GIVEN
        val request = OrderProtoFixture.grpcCreateOrderRequest
        every {
            orderOutputPort.create(request.toInternal())
        } returns OrderProtoFixture.createOrderResponse.toMono()

        // WHEN
        val actual = orderGrpcService.createOrder(request)

        // THEN
        actual.test()
            .expectNext(OrderProtoFixture.createOrderResponse.toGrpc())
            .verifyComplete()
    }

    @Test
    fun `getOrderById should return order`() {
        // GIVEN
        val request = OrderProtoFixture.grpcFindOrderByIdRequest
        every {
            orderOutputPort.findById(request.toInternal())
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
