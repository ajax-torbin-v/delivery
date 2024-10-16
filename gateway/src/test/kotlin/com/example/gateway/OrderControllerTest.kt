package com.example.gateway

import com.example.delivery.OrderFixture.buildDeleteOrderRequest
import com.example.delivery.OrderFixture.buildFindOrderRequest
import com.example.delivery.OrderFixture.createOrderDTO
import com.example.delivery.OrderFixture.domainOrder
import com.example.delivery.OrderFixture.domainOrderWithProduct
import com.example.delivery.OrderFixture.orderDTO
import com.example.delivery.OrderFixture.orderNotFoundException
import com.example.delivery.OrderFixture.randomOrderId
import com.example.delivery.OrderFixture.unexpectedError
import com.example.delivery.OrderFixture.updateOrderDTO
import com.example.delivery.OrderFixture.updatedDomainOrder
import com.example.delivery.ProductFixture
import com.example.delivery.UserFixture.randomUserId
import com.example.delivery.UserFixture.userNotFoundException
import com.example.delivery.domain.DomainOrder
import com.example.delivery.exception.OrderNotFoundException
import com.example.delivery.exception.ProductNotFoundException
import com.example.delivery.exception.UserNotFoundException
import com.example.delivery.mapper.OrderMapper.toDTO
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
import com.example.delivery.mapper.OrderProtoMapper.toUpdateOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toUpdateOrderStatusResponse
import com.example.delivery.mapper.OrderWithProductMapper.toDTO
import com.example.gateway.client.NatsClient
import com.example.gateway.mapper.OrderProtoMapper.toCreateOrderRequest
import com.example.gateway.mapper.OrderProtoMapper.updateOrderRequest
import com.example.gateway.rest.OrderController
import com.example.internal.api.subject.OrdersNatsSubject
import com.example.internal.input.reqreply.order.create.CreateOrderResponse
import com.example.internal.input.reqreply.order.delete.DeleteOrderResponse
import com.example.internal.input.reqreply.order.find.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.find_by_user_id.FindOrdersByUserIdRequest
import com.example.internal.input.reqreply.order.find_by_user_id.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.update.UpdateOrderResponse
import com.example.internal.input.reqreply.order.update_status.UpdateOrderStatusRequest
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
        } returns domainOrder.toCreateOrderResponse().toMono()

        // WHEN
        val actual = orderController.add(createOrderDTO).block()

        // THEN
        assertEquals(orderDTO, actual)
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
        } returns userNotFoundException.toFailureCreateOrderResponse().toMono()

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
        } returns ProductFixture.productNotFoundException.toFailureCreateOrderResponse().toMono()

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
        } returns unexpectedError.toFailureCreateOrderResponse().toMono()

        // WHEN //THEN
        assertThrows<IllegalStateException> { orderController.add(createOrderDTO).block() }
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
        assertThrows<IllegalArgumentException> { orderController.add(createOrderDTO).block() }
    }

    @Test
    fun `findById should return existing order`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_BY_ID}",
                payload = buildFindOrderRequest(randomOrderId),
                parser = FindOrderByIdResponse.parser()
            )
        } returns domainOrderWithProduct.toFindOrderByIdResponse().toMono()

        // WHEN
        val actual = orderController.findById(randomOrderId).block()!!

        // THEN
        assertEquals(domainOrderWithProduct.toDTO(), actual)
    }

    @Test
    fun `findById should throw exception when order doesn't exist`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_BY_ID}",
                payload = buildFindOrderRequest(randomOrderId),
                parser = FindOrderByIdResponse.parser()
            )
        } returns orderNotFoundException.toFailureFindOrderByIdResponse().toMono()

        // WHEN //THEN
        assertThrows<OrderNotFoundException> { orderController.findById(randomOrderId).block()!! }
    }

    @Test
    fun `findById should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_BY_ID}",
                payload = buildFindOrderRequest(randomOrderId),
                parser = FindOrderByIdResponse.parser()
            )
        } returns unexpectedError.toFailureFindOrderByIdResponse().toMono()

        // WHEN //THEN
        assertThrows<IllegalStateException> { orderController.findById(randomOrderId).block()!! }
    }

    @Test
    fun `findById should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_BY_ID}",
                payload = buildFindOrderRequest(randomOrderId),
                parser = FindOrderByIdResponse.parser()
            )
        } returns FindOrderByIdResponse.getDefaultInstance().toMono()

        // WHEN //THEN
        assertThrows<IllegalArgumentException> { orderController.findById(randomOrderId).block()!! }
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
        } returns updatedDomainOrder.toUpdateOrderResponse().toMono()

        // WHEN
        val actual = orderController.update(randomOrderId, updateOrderDTO).block()!!

        // THEN
        assertEquals(updatedDomainOrder.toDTO(), actual)
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
        } returns orderNotFoundException.toFailureUpdateOrderResponse().toMono()

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
        } returns unexpectedError.toFailureUpdateOrderResponse().toMono()

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
        assertThrows<IllegalArgumentException> { orderController.update(randomOrderId, updateOrderDTO).block()!! }
    }

    @Test
    fun `findAllByUserId should return all orders for user`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_ALL_BY_USER_ID}",
                payload = FindOrdersByUserIdRequest.newBuilder().setId(randomUserId).build(),
                parser = FindOrdersByUserIdResponse.parser()
            )
        } returns toFindOrdersByUserIdResponse(listOf(domainOrder)).toMono()

        // WHEN
        val actual = orderController.findAllByUserId(randomUserId).block()

        // THEN
        assertEquals(listOf(domainOrder).map { it.toDTO() }, actual)
    }

    @Test
    fun `findAllByUserId should throw exception when user doesn't exist`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_ALL_BY_USER_ID}",
                payload = FindOrdersByUserIdRequest.newBuilder().setId(randomUserId).build(),
                parser = FindOrdersByUserIdResponse.parser()
            )
        } returns userNotFoundException.toFailureFindOrdersByUserIdResponse().toMono()

        // WHEN // THEN
        assertThrows<UserNotFoundException> { orderController.findAllByUserId(randomUserId).block() }
    }

    @Test
    fun `findAllByUserId should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.FIND_ALL_BY_USER_ID}",
                payload = FindOrdersByUserIdRequest.newBuilder().setId(randomUserId).build(),
                parser = FindOrdersByUserIdResponse.parser()
            )
        } returns FindOrdersByUserIdResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { orderController.findAllByUserId(randomUserId).block() }
    }

    @Test
    fun `updateStatus should return order with updated status`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.UPDATE_STATUS}",
                payload = UpdateOrderStatusRequest.newBuilder().setId(randomOrderId).setStatus("COMPLETED").build(),
                parser = UpdateOrderStatusResponse.parser()
            )
        } returns domainOrder.copy(status = DomainOrder.Status.COMPLETED).toUpdateOrderStatusResponse().toMono()

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
                payload = UpdateOrderStatusRequest.newBuilder().setId(randomOrderId).setStatus("COMPLETED").build(),
                parser = UpdateOrderStatusResponse.parser()
            )
        } returns orderNotFoundException.toFailureUpdateStatusOrderResponse().toMono()

        // WHEN // THEN
        assertThrows<OrderNotFoundException> { orderController.updateStatus(randomOrderId, "COMPLETED").block() }
    }

    @Test
    fun `updateStatus should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.UPDATE_STATUS}",
                payload = UpdateOrderStatusRequest.newBuilder().setId(randomOrderId).setStatus("COMPLETED").build(),
                parser = UpdateOrderStatusResponse.parser()
            )
        } returns unexpectedError.toFailureUpdateStatusOrderResponse().toMono()

        // WHEN // THEN
        assertThrows<IllegalStateException> { orderController.updateStatus(randomOrderId, "COMPLETED").block() }
    }

    @Test
    fun `updateStatus should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.UPDATE_STATUS}",
                payload = UpdateOrderStatusRequest.newBuilder().setId(randomOrderId).setStatus("COMPLETED").build(),
                parser = UpdateOrderStatusResponse.parser()
            )
        } returns UpdateOrderStatusResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { orderController.updateStatus(randomOrderId, "COMPLETED").block() }
    }

    @Test
    fun `delete should delete order`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.DELETE}",
                buildDeleteOrderRequest(randomOrderId),
                DeleteOrderResponse.parser()
            )
        } returns toDeleteOrderResponse().toMono()

        // WHEN
        orderController.delete(randomOrderId).block()

        // THEN
        verify(exactly = 1) {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.DELETE}",
                buildDeleteOrderRequest(randomOrderId),
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
                buildDeleteOrderRequest(randomOrderId),
                DeleteOrderResponse.parser()
            )
        } returns unexpectedError.toFailureDeleteOrderResponse().toMono()

        // WHEN // THEN
        assertThrows<IllegalStateException> { orderController.delete(randomOrderId).block() }
    }

    @Test
    fun `delete should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${OrdersNatsSubject.ORDER_PREFIX}.${OrdersNatsSubject.DELETE}",
                buildDeleteOrderRequest(randomOrderId),
                DeleteOrderResponse.parser()
            )
        } returns DeleteOrderResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { orderController.delete(randomOrderId).block() }
    }
}
