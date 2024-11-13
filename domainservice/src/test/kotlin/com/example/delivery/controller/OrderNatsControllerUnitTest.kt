package com.example.delivery.controller

import com.example.delivery.controller.order.CreateOrderNatsHandler
import com.example.delivery.controller.order.DeleteOrderNatsHandler
import com.example.delivery.controller.order.FindAllByUserIdOrderNatsHandler
import com.example.delivery.controller.order.FindByIdOrderNatsHandler
import com.example.delivery.controller.order.UpdateOrderNatsHandler
import com.example.delivery.controller.order.UpdateOrderStatusNatsHandler
import com.example.delivery.service.OrderService
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.DeleteOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.FindOrdersByUserIdResponse
import com.example.internal.input.reqreply.order.UpdateOrderResponse
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
class OrderNatsControllerUnitTest {
    @SuppressWarnings("UnusedPrivateProperty")
    @MockK
    private lateinit var orderService: OrderService

    @InjectMockKs
    private lateinit var createOrderNatsHandler: CreateOrderNatsHandler

    @InjectMockKs
    private lateinit var findByIdOrderNatsHandler: FindByIdOrderNatsHandler

    @InjectMockKs
    private lateinit var findAllByUserIdOrderNatsHandler: FindAllByUserIdOrderNatsHandler

    @InjectMockKs
    private lateinit var updateOrderNatsHandler: UpdateOrderNatsHandler

    @InjectMockKs
    private lateinit var updateOrderStatusNatsHandler: UpdateOrderStatusNatsHandler

    @InjectMockKs
    private lateinit var deleteOrderNatsHandler: DeleteOrderNatsHandler

    @Test
    fun `create doOnUnexpectedError should return empty error message`() {
        // GIVEN
        val exception = KotlinNullPointerException()
        val message = CreateOrderResponse.newBuilder().apply { failureBuilder }.build()

        // WHEN
        val actual = createOrderNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `create doOnUnexpectedError should return error message`() {
        // GIVEN
        val exception = KotlinNullPointerException("Oops")
        val message = CreateOrderResponse.newBuilder().apply { failureBuilder.message = "Oops" }.build()

        // WHEN
        val actual = createOrderNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `findById doOnUnexpectedError should return empty error message`() {
        // GIVEN
        val exception = KotlinNullPointerException()
        val message = FindOrderByIdResponse.newBuilder().apply { failureBuilder }.build()

        // WHEN
        val actual = findByIdOrderNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `findById doOnUnexpectedError should return error message`() {
        // GIVEN
        val exception = KotlinNullPointerException("Oops")
        val message = FindOrderByIdResponse.newBuilder().apply { failureBuilder.message = "Oops" }.build()

        // WHEN
        val actual = findByIdOrderNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `findAllByUserId doOnUnexpectedError should return empty error message`() {
        // GIVEN
        val exception = KotlinNullPointerException()
        val message = FindOrdersByUserIdResponse.newBuilder().apply { failureBuilder }.build()

        // WHEN
        val actual = findAllByUserIdOrderNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `findAllByUserId doOnUnexpectedError should return error message`() {
        // GIVEN
        val exception = KotlinNullPointerException("Oops")
        val message = FindOrdersByUserIdResponse.newBuilder().apply { failureBuilder.message = "Oops" }.build()

        // WHEN
        val actual = findAllByUserIdOrderNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `update doOnUnexpectedError should return empty error message`() {
        // GIVEN
        val exception = KotlinNullPointerException()
        val message = UpdateOrderResponse.newBuilder().apply { failureBuilder }.build()

        // WHEN
        val actual = updateOrderNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `update doOnUnexpectedError should return error message`() {
        // GIVEN
        val exception = KotlinNullPointerException("Oops")
        val message = UpdateOrderResponse.newBuilder().apply { failureBuilder.message = "Oops" }.build()

        // WHEN
        val actual = updateOrderNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `updateStatus doOnUnexpectedError should return empty error message`() {
        // GIVEN
        val exception = KotlinNullPointerException()
        val message = UpdateOrderStatusResponse.newBuilder().apply { failureBuilder }.build()

        // WHEN
        val actual = updateOrderStatusNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `updateStatus doOnUnexpectedError should return error message`() {
        // GIVEN
        val exception = KotlinNullPointerException("Oops")
        val message = UpdateOrderStatusResponse.newBuilder().apply { failureBuilder.message = "Oops" }.build()

        // WHEN
        val actual = updateOrderStatusNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `delete doOnUnexpectedError should return empty error message`() {
        // GIVEN
        val exception = KotlinNullPointerException()
        val message = DeleteOrderResponse.newBuilder().apply { failureBuilder }.build()

        // WHEN
        val actual = deleteOrderNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }

    @Test
    fun `delete doOnUnexpectedError should return error message`() {
        // GIVEN
        val exception = KotlinNullPointerException("Oops")
        val message = DeleteOrderResponse.newBuilder().apply { failureBuilder.message = "Oops" }.build()

        // WHEN
        val actual = deleteOrderNatsHandler.doOnUnexpectedError(null, exception)

        // THEN
        actual.test()
            .expectNext(message)
            .verifyComplete()
    }
}
