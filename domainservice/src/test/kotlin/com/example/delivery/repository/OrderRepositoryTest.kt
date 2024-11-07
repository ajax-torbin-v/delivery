package com.example.delivery.repository

import com.example.delivery.AbstractIntegrationTest
import com.example.delivery.OrderFixture.mongoOrderWithProduct
import com.example.delivery.OrderFixture.unsavedOrder
import com.example.delivery.ProductFixture.product
import com.example.delivery.ProductFixture.unsavedProduct
import com.example.delivery.UserFixture.unsavedUser
import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.projection.MongoOrderWithProduct
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Update
import reactor.kotlin.test.test
import java.math.BigDecimal

class OrderRepositoryTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `save should save order and assign id`() {
        // GIVEN //WHEN
        val actual = orderRepository.save(unsavedOrder)

        // THEN
        actual.test().assertNext { product ->
            assertNotNull(product.id, "Id should not be null after save!")
            assertEquals(product.copy(id = null), unsavedOrder)
        }.verifyComplete()
    }

    @Test
    fun `findById should return saved order`() {
        // GIVEN
        val savedProduct = productRepository.save(unsavedProduct).block()
        val savedOrder = orderRepository.save(
            unsavedOrder.copy(
                items = listOf(
                    MongoOrder.MongoOrderItem(
                        savedProduct?.id,
                        BigDecimal.TEN,
                        product.amountAvailable
                    )
                )
            )
        ).block()

        // WHEN
        val actual = orderRepository.findById(savedOrder?.id.toString())

        // THEN
        actual.test().assertNext { order ->
            assertEquals(
                mongoOrderWithProduct.copy(
                    id = order.id,
                    items = listOf(
                        MongoOrderWithProduct.MongoOrderItemWithProduct(
                            savedProduct,
                            BigDecimal.TEN,
                            product.amountAvailable
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
        val savedOrder = orderRepository.save(unsavedOrder).block()

        // WHEN
        val actual = orderRepository.existsById(savedOrder?.id.toString())

        // THEN
        actual.test().expectNext(true).verifyComplete()
    }

    @Test
    fun `deleteById should delete order by id`() {
        // GIVEN
        val savedOrder = orderRepository.save(unsavedOrder).block()

        // WHEN // THEN
        orderRepository.deleteById(savedOrder?.id.toString()).test().expectNext(Unit).verifyComplete()

        // AND THEN
        orderRepository.existsById(savedOrder?.id.toString()).test().expectNext(false).verifyComplete()
    }

    @Test
    fun `updateOrderStatus should update the order status`() {
        // GIVEN
        val savedOrder = orderRepository.save(unsavedOrder).block()

        // WHEN
        val actual = orderRepository.updateOrderStatus(
            savedOrder?.id.toString(),
            MongoOrder.Status.COMPLETED
        )

        // THEN
        actual.test().assertNext { order -> assertEquals(MongoOrder.Status.COMPLETED, order.status) }.verifyComplete()
    }

    @Test
    fun `updateOrder should update the order`() {
        // GIVEN
        val savedOrder = orderRepository.save(unsavedOrder).block()
        val update = Update().set("status", MongoOrder.Status.SHIPPING)

        // WHEN
        val actual = orderRepository.updateOrder(savedOrder?.id.toString(), update)

        // THEN
        actual.test().assertNext { order ->
            assertEquals(MongoOrder.Status.SHIPPING, order.status)
        }.verifyComplete()
    }

    @Test
    fun `findAllByUserId should return all orders`() {
        // GIVEN
        val savedUser = userRepository.save(unsavedUser).block()
        val savedOrder = orderRepository.save(unsavedOrder.copy(userId = savedUser?.id)).block()

        // WHEN
        val actual = orderRepository.findAllByUserId(savedUser?.id.toString())

        // THEN
        actual.test().assertNext { order -> assertEquals(savedOrder, order) }.verifyComplete()
    }
}
