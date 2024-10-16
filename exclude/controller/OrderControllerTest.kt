package com.example.delivery.controller

import com.example.delivery.OrderFixture.createOrderDTO
import com.example.delivery.OrderFixture.domainOrder
import com.example.delivery.OrderFixture.domainOrderWithProduct
import com.example.delivery.OrderFixture.updateOrderDTO
import com.example.delivery.OrderFixture.updatedDomainOrder
import com.example.delivery.domain.DomainOrder
import com.example.delivery.mapper.OrderMapper.toDTO
import com.example.delivery.mapper.OrderWithProductMapper.toDTO
import com.example.delivery.service.OrderService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
internal class OrderControllerTest {
    @MockK
    private lateinit var orderService: OrderService

    @InjectMockKs
    private lateinit var orderController: OrderController

    @Test
    fun `should add order and return status is created`() {
        // GIVEN
        every { orderService.add(createOrderDTO) } returns domainOrder.toMono()

        // WHEN
        val actual = orderController.add(createOrderDTO)

        // THEN
        actual
            .test()
            .expectNext(domainOrder.toDTO())
            .verifyComplete()
    }

    @Test
    fun `should return order when order exists`() {
        // GIVEN
        every { orderService.getById("1") } returns domainOrderWithProduct.toMono()

        // WHEN
        val actual = orderController.findById("1")

        // THEN
        actual
            .test()
            .expectNext(domainOrderWithProduct.toDTO())
            .verifyComplete()
    }

    @Test
    fun `should update order with proper dto`() {
        // GIVEN
        every { orderService.updateOrder("1", updateOrderDTO) } returns updatedDomainOrder.toMono()

        // WHEN
        val actual = orderController.update("1", updateOrderDTO)

        // THEN
        actual
            .test()
            .expectNext(updatedDomainOrder.toDTO())
            .verifyComplete()
    }

    @Test
    fun `should update order's status`() {
        // GIVEN
        val order = domainOrder.copy(status = DomainOrder.Status.CANCELED)
        every { orderService.updateOrderStatus("1", "CANCELED") } returns order.toMono()

        // WHEN
        val actual = orderController.updateStatus("1", "CANCELED")

        // THEN
        actual
            .test()
            .expectNext(order.toDTO())
            .verifyComplete()
    }

    @Test
    fun `should delete order when order exists`() {
        // GIVEN
        every { (orderService).deleteById("1") } returns Mono.empty()

        // WHEN
        val actual = orderController.delete("1")

        // THEN
        actual
            .test()
            .verifyComplete()

        verify(exactly = 1) { orderService.deleteById("1") }
    }
}
