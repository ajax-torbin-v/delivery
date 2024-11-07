package com.example.delivery.repository

import com.example.delivery.AbstractIntegrationTest
import com.example.delivery.ProductFixture.unsavedProduct
import com.example.delivery.ProductFixture.updateProductObject
import com.example.delivery.ProductFixture.updatedProduct
import com.example.delivery.mongo.MongoOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import reactor.test.StepVerifier

class ProductRepositoryTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var productRepository: ProductRepository

    @Test
    fun `save should save product and assign id`() {
        // GIVEN // WHEN
        val actual = productRepository.save(unsavedProduct)

        // THEN
        actual
            .test()
            .assertNext { savedProduct ->
                assertNotNull(savedProduct.id, "Id should be assigned by a db!")
                assertEquals(unsavedProduct, savedProduct.copy(id = null))
            }
            .verifyComplete()
    }

    @Test
    fun `findById should return saved product`() {
        // GIVEN
        val savedProduct = productRepository.save(unsavedProduct).block()!!

        // WHEN
        val actual = productRepository.findById(savedProduct.id.toString())

        // THEN
        actual
            .test()
            .expectNext(savedProduct)
            .verifyComplete()
    }

    @Test
    fun `deleteById should delete product by id`() {
        // GIVEN
        val savedProduct = productRepository.save(unsavedProduct).block()

        // WHEN //THEN
        productRepository.deleteById(savedProduct?.id.toString())
            .test()
            .expectNext(Unit)
            .verifyComplete()

        // AND THEN
        productRepository.findById(savedProduct?.id.toString())
            .test()
            .verifyComplete()
    }

    @Test
    fun `update should update product`() {
        // GIVEN
        val savedProduct = productRepository.save(unsavedProduct).block()

        // WHEN
        val actual = productRepository.update(savedProduct?.id.toString(), updateProductObject)

        // THEN
        actual
            .test()
            .assertNext { product ->
                assertEquals(
                    updatedProduct.copy(id = savedProduct?.id),
                    product
                )
            }
            .verifyComplete()
    }

    @Test
    fun `updateProductsAmount should update products`() {
        // GIVEN
        val savedProduct1 = productRepository.save(unsavedProduct).block()
        val savedProduct2 = productRepository.save(unsavedProduct).block()
        val mongoOrderItem1 = MongoOrder.MongoOrderItem(savedProduct1?.id, null, 1)
        val mongoOrderItem2 = MongoOrder.MongoOrderItem(savedProduct2?.id, null, 3)

        // WHEN
        val actual = productRepository.updateProductsAmount(listOf(mongoOrderItem1, mongoOrderItem2))

        // THEN
        StepVerifier.create(actual)
            .expectNext(Unit)
            .verifyComplete()

        // AND THEN
        productRepository.findById(savedProduct1?.id.toString())
            .test()
            .assertNext { product ->
                assertEquals(savedProduct1?.amountAvailable!! - 1, product.amountAvailable)
            }

        productRepository.findById(savedProduct2?.id.toString())
            .test()
            .assertNext { product ->
                assertEquals(savedProduct2?.amountAvailable!! - 3, product.amountAvailable)
            }
    }

    @Test
    fun `findAllByIds should return list of products by ids`() {
        // GIVEN
        val product1 = productRepository.save(unsavedProduct).block()
        val product2 = productRepository.save(unsavedProduct).block()
        val productIds = listOf(product1?.id.toString(), product2?.id.toString())

        // WHEN
        val actual = productRepository.findAllByIds(productIds)

        // THEN
        actual
            .test()
            .expectNextMatches { it.id == product1?.id }
            .expectNextMatches { it.id == product2?.id }
            .verifyComplete()
    }
}
