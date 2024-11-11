package com.example.delivery.service

import com.example.core.OrderFixture.createOrderDTO
import com.example.core.OrderFixture.randomOrderId
import com.example.core.OrderFixture.updateOrderDTO
import com.example.core.ProductFixture.randomProductId
import com.example.core.UserFixture.randomUserId
import com.example.core.dto.request.CreateOrderDTO
import com.example.core.dto.request.CreateOrderItemDTO
import com.example.core.dto.response.ShipmentDetailsDTO
import com.example.core.exception.NotFoundException
import com.example.core.exception.ProductAmountException
import com.example.delivery.OrderFixture.domainOrder
import com.example.delivery.OrderFixture.domainOrderWithProduct
import com.example.delivery.OrderFixture.mongoOrderWithProduct
import com.example.delivery.OrderFixture.order
import com.example.delivery.OrderFixture.orderUpdateObject
import com.example.delivery.OrderFixture.updatedDomainOrder
import com.example.delivery.OrderFixture.updatedOrder
import com.example.delivery.ProductFixture.product
import com.example.delivery.UserFixture.user
import com.example.delivery.kafka.OrderUpdateStatusProducer
import com.example.delivery.mapper.OrderMapper.toDomain
import com.example.delivery.mongo.MongoOrder
import com.example.delivery.repository.OrderRepository
import com.example.delivery.repository.ProductRepository
import com.example.delivery.repository.UserRepository
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
    private lateinit var orderRepository: OrderRepository

    @MockK
    private lateinit var productRepository: ProductRepository

    @MockK
    private lateinit var kafkaUpdateOrderStatusSender: OrderUpdateStatusProducer

    @MockK
    private lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var orderService: OrderService

    @Test
    fun `should return order when order exists`() {
        // GIVEN
        every { orderRepository.findById(randomOrderId) } returns mongoOrderWithProduct.toMono()

        // WHEN
        val actual = orderService.getById(randomOrderId)

        // THEN
        actual
            .test()
            .expectNext(domainOrderWithProduct)
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
        every { orderRepository.save(any()) } returns Mono.just(order)
        every { userRepository.findById(randomUserId) } returns user.toMono()
        every { productRepository.findAllByIds(listOf(randomProductId)) } returns Flux.just(product)
        every { productRepository.updateProductsAmount(any()) } returns Unit.toMono()

        // WHEN
        val actual = orderService.add(createOrderDTO)

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
        val createOrderDTO = CreateOrderDTO(
            items = listOf(
                CreateOrderItemDTO(randomProductId.reversed(), 0),
                CreateOrderItemDTO(randomProductId, 0)
            ),
            shipmentDetails = ShipmentDetailsDTO(
                city = "city",
                street = "street",
                building = "3a",
                index = "54890",
            ),
            userId = randomUserId
        )

        val productsIdsList = listOf(randomProductId.reversed(), randomProductId)
        every { userRepository.findById(randomUserId) } returns user.toMono()
        every { productRepository.findAllByIds(productsIdsList) } returns Flux.just(product)

        // WHEN
        val actual = orderService.add(createOrderDTO)

        // THEN
        actual
            .test()
            .expectError(NotFoundException::class.java)
            .verify()
    }

    @Test
    fun `should throw exception when not sufficient product`() {
        // GIVEN
        every { userRepository.findById(randomUserId) } returns user.toMono()
        every { productRepository.findAllByIds(listOf(randomProductId)) }
            .returns(Flux.just(product.copy(amountAvailable = -1)))

        // WHEN
        val actual = orderService.add(createOrderDTO)

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
        val actual = orderService.add(createOrderDTO)

        // THEN
        actual
            .test()
            .expectError(NotFoundException::class.java)
            .verify()
    }

    @Test
    fun `should update order with proper dto when product exists`() {
        // GIVEN
        every { orderRepository.updateOrder(randomOrderId, orderUpdateObject) } returns updatedOrder.toMono()

        // WHEN
        val actual = orderService.updateOrder(randomOrderId, updateOrderDTO)

        // THEN
        actual
            .test()
            .expectNext(updatedDomainOrder)
            .verifyComplete()
        verify(exactly = 1) { orderRepository.updateOrder(randomOrderId, orderUpdateObject) }
    }

    @Test
    fun `should throw exception if order not exists on update`() {
        // GIVEN
        every { orderRepository.updateOrder(randomOrderId, orderUpdateObject) } returns Mono.empty()

        // WHEN
        val actual = orderService.updateOrder(randomOrderId, updateOrderDTO)

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
        val actual = orderService.deleteById(randomOrderId)

        // THEN
        actual
            .test()
            .verifyComplete()
        verify(exactly = 1) { orderRepository.deleteById(randomOrderId) }
    }

    @Test
    fun `should update order's status`() {
        // GIVEN
        every { orderRepository.updateOrderStatus(randomOrderId, MongoOrder.Status.COMPLETED) }
            .returns(order.copy(status = MongoOrder.Status.COMPLETED).toMono())
        every { kafkaUpdateOrderStatusSender.sendOrderUpdateStatus(any()) } returns Unit.toMono()

        // WHEN
        val actual = orderService.updateOrderStatus(randomOrderId, "COMPLETED")

        // THEN
        actual
            .test()
            .expectNext(order.copy(status = MongoOrder.Status.COMPLETED).toDomain())
            .verifyComplete()
    }

    @Test
    fun `should return all user's orders by user id`() {
        // GIVEN
        every { orderRepository.findAllByUserId(randomOrderId) } returns Flux.just(order)

        // WHEN
        val actual = orderService.getAllByUserId(randomOrderId)

        // THEN
        actual
            .test()
            .expectNext(domainOrder)
            .verifyComplete()
    }
}
