package com.example.domainservice.product.application.service

import com.example.core.ProductFixture.randomProductId
import com.example.core.exception.NotFoundException
import com.example.domainservice.ProductFixture.domainProduct
import com.example.domainservice.ProductFixture.partialUpdate
import com.example.domainservice.ProductFixture.unsavedDomainProduct
import com.example.domainservice.ProductFixture.updatedDomainProduct
import com.example.domainservice.product.application.port.output.ProductRepositoryOutputPort
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
internal class ProductServiceTest {
    @MockK
    lateinit var productRepository: ProductRepositoryOutputPort

    @InjectMockKs
    lateinit var productService: ProductService

    @Test
    fun `should add product with proper dto`() {
        // GIVEN
        every { productRepository.save(unsavedDomainProduct) } returns domainProduct.toMono()

        // WHEN
        val actual = productService.save(unsavedDomainProduct)

        // THEN
        actual
            .test()
            .expectNext(domainProduct)
            .verifyComplete()

        verify(exactly = 1) { productRepository.save(unsavedDomainProduct) }
    }

    @Test
    fun `should return product when product exists`() {
        // GIVEN
        every { productRepository.findById(randomProductId) } returns domainProduct.toMono()

        // WHEN
        val actual = productService.getById(randomProductId)

        // THEN
        actual
            .test()
            .expectNext(domainProduct)
            .verifyComplete()
    }

    @Test
    fun `should throw an exception when product don't exist`() {
        // GIVEN
        every { productRepository.findById(randomProductId) } returns Mono.empty()

        // WHEN
        val actual = productService.getById(randomProductId)

        // THEN
        actual
            .test()
            .expectError(NotFoundException::class.java)
    }

    @Test
    fun `should update product with proper dto when product exists`() {
        // GIVEN
        every { productRepository.findById(randomProductId) } returns domainProduct.toMono()
        every { productRepository.update(any()) } returns updatedDomainProduct.toMono()

        // WHEN
        val actual = productService.update(partialUpdate)

        // THEN
        actual
            .test()
            .expectNext(updatedDomainProduct)
            .verifyComplete()
    }

    @Test
    fun `should throw exception if product doesn't exists on update`() {
        // GIVEN
        every { productRepository.findById(randomProductId) } returns Mono.empty()

        // WHEN
        val actual = productService.update(partialUpdate)

        // THEN
        actual
            .test()
            .expectError(NotFoundException::class.java)
    }

    @Test
    fun `should be okay when deleting existing product`() {
        // GIVEN
        every { productRepository.deleteById(randomProductId) } returns Mono.empty()

        // WHEN
        val actual = productService.deleteById(randomProductId)

        // THEN
        actual
            .test()
            .verifyComplete()
        verify(exactly = 1) { productRepository.deleteById(randomProductId) }
    }
}
