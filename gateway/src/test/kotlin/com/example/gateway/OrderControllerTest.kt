package com.example.gateway

import com.example.core.OrderFixture.createOrderDTO
import com.example.core.OrderFixture.orderDTO
import com.example.core.OrderFixture.randomOrderId
import com.example.core.OrderFixture.updateOrderDTO
import com.example.core.UserFixture.randomUserId
import com.example.core.exception.OrderNotFoundException
import com.example.core.exception.ProductNotFoundException
import com.example.core.exception.UserNotFoundException
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
import com.example.gateway.OrderProtoFixture.updateOrderResponse
import com.example.gateway.OrderProtoFixture.updateOrderResponseWithOrderNotFoundException
import com.example.gateway.OrderProtoFixture.updateOrderResponseWithUnexpectedException
import com.example.gateway.OrderProtoFixture.updateOrderStatusRequest
import com.example.gateway.OrderProtoFixture.updateOrderStatusResponse
import com.example.gateway.OrderProtoFixture.updateOrderStatusResponseWithOrNotFoundException
import com.example.gateway.OrderProtoFixture.updateOrderStatusResponseWithUnexpectedException
import com.example.gateway.client.NatsClient
import com.example.gateway.mapper.OrderProtoMapper.toCreateOrderRequest
import com.example.gateway.mapper.OrderProtoMapper.toDTO
import com.example.gateway.mapper.OrderProtoMapper.toDtoWithProduct
import com.example.gateway.mapper.OrderProtoMapper.updateOrderRequest
import com.example.gateway.rest.OrderController
import com.example.internal.api.subject.OrdersNatsSubject
import com.example.internal.input.reqreply.order.create.CreateOrderResponse
import com.example.internal.input.reqreply.order.delete.DeleteOrderResponse
import com.example.internal.input.reqreply.order.find.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.find_by_user_id.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.update.UpdateOrderResponse
import com.example.internal.input.reqreply.order.update_status.UpdateOrderStatusResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.nats.client.Connection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toMono

@ExtendWith(MockKExtension::class)
class OrderControllerTest {
    @SuppressWarnings("UnusedPrivateProperty")
    @MockK
    private lateinit var connection: Connection

    @MockK
    private lateinit var natsClient: NatsClient

    @InjectMockKs
    private lateinit var orderController: OrderController

    @Test
    fun `save should return order DTO`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.SAVE}",
                payload = createOrderDTO.toCreateOrderRequest(),
                parser = CreateOrderResponse.parser()
            )
        } returns createOrderResponse.toMono()

        // WHEN
        val actual = orderController.add(createOrderDTO).block()

        // THEN
        assertEquals(createOrderResponse.toDTO(), actual)
    }

    @Test
    fun `save should throw exception when user doesn't exist`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.SAVE}",
                payload = createOrderDTO.toCreateOrderRequest(),
                parser = CreateOrderResponse.parser()
            )
        } returns createOrderResponseWithUserNotFoundException.toMono()

        // WHEN //THEN
        assertThrows<UserNotFoundException> { orderController.add(createOrderDTO).block() }
    }

    @Test
    fun `save should throw exception when product doesn't exist`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.SAVE}",
                payload = createOrderDTO.toCreateOrderRequest(),
                parser = CreateOrderResponse.parser()
            )
        } returns createOrderResponseWithProductNotFoundException.toMono()

        // WHEN //THEN
        assertThrows<ProductNotFoundException> { orderController.add(createOrderDTO).block() }
    }

    @Test
    fun `save should rethrow exception when message contains unexpected error`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.SAVE}",
                payload = createOrderDTO.toCreateOrderRequest(),
                parser = CreateOrderResponse.parser()
            )
        } returns createOrderResponseWithUnexpectedException.toMono()

        // WHEN //THEN
        assertThrows<RuntimeException> { orderController.add(createOrderDTO).block() }
    }

    @Test
    fun `save should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.SAVE}",
                payload = createOrderDTO.toCreateOrderRequest(),
                parser = CreateOrderResponse.parser()
            )
        } returns CreateOrderResponse.getDefaultInstance().toMono()

        // WHEN //THEN
        assertThrows<RuntimeException> { orderController.add(createOrderDTO).block() }
    }

    @Test
    fun `findById should return existing order`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_BY_ID}",
                payload = findOrderByIdRequest,
                parser = FindOrderByIdResponse.parser()
            )
        } returns findOrderByIdResponse.toMono()

        // WHEN
        val actual = orderController.findById(randomOrderId).block()!!
        println(actual)

        // THEN
        assertEquals(findOrderByIdResponse.toDtoWithProduct(), actual)
    }

    @Test
    fun `findById should throw exception when order doesn't exist`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_BY_ID}",
                payload = findOrderByIdRequest,
                parser = FindOrderByIdResponse.parser()
            )
        } returns findOrderByIdResponseWithOrderNotFoundException.toMono()

        // WHEN //THEN
        assertThrows<OrderNotFoundException> { orderController.findById(randomOrderId).block()!! }
    }

    @Test
    fun `findById should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_BY_ID}",
                payload = findOrderByIdRequest,
                parser = FindOrderByIdResponse.parser()
            )
        } returns findOrderByIdResponseWithUnexpectedException.toMono()

        // WHEN //THEN
        assertThrows<IllegalStateException> { orderController.findById(randomOrderId).block()!! }
    }

    @Test
    fun `findById should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_BY_ID}",
                payload = findOrderByIdRequest,
                parser = FindOrderByIdResponse.parser()
            )
        } returns FindOrderByIdResponse.getDefaultInstance().toMono()

        // WHEN //THEN
        assertThrows<RuntimeException> { orderController.findById(randomOrderId).block()!! }
    }

    @Test
    fun `update should return updated order`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.UPDATE}",
                payload = updateOrderRequest(randomOrderId, updateOrderDTO),
                parser = UpdateOrderResponse.parser()
            )
        } returns updateOrderResponse.toMono()

        // WHEN
        val actual = orderController.update(randomOrderId, updateOrderDTO).block()!!

        // THEN
        assertEquals(updateOrderResponse.toDTO(), actual)
    }

    @Test
    fun `update should throw exception when order doesn't exist`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.UPDATE}",
                payload = updateOrderRequest(randomOrderId, updateOrderDTO),
                parser = UpdateOrderResponse.parser()
            )
        } returns updateOrderResponseWithOrderNotFoundException.toMono()

        // WHEN //THEN
        assertThrows<OrderNotFoundException> { orderController.update(randomOrderId, updateOrderDTO).block()!! }
    }

    @Test
    fun `update should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.UPDATE}",
                payload = updateOrderRequest(randomOrderId, updateOrderDTO),
                parser = UpdateOrderResponse.parser()
            )
        } returns updateOrderResponseWithUnexpectedException.toMono()

        // WHEN //THEN
        assertThrows<IllegalStateException> { orderController.update(randomOrderId, updateOrderDTO).block()!! }
    }

    @Test
    fun `update should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.UPDATE}",
                payload = updateOrderRequest(randomOrderId, updateOrderDTO),
                parser = UpdateOrderResponse.parser()
            )
        } returns UpdateOrderResponse.getDefaultInstance().toMono()

        // WHEN //THEN
        assertThrows<RuntimeException> { orderController.update(randomOrderId, updateOrderDTO).block()!! }
    }

    @Test
    fun `findAllByUserId should return all orders for user`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_ALL_BY_USER_ID}",
                payload = findOrdersByUserIdRequest,
                parser = FindOrdersByUserIdResponse.parser()
            )
        } returns findOrdersByUserIdResponse.toMono()

        // WHEN
        val actual = orderController.findAllByUserId(randomUserId).block()

        // THEN
        assertEquals(findOrdersByUserIdResponse.toDTO(), actual)
    }

    @Test
    fun `findAllByUserId should throw exception when user doesn't exist`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_ALL_BY_USER_ID}",
                payload = findOrdersByUserIdRequest,
                parser = FindOrdersByUserIdResponse.parser()
            )
        } returns findOrdersByUserIdResponseWithUserNotFoundException.toMono()

        // WHEN // THEN
        assertThrows<UserNotFoundException> { orderController.findAllByUserId(randomUserId).block() }
    }

    @Test
    fun `findAllByUserId should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_ALL_BY_USER_ID}",
                payload = findOrdersByUserIdRequest,
                parser = FindOrdersByUserIdResponse.parser()
            )
        } returns FindOrdersByUserIdResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<RuntimeException> { orderController.findAllByUserId(randomUserId).block() }
    }

    @Test
    fun `updateStatus should return order with updated status`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.UPDATE_STATUS}",
                payload = updateOrderStatusRequest,
                parser = UpdateOrderStatusResponse.parser()
            )
        } returns updateOrderStatusResponse.toMono()

        // WHEN
        val actual = orderController.updateStatus(randomOrderId, "COMPLETED").block()

        // THEN
        assertEquals(orderDTO.copy(status = "COMPLETED"), actual)
    }

    @Test
    fun `updateStatus should throw exception when order doesn't exist`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.UPDATE_STATUS}",
                payload = updateOrderStatusRequest,
                parser = UpdateOrderStatusResponse.parser()
            )
        } returns updateOrderStatusResponseWithOrNotFoundException.toMono()

        // WHEN // THEN
        assertThrows<OrderNotFoundException> { orderController.updateStatus(randomOrderId, "COMPLETED").block() }
    }

    @Test
    fun `updateStatus should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.UPDATE_STATUS}",
                payload = updateOrderStatusRequest,
                parser = UpdateOrderStatusResponse.parser()
            )
        } returns updateOrderStatusResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        assertThrows<IllegalStateException> { orderController.updateStatus(randomOrderId, "COMPLETED").block() }
    }

    @Test
    fun `updateStatus should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.UPDATE_STATUS}",
                payload = updateOrderStatusRequest,
                parser = UpdateOrderStatusResponse.parser()
            )
        } returns UpdateOrderStatusResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<RuntimeException> { orderController.updateStatus(randomOrderId, "COMPLETED").block() }
    }

    @Test
    fun `delete should delete order`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.DELETE}",
                deleteOrderRequest,
                DeleteOrderResponse.parser()
            )
        } returns deleteOrderResponse.toMono()

        // WHEN
        orderController.delete(randomOrderId).block()

        // THEN
        verify(exactly = 1) {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.DELETE}",
                deleteOrderRequest,
                DeleteOrderResponse.parser()
            )
        }
    }

    @Test
    fun `delete should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.DELETE}",
                deleteOrderRequest,
                DeleteOrderResponse.parser()
            )
        } returns deleteOrderResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        assertThrows<IllegalStateException> { orderController.delete(randomOrderId).block() }
    }

    @Test
    fun `delete should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.DELETE}",
                deleteOrderRequest,
                DeleteOrderResponse.parser()
            )
        } returns DeleteOrderResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<RuntimeException> { orderController.delete(randomOrderId).block() }
    }
}
