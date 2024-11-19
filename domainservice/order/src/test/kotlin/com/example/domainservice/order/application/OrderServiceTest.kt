package com.example.domainservice.order.application

import com.example.core.OrderFixture.randomOrderId
import com.example.core.ProductFixture.randomProductId
import com.example.core.UserFixture.randomUserId
import com.example.core.exception.NotFoundException
import com.example.core.exception.ProductAmountException
import com.example.domainservice.OrderFixture.domainOrder
import com.example.domainservice.OrderFixture.updatedDomainOrder
import com.example.domainservice.ProductFixture.domainProduct
import com.example.domainservice.UserFixture.domainUser
import com.example.domainservice.order.application.port.output.OrderRepositoryOutputPort
import com.example.domainservice.order.application.service.OrderService
import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.infrastructure.kafka.OrderUpdateStatusProducer
import com.example.domainservice.product.application.port.output.ProductRepositoryOutputPort
import com.example.domainservice.user.infrastructure.mongo.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
internal class OrderServiceTest {
    @MockK
    private lateinit var orderRepository: OrderRepositoryOutputPort

    @MockK
    private lateinit var productRepository: ProductRepositoryOutputPort

    @MockK
    private lateinit var kafkaUpdateOrderStatusSender: OrderUpdateStatusProducer

    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var orderService: OrderService

    @Test
    fun `should return order when order exists`() {
        // GIVEN
        every { orderRepository.findById(randomOrderId) } returns domainOrder.toMono()

        // WHEN
        val actual = orderService.getById(randomOrderId)

        // THEN
        actual
            .test()
            .expectNext(domainOrder)
            .verifyComplete()
    }

    @Test
    fun `should throw exception when order doesn't exists while find`() {
        // GIVEN
        every { orderRepository.findById(randomOrderId) } returns Mono.empty()

        // WHEN
        val actual = orderService.getById(randomOrderId)

        // THEN
        actual
            .test()
            .expectError(NotFoundException::class.java)
    }

    @Test
    fun `should add order with proper dto`() {
        // GIVEN
        every { orderRepository.save(any()) } returns Mono.just(domainOrder)
        every { userRepository.findById(randomUserId) } returns domainUser.toMono()
        every { productRepository.findAllByIds(listOf(randomProductId)) } returns Flux.just(domainProduct)
        every { productRepository.updateProductsAmount(any()) } returns Unit.toMono()

        // WHEN
        val actual = orderService.save(domainOrder)

        // THEN
        actual
            .test()
            .expectNext(domainOrder)
            .verifyComplete()

        verify(exactly = 1) { orderRepository.save(any()) }
    }

    @Test
    fun `should throw exception when product doesn't exist`() {
        // GIVEN
        val productsIdsList = listOf(randomProductId)
        every { userRepository.findById(randomUserId) } returns domainUser.toMono()
        every { productRepository.findAllByIds(productsIdsList) } returns Flux.empty()

        // WHEN
        val actual = orderService.save(domainOrder)

        // THEN
        actual
            .test()
            .expectError(NotFoundException::class.java)
            .verify()
    }

    @Test
    fun `should throw exception when not sufficient product`() {
        // GIVEN
        every { userRepository.findById(randomUserId) } returns domainUser.toMono()
        every { productRepository.findAllByIds(listOf(randomProductId)) }
            .returns(Flux.just(domainProduct.copy(amountAvailable = -1)))

        // WHEN
        val actual = orderService.save(domainOrder)

        // THEN
        actual
            .test()
            .expectError(ProductAmountException::class.java)
            .verify()
    }

    @Test
    fun `should throw exception when user doesn't exist`() {
        // GIVEN
        every { userRepository.findById(randomUserId) } returns Mono.empty()

        // WHEN
        val actual = orderService.save(domainOrder)

        // THEN
        actual
            .test()
            .expectError(NotFoundException::class.java)
            .verify()
    }

    @Test
    fun `should update order with proper dto when product exists`() {
        // GIVEN
        every { orderRepository.updateOrder(updatedDomainOrder) } returns updatedDomainOrder.toMono()
        every { orderRepository.findById(updatedDomainOrder.id!!) } returns domainOrder.toMono()

        // WHEN
        val actual = orderService.updateOrder(updatedDomainOrder)

        // THEN
        actual
            .test()
            .expectNext(updatedDomainOrder)
            .verifyComplete()
        verify(exactly = 1) { orderRepository.updateOrder(updatedDomainOrder) }
    }

    @Test
    fun `should throw exception if order not exists on update`() {
        // GIVEN
        every { orderRepository.updateOrder(updatedDomainOrder) } returns updatedDomainOrder.toMono()
        every { orderRepository.findById(updatedDomainOrder.id!!) } returns Mono.empty()

        // WHEN
        val actual = orderService.updateOrder(updatedDomainOrder)

        // THEN
        actual
            .test()
            .expectError(NotFoundException::class.java)
            .verify()
    }

    @Test
    fun `should delete order`() {
        // GIVEN
        every { orderRepository.deleteById(randomOrderId) } returns Mono.empty()

        // WHEN
        val actual = orderService.delete(randomOrderId)

        // THEN
        actual
            .test()
            .verifyComplete()
        verify(exactly = 1) { orderRepository.deleteById(randomOrderId) }
    }

    @Test
    fun `should update order's status`() {
        // GIVEN
        every { orderRepository.updateOrderStatus(randomOrderId, DomainOrder.Status.COMPLETED) }
            .returns(domainOrder.copy(status = DomainOrder.Status.COMPLETED).toMono())
        every { kafkaUpdateOrderStatusSender.sendOrderUpdateStatus(any()) } returns Unit.toMono()

        // WHEN
        val actual = orderService.updateOrderStatus(randomOrderId, DomainOrder.Status.COMPLETED)

        // THEN
        actual
            .test()
            .expectNext(domainOrder.copy(status = DomainOrder.Status.COMPLETED))
            .verifyComplete()
    }

    @Test
    fun `should return all user's orders by user id`() {
        // GIVEN
        every { orderRepository.findAllByUserId(randomOrderId) } returns Flux.just(domainOrder)

        // WHEN
        val actual = orderService.getAllByUserId(randomOrderId)

        // THEN
        actual
            .test()
            .expectNext(domainOrder)
            .verifyComplete()
    }
}
