package com.example.delivery.controller

import com.example.delivery.ProductFixture.createProductDTO
import com.example.delivery.ProductFixture.domainProduct
import com.example.delivery.ProductFixture.updateProductDTO
import com.example.delivery.ProductFixture.updatedDomainProduct
import com.example.delivery.mapper.ProductMapper.toDTO
import com.example.delivery.service.ProductService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
internal class ProductControllerTest {
    @MockK
    private lateinit var productService: ProductService

    @InjectMockKs
    private lateinit var productController: ProductController

    @Test
    fun `should return product when product exists`() {
        // GIVEN
        every { productService.getById("123") } returns domainProduct.toMono()

        // WHEN
        val actual = productController.findById("123")

        // THEN
        actual
            .test()
            .expectNext(domainProduct.toDTO())
            .verifyComplete()
    }

    @Test
    fun `should add product and return status created`() {
        // GIVEN
        every { productService.add(createProductDTO) } returns domainProduct.toMono()

        // WHEN
        val actual = productController.add(createProductDTO)

        // THEN
        actual
            .test()
            .expectNext(domainProduct.toDTO())
            .verifyComplete()
    }

    @Test
    fun `should update product with proper dto`() {
        // GIVEN
        every { productService.update("1", updateProductDTO) } returns updatedDomainProduct.toMono()

        // WHEN
        val actual = productController.update("1", updateProductDTO)

        // THEN
        actual
            .test()
            .expectNext(updatedDomainProduct.toDTO())
            .verifyComplete()
    }

    @Test
    fun `should delete product and return no content`() {
        // GIVEN
        every { (productService).deleteById("123") } returns Mono.empty()

        // WHEN
        val actual = productController.delete("123")

        // THEN
        actual
            .test()
            .verifyComplete()

        verify(exactly = 1) { productService.deleteById("123") }
    }
}
