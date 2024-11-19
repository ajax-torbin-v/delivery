package com.example.domainservice.product.infrastructure.repository

import com.example.domainservice.ProductFixture.partialUpdate
import com.example.domainservice.ProductFixture.unsavedDomainProduct
import com.example.domainservice.ProductFixture.updatedDomainProduct
import com.example.domainservice.product.AbstractIntegrationTest
import com.example.domainservice.product.application.mapper.ProductMapper.applyPartialUpdate
import com.example.domainservice.product.application.port.output.ProductRepositoryOutputPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import reactor.kotlin.test.test
import reactor.test.StepVerifier

class MongoProductRepositoryTest : AbstractIntegrationTest() {
    @Autowired
    @Qualifier("mongoProductRepository")
    private lateinit var productRepositoryOutputPort: ProductRepositoryOutputPort

    @Test
    fun `save should save product and assign id`() {
        // GIVEN // WHEN
        val actual = productRepositoryOutputPort.save(unsavedDomainProduct)

        // THEN
        actual
            .test()
            .assertNext { savedProduct ->
                assertNotNull(savedProduct.id, "Id should be assigned by a db!")
                assertEquals(unsavedDomainProduct, savedProduct.copy(id = null))
            }
            .verifyComplete()
    }

    @Test
    fun `findById should return saved product`() {
        // GIVEN
        val savedProduct = productRepositoryOutputPort.save(unsavedDomainProduct).block()!!

        // WHEN
        val actual = productRepositoryOutputPort.findById(savedProduct.id.toString())

        // THEN
        actual
            .test()
            .expectNext(savedProduct)
            .verifyComplete()
    }

    @Test
    fun `deleteById should delete product by id`() {
        // GIVEN
        val savedProduct = productRepositoryOutputPort.save(unsavedDomainProduct).block()

        // WHEN //THEN
        productRepositoryOutputPort.deleteById(savedProduct?.id.toString())
            .test()
            .expectNext(Unit)
            .verifyComplete()

        // AND THEN
        productRepositoryOutputPort.findById(savedProduct?.id.toString())
            .test()
            .verifyComplete()
    }

    @Test
    fun `update should update product`() {
        // GIVEN
        val savedProduct = productRepositoryOutputPort.save(unsavedDomainProduct).block()!!

        println("###")
        println(savedProduct)
        println(partialUpdate.copy(id = savedProduct.id))
        println(savedProduct.applyPartialUpdate(partialUpdate.copy(id = savedProduct.id)))

        // WHEN
        val actual = productRepositoryOutputPort.update(
            savedProduct.applyPartialUpdate(
                partialUpdate.copy(id = savedProduct.id)
            )
        )

        // THEN
        actual
            .test()
            .expectNext(updatedDomainProduct.copy(id = savedProduct.id))
            .verifyComplete()
    }

    @Test
    fun `updateProductsAmount should update products`() {
        // GIVEN
        val savedProduct1 = productRepositoryOutputPort.save(unsavedDomainProduct).block()!!
        val savedProduct2 = productRepositoryOutputPort.save(unsavedDomainProduct).block()!!
        val items = mapOf(savedProduct1.id!! to 1, savedProduct2.id!! to 3)

        // WHEN
        val actual = productRepositoryOutputPort.updateProductsAmount(items)

        // THEN
        StepVerifier.create(actual)
            .expectNext(Unit)
            .verifyComplete()

        // AND THEN
        productRepositoryOutputPort.findById(savedProduct1.id.toString())
            .test()
            .assertNext { product ->
                assertEquals(savedProduct1.amountAvailable - 1, product.amountAvailable)
            }

        productRepositoryOutputPort.findById(savedProduct2.id.toString())
            .test()
            .assertNext { product ->
                assertEquals(savedProduct2.amountAvailable - 3, product.amountAvailable)
            }
    }

    @Test
    fun `findAllByIds should return list of products by ids`() {
        // GIVEN
        val product1 = productRepositoryOutputPort.save(unsavedDomainProduct).block()!!
        val product2 = productRepositoryOutputPort.save(unsavedDomainProduct).block()!!
        val productIds = listOf(product1.id.toString(), product2.id.toString())

        // WHEN
        val actual = productRepositoryOutputPort.findAllByIds(productIds)

        // THEN
        actual
            .test()
            .expectNextMatches { it.id == product1.id }
            .expectNextMatches { it.id == product2.id }
            .verifyComplete()
    }
}
