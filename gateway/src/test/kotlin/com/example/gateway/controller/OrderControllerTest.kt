package com.example.gateway.controller

import com.example.core.OrderFixture.randomOrderId
import com.example.core.UserFixture.randomUserId
import com.example.core.exception.OrderNotFoundException
import com.example.core.exception.ProductNotFoundException
import com.example.core.exception.UserNotFoundException
import com.example.gateway.OrderProtoFixture.createOrderDTO
import com.example.gateway.OrderProtoFixture.createOrderResponse
import com.example.gateway.OrderProtoFixture.createOrderResponseWithProductNotFoundException
import com.example.gateway.OrderProtoFixture.createOrderResponseWithUnexpectedException
import com.example.gateway.OrderProtoFixture.createOrderResponseWithUserNotFoundException
import com.example.gateway.OrderProtoFixture.deleteOrderRequest
import com.example.gateway.OrderProtoFixture.deleteOrderResponse
import com.example.gateway.OrderProtoFixture.deleteOrderResponseWithUnexpectedException
import com.example.gateway.OrderProtoFixture.findOrderByIdRequest
import com.example.gateway.OrderProtoFixture.findOrderByIdResponse
import com.example.gateway.OrderProtoFixture.findOrderByIdResponseWithOrderNotFoundException
import com.example.gateway.OrderProtoFixture.findOrderByIdResponseWithUnexpectedException
import com.example.gateway.OrderProtoFixture.findOrdersByUserIdRequest
import com.example.gateway.OrderProtoFixture.findOrdersByUserIdResponse
import com.example.gateway.OrderProtoFixture.findOrdersByUserIdResponseWithUserNotFoundException
import com.example.gateway.OrderProtoFixture.orderDTO
import com.example.gateway.OrderProtoFixture.updateOrderDTO
import com.example.gateway.OrderProtoFixture.updateOrderResponse
import com.example.gateway.OrderProtoFixture.updateOrderResponseWithOrderNotFoundException
import com.example.gateway.OrderProtoFixture.updateOrderResponseWithUnexpectedException
import com.example.gateway.OrderProtoFixture.updateOrderStatusRequest
import com.example.gateway.OrderProtoFixture.updateOrderStatusResponse
import com.example.gateway.OrderProtoFixture.updateOrderStatusResponseWithOrNotFoundException
import com.example.gateway.OrderProtoFixture.updateOrderStatusResponseWithUnexpectedException
import com.example.gateway.application.port.output.OrderOutputPort
import com.example.gateway.infrastructure.mapper.OrderProtoMapper.toCreateOrderRequest
import com.example.gateway.infrastructure.mapper.OrderProtoMapper.toDTO
import com.example.gateway.infrastructure.mapper.OrderProtoMapper.toDtoWithProduct
import com.example.gateway.infrastructure.mapper.OrderProtoMapper.updateOrderRequest
import com.example.gateway.infrastructure.rest.OrderController
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.DeleteOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.UpdateOrderResponse
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

@ExtendWith(MockKExtension::class)
class OrderControllerTest {
    @MockK
    private lateinit var orderOutputPort: OrderOutputPort

    @InjectMockKs
    private lateinit var orderController: OrderController

    @Test
    fun `save should return order DTO`() {
        // GIVEN
        every {
            orderOutputPort.create(createOrderDTO.toCreateOrderRequest())
        } returns createOrderResponse.toMono()

        // WHEN //THEN
        orderController.add(createOrderDTO)
            .test()
            .expectNext(createOrderResponse.toDTO())
            .verifyComplete()
    }

    @Test
    fun `save should throw exception when user doesn't exist`() {
        // GIVEN
        every {
            orderOutputPort.create(createOrderDTO.toCreateOrderRequest())
        } returns createOrderResponseWithUserNotFoundException.toMono()

        // WHEN //THEN
        orderController.add(createOrderDTO)
            .test()
            .verifyError(UserNotFoundException::class)
    }

    @Test
    fun `save should throw exception when product doesn't exist`() {
        // GIVEN
        every {
            orderOutputPort.create(
                createOrderDTO.toCreateOrderRequest(),
            )
        } returns createOrderResponseWithProductNotFoundException.toMono()

        // WHEN //THEN
        orderController.add(createOrderDTO)
            .test()
            .verifyError(ProductNotFoundException::class)
    }

    @Test
    fun `save should rethrow exception when message contains unexpected error`() {
        // GIVEN
        every {
            orderOutputPort.create(createOrderDTO.toCreateOrderRequest())
        } returns createOrderResponseWithUnexpectedException.toMono()

        // WHEN //THEN
        orderController.add(createOrderDTO)
            .test()
            .verifyError(RuntimeException::class)
    }

    @Test
    fun `save should throw exception when message is empty`() {
        // GIVEN
        every {
            orderOutputPort.create(createOrderDTO.toCreateOrderRequest())
        } returns CreateOrderResponse.getDefaultInstance().toMono()

        // WHEN //THEN
        orderController.add(createOrderDTO)
            .test()
            .verifyError(RuntimeException::class)
    }

    @Test
    fun `findById should return existing order`() {
        // GIVEN
        every {
            orderOutputPort.findById(findOrderByIdRequest)
        } returns findOrderByIdResponse.toMono()

        // WHEN // THEN
        orderController.findById(randomOrderId)
            .test()
            .expectNext(findOrderByIdResponse.toDtoWithProduct())
            .verifyComplete()
    }

    @Test
    fun `findById should throw exception when order doesn't exist`() {
        // GIVEN
        every {
            orderOutputPort.findById(findOrderByIdRequest)
        } returns findOrderByIdResponseWithOrderNotFoundException.toMono()

        // WHEN //THEN
        orderController.findById(randomOrderId)
            .test()
            .verifyError(OrderNotFoundException::class)
    }

    @Test
    fun `findById should rethrow unexpected exception`() {
        // GIVEN
        every {
            orderOutputPort.findById(findOrderByIdRequest)
        } returns findOrderByIdResponseWithUnexpectedException.toMono()

        // WHEN //THEN
        orderController.findById(randomOrderId)
            .test()
            .verifyError(IllegalStateException::class)
    }

    @Test
    fun `findById should throw exception when message is empty`() {
        // GIVEN
        every {
            orderOutputPort.findById(findOrderByIdRequest)
        } returns FindOrderByIdResponse.getDefaultInstance().toMono()

        // WHEN //THEN
        orderController.findById(randomOrderId)
            .test()
            .verifyError(RuntimeException::class)
    }

    @Test
    fun `update should return updated order`() {
        // GIVEN
        every {
            orderOutputPort.update(updateOrderRequest(randomOrderId, updateOrderDTO))
        } returns updateOrderResponse.toMono()

        // WHEN // THEN
        orderController.update(randomOrderId, updateOrderDTO)
            .test()
            .expectNext(updateOrderResponse.toDTO())
            .verifyComplete()
    }

    @Test
    fun `update should throw exception when order doesn't exist`() {
        // GIVEN
        every {
            orderOutputPort.update(updateOrderRequest(randomOrderId, updateOrderDTO))
        } returns updateOrderResponseWithOrderNotFoundException.toMono()

        // WHEN //THEN
        orderController.update(randomOrderId, updateOrderDTO)
            .test()
            .verifyError(OrderNotFoundException::class)
    }

    @Test
    fun `update should rethrow unexpected exception`() {
        // GIVEN
        every {
            orderOutputPort.update(updateOrderRequest(randomOrderId, updateOrderDTO))
        } returns updateOrderResponseWithUnexpectedException.toMono()

        // WHEN //THEN
        orderController.update(randomOrderId, updateOrderDTO)
            .test()
            .verifyError(IllegalStateException::class)
    }

    @Test
    fun `update should throw exception when message is empty`() {
        // GIVEN
        every {
            orderOutputPort.update(updateOrderRequest(randomOrderId, updateOrderDTO))
        } returns UpdateOrderResponse.getDefaultInstance().toMono()

        // WHEN //THEN
        orderController.update(randomOrderId, updateOrderDTO)
            .test()
            .verifyError(RuntimeException::class)
    }

    @Test
    fun `findAllByUserId should return all orders for user`() {
        // GIVEN
        every {
            orderOutputPort.findAllByUserId(findOrdersByUserIdRequest)
        } returns findOrdersByUserIdResponse.toMono()

        // WHEN // THEN
        orderController.findAllByUserId(randomUserId)
            .test()
            .expectNext(findOrdersByUserIdResponse.toDTO())
            .verifyComplete()
    }

    @Test
    fun `findAllByUserId should throw exception when user doesn't exist`() {
        // GIVEN
        every {
            orderOutputPort.findAllByUserId(findOrdersByUserIdRequest)
        } returns findOrdersByUserIdResponseWithUserNotFoundException.toMono()

        // WHEN // THEN
        orderController.findAllByUserId(randomUserId)
            .test()
            .verifyError(UserNotFoundException::class)
    }

    @Test
    fun `findAllByUserId should throw exception when message is empty`() {
        // GIVEN
        every {
            orderOutputPort.findAllByUserId(findOrdersByUserIdRequest)
        } returns FindOrdersByUserIdResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        orderController.findAllByUserId(randomUserId)
            .test()
            .verifyError(RuntimeException::class)
    }

    @Test
    fun `updateStatus should return order with updated status`() {
        // GIVEN
        every {
            orderOutputPort.updateStatus(updateOrderStatusRequest)
        } returns updateOrderStatusResponse.toMono()

        // WHEN //THEN
        orderController.updateStatus(randomOrderId, "COMPLETED")
            .test()
            .expectNext(orderDTO.copy(status = "COMPLETED"))
            .verifyComplete()
    }

    @Test
    fun `updateStatus should throw exception when order doesn't exist`() {
        // GIVEN
        every {
            orderOutputPort.updateStatus(updateOrderStatusRequest)
        } returns updateOrderStatusResponseWithOrNotFoundException.toMono()

        // WHEN // THEN
        orderController.updateStatus(randomOrderId, "COMPLETED")
            .test()
            .verifyError(OrderNotFoundException::class)
    }

    @Test
    fun `updateStatus should rethrow unexpected exception`() {
        // GIVEN
        every {
            orderOutputPort.updateStatus(updateOrderStatusRequest)
        } returns updateOrderStatusResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        orderController.updateStatus(randomOrderId, "COMPLETED")
            .test()
            .verifyError(IllegalStateException::class)
    }

    @Test
    fun `updateStatus should throw exception when message is empty`() {
        // GIVEN
        every {
            orderOutputPort.updateStatus(updateOrderStatusRequest)
        } returns UpdateOrderStatusResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        orderController.updateStatus(randomOrderId, "COMPLETED")
            .test()
            .verifyError(RuntimeException::class)
    }

    @Test
    fun `delete should delete order`() {
        // GIVEN
        every {
            orderOutputPort.delete(deleteOrderRequest)
        } returns deleteOrderResponse.toMono()

        // WHEN
        orderController.delete(randomOrderId).block()

        // THEN
        verify(exactly = 1) { orderOutputPort.delete(deleteOrderRequest) }
    }

    @Test
    fun `delete should rethrow unexpected exception`() {
        // GIVEN
        every {
            orderOutputPort.delete(deleteOrderRequest)
        } returns deleteOrderResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        orderController.delete(randomOrderId)
            .test()
            .verifyError(IllegalStateException::class)
    }

    @Test
    fun `delete should throw exception when message is empty`() {
        // GIVEN
        every {
            orderOutputPort.delete(deleteOrderRequest)
        } returns DeleteOrderResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        orderController.delete(randomOrderId)
            .test()
            .verifyError(RuntimeException::class)
    }
}
