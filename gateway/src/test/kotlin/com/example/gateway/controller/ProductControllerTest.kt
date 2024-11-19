package com.example.gateway.controller

import com.example.core.ProductFixture.randomProductId
import com.example.core.UserFixture.randomUserId
import com.example.core.exception.ProductNotFoundException
import com.example.gateway.ProductProtoFixture.createProductDTO
import com.example.gateway.ProductProtoFixture.createProductResponse
import com.example.gateway.ProductProtoFixture.createProductResponseWithUnexpectedException
import com.example.gateway.ProductProtoFixture.deleteProductRequest
import com.example.gateway.ProductProtoFixture.deleteProductResponseWithUnexpectedException
import com.example.gateway.ProductProtoFixture.findProductByIdRequest
import com.example.gateway.ProductProtoFixture.findProductByIdResponse
import com.example.gateway.ProductProtoFixture.findProductByIdResponseWithProductNotFoundException
import com.example.gateway.ProductProtoFixture.findProductByIdResponseWithUnexpectedException
import com.example.gateway.ProductProtoFixture.updateProductDTO
import com.example.gateway.ProductProtoFixture.updateProductResponse
import com.example.gateway.application.port.output.ProductOutputPort
import com.example.gateway.infrastructure.mapper.ProductProtoMapper.toCreateProductRequest
import com.example.gateway.infrastructure.mapper.ProductProtoMapper.toDTO
import com.example.gateway.infrastructure.mapper.ProductProtoMapper.updateProductRequest
import com.example.gateway.infrastructure.rest.ProductController
import com.example.internal.input.reqreply.product.CreateProductResponse
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError

@ExtendWith(MockKExtension::class)
class ProductControllerTest {
    @MockK
    private lateinit var productOutputPort: ProductOutputPort

    @InjectMockKs
    private lateinit var productController: ProductController

    @Test
    fun `save should return product DTO`() {
        // GIVEN
        every {
            productOutputPort.create(createProductDTO.toCreateProductRequest())
        } returns createProductResponse.toMono()

        // WHEN //THEN
        productController.add(createProductDTO)
            .test()
            .expectNext(createProductResponse.toDTO())
            .verifyComplete()
    }

    @Test
    fun `save should rethrow exception when message contains unexpected error`() {
        // GIVEN
        every {
            productOutputPort.create(createProductDTO.toCreateProductRequest())
        } returns createProductResponseWithUnexpectedException.toMono()

        // WHEN //THEN
        productController.add(createProductDTO)
            .test()
            .verifyError(IllegalStateException::class)
    }

    @Test
    fun `save should throw exception when message is empty`() {
        // GIVEN
        every {
            productOutputPort.create(createProductDTO.toCreateProductRequest())
        } returns CreateProductResponse.getDefaultInstance().toMono()

        // WHEN //THEN
        productController.add(createProductDTO)
            .test()
            .verifyError(RuntimeException::class)
    }

    @Test
    fun `findById should return existing product`() {
        // GIVEN
        every {
            productOutputPort.findById(findProductByIdRequest)
        } returns findProductByIdResponse.toMono()

        // WHEN // THEN
        productController.findById(randomProductId)
            .test()
            .expectNext(findProductByIdResponse.toDTO())
            .verifyComplete()
    }

    @Test
    fun `findById should throw exception when product doesn't exist`() {
        // GIVEN
        every {
            productOutputPort.findById(findProductByIdRequest)
        } returns findProductByIdResponseWithProductNotFoundException.toMono()

        // WHEN // THEN
        productController.findById(randomProductId)
            .test()
            .verifyError(ProductNotFoundException::class)
    }

    @Test
    fun `findById should rethrow unexpected exception`() {
        // GIVEN
        every {
            productOutputPort.findById(findProductByIdRequest)
        } returns findProductByIdResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        productController.findById(randomProductId)
            .test()
            .verifyError(IllegalStateException::class)
    }

    @Test
    fun `findById should throw exception when message is empty`() {
        // GIVEN
        every {
            productOutputPort.findById(findProductByIdRequest)
        } returns FindProductByIdResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        productController.findById(randomProductId)
            .test()
            .verifyError(RuntimeException::class)
    }

    @Test
    fun `update should return updated product`() {
        // GIVEN
        every {
            productOutputPort.update(updateProductRequest(randomUserId, updateProductDTO))
        } returns updateProductResponse.toMono()

        // WHEN // THEN
        productController.update(randomUserId, updateProductDTO)
            .test()
            .expectNext(updateProductResponse.toDTO())
            .verifyComplete()
    }

    @Test
    fun `delete should rethrow unexpected exception`() {
        // GIVEN
        every {
            productOutputPort.delete(deleteProductRequest)
        } returns deleteProductResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        productController.delete(randomProductId)
            .test()
            .verifyError(IllegalStateException::class)
    }
}
