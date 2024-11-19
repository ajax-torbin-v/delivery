package com.example.domainservice.order.infrastructure.mongo

import com.example.domainservice.OrderFixture.domainOrderWithProduct
import com.example.domainservice.OrderFixture.unsavedDomainOrder
import com.example.domainservice.ProductFixture.product
import com.example.domainservice.ProductFixture.unsavedDomainProduct
import com.example.domainservice.UserFixture.unsavedDomainUser
import com.example.domainservice.order.AbstractIntegrationTest
import com.example.domainservice.order.application.port.output.OrderRepositoryOutputPort
import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.domain.projection.DomainOrderWithProduct
import com.example.domainservice.product.application.port.output.ProductRepositoryOutputPort
import com.example.domainservice.user.infrastructure.mongo.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kotlin.test.test
import java.math.BigDecimal

class OrderRepositoryTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var orderRepository: OrderRepositoryOutputPort

    @Autowired
    @Qualifier("redisProductRepository")
    private lateinit var productRepository: ProductRepositoryOutputPort

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `save should save order and assign id`() {
        // GIVEN //WHEN
        val actual = orderRepository.save(unsavedDomainOrder)

        // THEN
        actual.test().assertNext { product ->
            assertNotNull(product.id, "Id should not be null after save!")
            assertEquals(product.copy(id = null), unsavedDomainOrder)
        }.verifyComplete()
    }

    @Test
    fun `findById should return saved order`() {
        // GIVEN
        val savedProduct = productRepository.save(unsavedDomainProduct).block()!!
        val savedOrder = orderRepository.save(
            unsavedDomainOrder.copy(
                items = listOf(
                    DomainOrder.DomainOrderItem(
                        savedProduct.id!!,
                        BigDecimal.TEN,
                        product.amountAvailable!!
                    )
                )
            )
        ).block()

        // WHEN
        val actual = orderRepository.findByIdFull(savedOrder?.id.toString())

        // THEN
        actual.test().assertNext { order ->
            assertEquals(
                domainOrderWithProduct.copy(
                    id = order.id,
                    items = listOf(
                        DomainOrderWithProduct.DomainOrderItemWithProduct(
                            savedProduct,
                            BigDecimal.TEN,
                            product.amountAvailable!!
                        )
                    )
                ),
                order.copy()
            )
        }.verifyComplete()
    }

    @Test
    fun `existsById should return if order exists`() {
        // GIVEN
        val savedOrder = orderRepository.save(unsavedDomainOrder).block()

        // WHEN
        val actual = orderRepository.existsById(savedOrder?.id.toString())

        // THEN
        actual.test().expectNext(true).verifyComplete()
    }

    @Test
    fun `deleteById should delete order by id`() {
        // GIVEN
        val savedOrder = orderRepository.save(unsavedDomainOrder).block()

        // WHEN // THEN
        orderRepository.deleteById(savedOrder?.id.toString()).test().expectNext(Unit).verifyComplete()

        // AND THEN
        orderRepository.existsById(savedOrder?.id.toString()).test().expectNext(false).verifyComplete()
    }

    @Test
    fun `updateOrderStatus should update the order status`() {
        // GIVEN
        val savedOrder = orderRepository.save(unsavedDomainOrder).block()

        // WHEN
        val actual = orderRepository.updateOrderStatus(
            savedOrder?.id.toString(),
            DomainOrder.Status.COMPLETED
        )

        // THEN
        actual.test().assertNext { order -> assertEquals(DomainOrder.Status.COMPLETED, order.status) }.verifyComplete()
    }

    @Test
    fun `updateOrder should update the order`() {
        // GIVEN
        val savedOrder = orderRepository.save(unsavedDomainOrder).block()!!

        // WHEN
        val actual = orderRepository.updateOrderStatus(savedOrder.id!!, DomainOrder.Status.SHIPPING)

        // THEN
        actual.test().assertNext { order ->
            assertEquals(DomainOrder.Status.SHIPPING, order.status)
        }.verifyComplete()
    }

    @Test
    fun `findAllByUserId should return all orders`() {
        // GIVEN
        val savedUser = userRepository.save(unsavedDomainUser).block()!!
        val savedOrder = orderRepository.save(unsavedDomainOrder.copy(userId = savedUser.id!!)).block()

        // WHEN
        val actual = orderRepository.findAllByUserId(savedUser.id.toString())

        // THEN
        actual.test().assertNext { order -> assertEquals(savedOrder, order) }.verifyComplete()
    }
}
