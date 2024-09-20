package com.example.delivery.repository

import com.example.delivery.OrderFixture.mongoOrderWithProduct
import com.example.delivery.OrderFixture.unsavedOrder
import com.example.delivery.ProductFixture.unsavedProduct
import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.MongoOrderWithProduct
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Update
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrderRepositoryTest : AbstractMongoTestContainer {
    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Test
    fun `save should save order and assign id`() {
        // GIVEN //WHEN
        val actual = orderRepository.save(unsavedOrder)

        // THEN
        assertTrue(actual.id != null, "Id should not be null after save!")
    }

    @Test
    fun `findById should return saved order`() {
        // GIVEN
        val product = productRepository.save(unsavedProduct)
        val savedOrder = orderRepository.save(
            unsavedOrder.copy(
                items = listOf(
                    MongoOrder.MongoOrderItem(
                        product.id,
                        product.price,
                        product.amountAvailable
                    )
                )
            )
        )

        // WHEN
        val actual = orderRepository.findById(savedOrder.id.toString())

        // THEN
        assertEquals(
            mongoOrderWithProduct.copy(
                id = savedOrder.id,
                items = listOf(
                    MongoOrderWithProduct.MongoOrderItemWithProduct(
                        product,
                        product.price,
                        product.amountAvailable
                    )
                )
            ), actual
        )
    }

    @Test
    fun `existsById should return if order exists`() {
        // GIVEN
        val savedOrder = orderRepository.save(unsavedOrder)

        // WHEN
        val actual = orderRepository.existsById(savedOrder.id.toString())

        // THEN
        assertTrue(actual, "Order should exist!")
    }

    @Test
    fun `deleteById should delete order by id`() {
        // GIVEN
        val savedOrder = orderRepository.save(unsavedOrder)

        // WHEN
        orderRepository.deleteById(savedOrder.id.toString())

        // THEN
        assertTrue(!orderRepository.existsById(savedOrder.id.toString()))
    }

    @Test
    fun `updateOrderStatus should update the order status`() {
        // GIVEN
        val savedOrder = orderRepository.save(unsavedOrder)

        // WHEN
        val updatedOrder = orderRepository.updateOrderStatus(
            savedOrder.id.toString(),
            MongoOrder.Status.COMPLETED
        )

        // THEN
        assertEquals(MongoOrder.Status.COMPLETED, updatedOrder?.status)
    }

    @Test
    fun `updateOrder should update the order`() {
        // GIVEN
        val savedOrder = orderRepository.save(unsavedOrder)
        val update = Update().set("status", MongoOrder.Status.SHIPPING)

        // WHEN
        val updatedOrder = orderRepository.updateOrder(savedOrder.id.toString(), update)

        // THEN
        assertEquals(MongoOrder.Status.SHIPPING, updatedOrder?.status)
    }

    @Test
    fun `fetchProducts should return list of products by ids`() {
        // GIVEN
        val product1 = productRepository.save(unsavedProduct)
        val product2 = productRepository.save(unsavedProduct)
        val productIds = listOf(product1.id.toString(), product2.id.toString())

        // WHEN
        val products = orderRepository.fetchProducts(productIds)

        // THEN
        assertTrue(products.any { it.id == product1.id } && products.any { it.id == product2.id })
    }
}
