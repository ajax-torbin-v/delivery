package com.example.gateway.controller

import com.example.core.ProductFixture.createProductDTO
import com.example.core.ProductFixture.randomProductId
import com.example.core.ProductFixture.updateProductDTO
import com.example.core.UserFixture.randomUserId
import com.example.core.exception.ProductNotFoundException
import com.example.gateway.ProductProtoFixture.createProductResponse
import com.example.gateway.ProductProtoFixture.createProductResponseWithUnexpectedException
import com.example.gateway.ProductProtoFixture.deleteProductRequest
import com.example.gateway.ProductProtoFixture.deleteProductResponseWithUnexpectedException
import com.example.gateway.ProductProtoFixture.findProductByIdRequest
import com.example.gateway.ProductProtoFixture.findProductByIdResponse
import com.example.gateway.ProductProtoFixture.findProductByIdResponseWithProductNotFoundException
import com.example.gateway.ProductProtoFixture.findProductByIdResponseWithUnexpectedException
import com.example.gateway.ProductProtoFixture.updateProductResponse
import com.example.gateway.mapper.ProductProtoMapper.toCreateProductRequest
import com.example.gateway.mapper.ProductProtoMapper.toDTO
import com.example.gateway.mapper.ProductProtoMapper.updateProductRequest
import com.example.gateway.rest.ProductController
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.product.CreateProductResponse
import com.example.internal.input.reqreply.product.DeleteProductResponse
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import com.example.internal.input.reqreply.product.UpdateProductResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import reactor.kotlin.test.verifyError
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@ExtendWith(MockKExtension::class)
class ProductControllerTest {
    @MockK
    private lateinit var natsMessagePublisher: NatsMessagePublisher

    @InjectMockKs
    private lateinit var productController: ProductController

    @Test
    fun `save should return product DTO`() {
        // GIVEN
        every {
            natsMessagePublisher.request(
                NatsSubject.Product.SAVE,
                payload = createProductDTO.toCreateProductRequest(),
                parser = CreateProductResponse.parser()
            )
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
            natsMessagePublisher.request(
                NatsSubject.Product.SAVE,
                payload = createProductDTO.toCreateProductRequest(),
                parser = CreateProductResponse.parser()
            )
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
            natsMessagePublisher.request(
                NatsSubject.Product.SAVE,
                payload = createProductDTO.toCreateProductRequest(),
                parser = CreateProductResponse.parser()
            )
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
            natsMessagePublisher.request(
                NatsSubject.Product.FIND_BY_ID,
                findProductByIdRequest,
                FindProductByIdResponse.parser()
            )
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
            natsMessagePublisher.request(
                NatsSubject.Product.FIND_BY_ID,
                findProductByIdRequest,
                FindProductByIdResponse.parser()
            )
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
            natsMessagePublisher.request(
                NatsSubject.Product.FIND_BY_ID,
                findProductByIdRequest,
                FindProductByIdResponse.parser()
            )
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
            natsMessagePublisher.request(
                NatsSubject.Product.FIND_BY_ID,
                findProductByIdRequest,
                FindProductByIdResponse.parser()
            )
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
            natsMessagePublisher.request(
                NatsSubject.Product.UPDATE,
                updateProductRequest(randomUserId, updateProductDTO),
                UpdateProductResponse.parser()
            )
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
            natsMessagePublisher.request(
                NatsSubject.Product.DELETE,
                deleteProductRequest,
                DeleteProductResponse.parser()
            )
        } returns deleteProductResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        productController.delete(randomProductId)
            .test()
            .verifyError(IllegalStateException::class)
    }
}
