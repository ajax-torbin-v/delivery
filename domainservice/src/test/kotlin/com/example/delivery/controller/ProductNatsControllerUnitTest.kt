package com.example.delivery.controller

import com.example.delivery.controller.product.CreateProductNatsHandler
import com.example.delivery.controller.product.DeleteProductNatsHandler
import com.example.delivery.controller.product.FindByIdProductNatsHandler
import com.example.delivery.controller.product.UpdateProductNatsHandler
import com.example.delivery.service.ProductService
import com.example.internal.input.reqreply.product.CreateProductResponse
import com.example.internal.input.reqreply.product.DeleteProductResponse
import com.example.internal.input.reqreply.product.FindProductByIdResponse
import com.example.internal.input.reqreply.product.UpdateProductResponse
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
class ProductNatsControllerUnitTest {
    @SuppressWarnings("UnusedPrivateProperty")
    @MockK
    private lateinit var productService: ProductService

    @InjectMockKs
    private lateinit var createProductNatsHandler: CreateProductNatsHandler

    @InjectMockKs
    private lateinit var findByIdProductNatsHandler: FindByIdProductNatsHandler

    @InjectMockKs
    private lateinit var updateProductNatsHandler: UpdateProductNatsHandler

    @InjectMockKs
    private lateinit var deleteProductNatsHandler: DeleteProductNatsHandler

    @Test
    fun `create doOnUnexpectedError should return empty error message`() {
        // GIVEN
        val exception = KotlinNullPointerException()
        val message = CreateProductResponse.newBuilder().apply { failureBuilder }.build()

        // WHEN
        val actual = createProductNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `create doOnUnexpectedError should return error message`() {
        // GIVEN
        val exception = KotlinNullPointerException("Oops")
        val message = CreateProductResponse.newBuilder().apply { failureBuilder.message = "Oops" }.build()

        // WHEN
        val actual = createProductNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `findById doOnUnexpectedError should return empty error message`() {
        // GIVEN
        val exception = KotlinNullPointerException()
        val message = FindProductByIdResponse.newBuilder().apply { failureBuilder }.build()

        // WHEN
        val actual = findByIdProductNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `findById doOnUnexpectedError should return error message`() {
        // GIVEN
        val exception = KotlinNullPointerException("Oops")
        val message = FindProductByIdResponse.newBuilder().apply { failureBuilder.message = "Oops" }.build()

        // WHEN
        val actual = findByIdProductNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `update doOnUnexpectedError should return empty error message`() {
        // GIVEN
        val exception = KotlinNullPointerException()
        val message = UpdateProductResponse.newBuilder().apply { failureBuilder }.build()

        // WHEN
        val actual = updateProductNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `update doOnUnexpectedError should return error message`() {
        // GIVEN
        val exception = KotlinNullPointerException("Oops")
        val message = UpdateProductResponse.newBuilder().apply { failureBuilder.message = "Oops" }.build()

        // WHEN
        val actual = updateProductNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `delete doOnUnexpectedError should return empty error message`() {
        // GIVEN
        val exception = KotlinNullPointerException()
        val message = DeleteProductResponse.newBuilder().apply { failureBuilder }.build()

        // WHEN
        val actual = deleteProductNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `delete doOnUnexpectedError should return error message`() {
        // GIVEN
        val exception = KotlinNullPointerException("Oops")
        val message = DeleteProductResponse.newBuilder().apply { failureBuilder.message = "Oops" }.build()

        // WHEN
        val actual = deleteProductNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }
}
