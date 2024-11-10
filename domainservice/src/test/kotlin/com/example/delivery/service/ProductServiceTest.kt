package com.example.delivery.service

import com.example.core.ProductFixture.createProductDTO
import com.example.core.exception.NotFoundException
import com.example.delivery.ProductFixture.domainProduct
import com.example.delivery.ProductFixture.product
import com.example.delivery.repository.ProductRepository
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
    lateinit var productRepository: ProductRepository

    @InjectMockKs
    lateinit var productService: ProductService

    @Test
    fun `should add product with proper dto`() {
        // GIVEN
        every { productRepository.save(product.copy(id = null)) } returns product.toMono()

        // WHEN
        val actual = productService.add(createProductDTO)

        // THEN
        actual
            .test()
            .expectNext(domainProduct)
            .verifyComplete()

        verify(exactly = 1) { productRepository.save(product.copy(id = null)) }
    }

    @Test
    fun `should return product when product exists`() {
        // GIVEN
        every { productRepository.findById("1") } returns product.toMono()

        // WHEN
        val actual = productService.getById("1")

        // THEN
        actual
            .test()
            .expectNext(domainProduct)
            .verifyComplete()
    }

    @Test
    fun `should throw an exception when product don't exist`() {
        // GIVEN
        every { productRepository.findById("13") } returns Mono.empty()

        // WHEN
        val actual = productService.getById("13")

        // THEN
        actual
            .test()
            .expectError(NotFoundException::class.java)
    }
//
//    @Test
//    fun `should update product with proper dto when product exists`() {
//        // GIVEN
//        every { productRepository.update("1", updateProductObject) } returns updatedProduct.toMono()
//
//        // WHEN
//        val actual = productService.update("1", updateProductDTO)
//
//        // THEN
//        actual
//            .test()
//            .expectNext(updatedDomainProduct)
//            .verifyComplete()
//        verify(exactly = 1) { productRepository.update("1", updateProductObject) }
//    }
//
//    @Test
//    fun `should throw exception if product doesn't exists on update`() {
//        // GIVEN
//        every { productRepository.update("1", updateProductObject) } returns Mono.empty()
//
//        // WHEN
//        val actual = productService.update("1", updateProductDTO)
//
//        // THEN
//        actual
//            .test()
//            .expectError(NotFoundException::class.java)
//    }

    @Test
    fun `should be okay when deleting existing product`() {
        // GIVEN
        every { productRepository.deleteById("1") } returns Mono.empty()

        // WHEN
        val actual = productService.deleteById("1")

        // THEN
        actual
            .test()
            .verifyComplete()
        verify(exactly = 1) { productRepository.deleteById("1") }
    }
}
