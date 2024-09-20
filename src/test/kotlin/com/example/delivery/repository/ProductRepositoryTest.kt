package com.example.delivery.repository

import com.example.delivery.ProductFixture.unsavedProduct
import com.example.delivery.mongo.MongoOrder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Update
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductRepositoryTest : AbstractMongoTestContainer {
    @Autowired
    private lateinit var productRepository: ProductRepository

    @Test
    fun `save should save product and assign id`() {
        // GIVEN // WHEN
        val actual = productRepository.save(unsavedProduct)

        // THEN
        assertTrue(actual.id != null, "Id should not be null after save!")
    }

    @Test
    fun `findById should return saved product`() {
        // GIVEN
        val savedProduct = productRepository.save(unsavedProduct)

        // WHEN
        val actual = productRepository.findById(savedProduct.id.toString())

        // THEN
        assertEquals(savedProduct, actual)
    }

    @Test
    fun `existById should return if product exist`() {
        // GIVEN
        val savedProduct = productRepository.save(unsavedProduct)

        // WHEN
        val actual = productRepository.existsById(savedProduct.id.toString())

        // THEN
        assertTrue(actual, "Product should exist!")
    }

    @Test
    fun `findAll should return all saved products `() {
        // GIVEN
        val savedProduct1 = productRepository.save(unsavedProduct)
        val savedProduct2 = productRepository.save(unsavedProduct)

        // WHEN
        val actual = productRepository.findAll().contains(savedProduct1) &&
            productRepository.findAll().contains(savedProduct2)

        // THEN
        assertTrue(actual)
    }

    @Test
    fun `deleteById should delete product by id`() {
        // GIVEN
        val savedProduct = productRepository.save(unsavedProduct)

        // WHEN
        productRepository.deleteById(savedProduct.id.toString())

        // THEN
        assertTrue(!productRepository.existsById(savedProduct.id.toString()))
    }

    @Test
    fun `update should update product`() {
        // GIVEN
        val savedProduct = productRepository.save(unsavedProduct)
        val update = Update()
            .set("name", "UpdatedName")
            .set("price", BigDecimal.valueOf(99.99))
            .set("amountAvailable", 999)
            .set("measurement", "99L")

        // WHEN
        val actual = productRepository.update(
            savedProduct.id.toString(),
            update = update
        )

        // THEN
        assertEquals(
            savedProduct.copy(
                savedProduct.id,
                "UpdatedName",
                BigDecimal.valueOf(99.99),
                999,
                "99L"
            ),
            actual
        )
    }

    @Test
    fun `updateProductsAmount should update products`() {
        // GIVEN
        val savedProduct1 = productRepository.save(unsavedProduct)
        val savedProduct2 = productRepository.save(unsavedProduct)
        val mongoOrderItem1 = MongoOrder.MongoOrderItem(savedProduct1.id, null, 1)
        val mongoOrderItem2 = MongoOrder.MongoOrderItem(savedProduct2.id, null, 1)

        // WHEN
        productRepository.updateProductsAmount(listOf(mongoOrderItem1, mongoOrderItem2))

        // THEN
        assertEquals(
            savedProduct1.amountAvailable!! - 1,
            productRepository.findById(savedProduct1.id.toString())?.amountAvailable
        )
        assertEquals(
            savedProduct2.amountAvailable!! - 1,
            productRepository.findById(savedProduct2.id.toString())?.amountAvailable
        )
    }
}
