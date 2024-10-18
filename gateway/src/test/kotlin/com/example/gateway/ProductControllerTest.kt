package com.example.gateway

import com.example.core.ProductFixture.createProductDTO
import com.example.core.ProductFixture.randomProductId
import com.example.core.exception.ProductNotFoundException
import com.example.gateway.ProductProtoFixture.createProductResponse
import com.example.gateway.ProductProtoFixture.createProductResponseWithUnexpectedException
import com.example.gateway.ProductProtoFixture.deleteProductRequest
import com.example.gateway.ProductProtoFixture.deleteProductResponseWithUnexpectedException
import com.example.gateway.ProductProtoFixture.findProductByIdRequest
import com.example.gateway.ProductProtoFixture.findProductByIdResponse
import com.example.gateway.ProductProtoFixture.findProductByIdResponseWithProductNotFoundException
import com.example.gateway.ProductProtoFixture.findProductByIdResponseWithUnexpectedException
import com.example.gateway.client.NatsClient
import com.example.gateway.mapper.ProductProtoMapper.toCreateProductRequest
import com.example.gateway.mapper.ProductProtoMapper.toDTO
import com.example.gateway.rest.ProductController
import com.example.internal.api.subject.ProductsNatsSubject
import com.example.internal.commonmodels.input.reqreply.product.delete.DeleteProductResponse
import com.example.internal.input.reqreply.product.create.CreateProductResponse
import com.example.internal.input.reqreply.product.find.FindProductByIdResponse
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.nats.client.Connection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toMono

@ExtendWith(MockKExtension::class)
class ProductControllerTest {
    @SuppressWarnings("UnusedPrivateProperty")
    @MockK
    private lateinit var connection: Connection

    @MockK
    private lateinit var natsClient: NatsClient

    @InjectMockKs
    private lateinit var productController: ProductController

    @Test
    fun `save should return product DTO`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${ProductsNatsSubject.PRODUCT_PREFIX}.${ProductsNatsSubject.SAVE}",
                payload = createProductDTO.toCreateProductRequest(),
                parser = CreateProductResponse.parser()
            )
        } returns createProductResponse.toMono()

        // WHEN
        val actual = productController.add(createProductDTO).block()

        // THEN
        assertEquals(createProductResponse.toDTO(), actual)
    }

    @Test
    fun `save should rethrow exception when message contains unexpected error`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${ProductsNatsSubject.PRODUCT_PREFIX}.${ProductsNatsSubject.SAVE}",
                payload = createProductDTO.toCreateProductRequest(),
                parser = CreateProductResponse.parser()
            )
        } returns createProductResponseWithUnexpectedException.toMono()

        // WHEN //THEN
        assertThrows<IllegalStateException> { productController.add(createProductDTO).block() }
    }

    @Test
    fun `save should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${ProductsNatsSubject.PRODUCT_PREFIX}.${ProductsNatsSubject.SAVE}",
                payload = createProductDTO.toCreateProductRequest(),
                parser = CreateProductResponse.parser()
            )
        } returns CreateProductResponse.getDefaultInstance().toMono()

        // WHEN //THEN
        assertThrows<RuntimeException> { productController.add(createProductDTO).block() }
    }

    @Test
    fun `findById should return existing product`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${ProductsNatsSubject.PRODUCT_PREFIX}.${ProductsNatsSubject.FIND_BY_ID}",
                findProductByIdRequest,
                FindProductByIdResponse.parser()
            )
        } returns findProductByIdResponse.toMono()

        // WHEN
        val actual = productController.findById(randomProductId).block()

        // THEN
        assertEquals(findProductByIdResponse.toDTO(), actual)
    }

    @Test
    fun `findById should throw exception when user doesn't exist`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${ProductsNatsSubject.PRODUCT_PREFIX}.${ProductsNatsSubject.FIND_BY_ID}",
                findProductByIdRequest,
                FindProductByIdResponse.parser()
            )
        } returns findProductByIdResponseWithProductNotFoundException.toMono()

        // WHEN // THEN
        assertThrows<ProductNotFoundException> { productController.findById(randomProductId).block() }
    }

    @Test
    fun `findById should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${ProductsNatsSubject.PRODUCT_PREFIX}.${ProductsNatsSubject.FIND_BY_ID}",
                findProductByIdRequest,
                FindProductByIdResponse.parser()
            )
        } returns findProductByIdResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        assertThrows<IllegalStateException> { productController.findById(randomProductId).block() }
    }

    @Test
    fun `findById should throw exception when message is empty`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${ProductsNatsSubject.PRODUCT_PREFIX}.${ProductsNatsSubject.FIND_BY_ID}",
                findProductByIdRequest,
                FindProductByIdResponse.parser()
            )
        } returns FindProductByIdResponse.getDefaultInstance().toMono()

        // WHEN // THEN
        assertThrows<RuntimeException> { productController.findById(randomProductId).block() }
    }

    @Test
    fun `delete should rethrow unexpected exception`() {
        // GIVEN
        every {
            natsClient.doRequest(
                "${ProductsNatsSubject.PRODUCT_PREFIX}.${ProductsNatsSubject.DELETE}",
                deleteProductRequest,
                DeleteProductResponse.parser()
            )
        } returns deleteProductResponseWithUnexpectedException.toMono()

        // WHEN // THEN
        assertThrows<IllegalStateException> { productController.delete(randomProductId).block() }
    }
}
