package com.example.delivery.mapper

import com.example.core.exception.OrderNotFoundException
import com.example.core.exception.ProductAmountException
import com.example.core.exception.ProductNotFoundException
import com.example.core.exception.UserNotFoundException
import com.example.delivery.OrderFixture
import com.example.delivery.mapper.OrderProtoMapper.toFailureCreateOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureFindOrderByIdResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureUpdateOrderResponse
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.UpdateOrderResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class OrderProtoMapperTest {
    @ParameterizedTest
    @MethodSource("failureCreateOrderResponseProvider")
    fun `should build failure message for CreateOrderResponse`(
        error: Throwable,
        failureResponse: CreateOrderResponse,
    ) {
        // WHEN // THEN
        assertEquals(failureResponse, error.toFailureCreateOrderResponse())
    }

    @ParameterizedTest
    @MethodSource("failureFindOrderByIdResponseProvider")
    fun `should build failure message for FindOrderByIdResponse`(
        error: Throwable,
        failureResponse: FindOrderByIdResponse,
    ) {
        // WHEN // THEN
        assertEquals(failureResponse, error.toFailureFindOrderByIdResponse())
    }

    @ParameterizedTest
    @MethodSource("failureUpdateOrderResponseProvider")
    fun `should build failure message for UpdateOrderResponse`(
        error: Throwable,
        failureResponse: UpdateOrderResponse,
    ) {
        // WHEN // THEN
        assertEquals(failureResponse, error.toFailureUpdateOrderResponse())
    }

    companion object {
        @JvmStatic
        fun failureCreateOrderResponseProvider(): List<Arguments> {
            return listOf(
                Arguments.of(
                    UserNotFoundException("User not found"),
                    OrderFixture.failureCreateOrderResponseWithUserNotFoundException
                ),
                Arguments.of(
                    ProductNotFoundException("Product not found"),
                    OrderFixture.failureCreateOrderResponseWithProductNotFoundException
                ),
                Arguments.of(
                    ProductAmountException("Not enough product"),
                    OrderFixture.failureCreateOrderResponseWithProductAmountException
                ),
                Arguments.of(
                    NullPointerException(),
                    OrderFixture.failureCreateOrderResponseWithUnexpectedException
                )
            )
        }

        @JvmStatic
        fun failureFindOrderByIdResponseProvider(): List<Arguments> {
            return listOf(
                Arguments.of(
                    OrderNotFoundException("Order not found"),
                    OrderFixture.failureFindOrderByIdWithOrderNotFoundException
                ),
                Arguments.of(
                    NullPointerException(),
                    OrderFixture.failureFindOrderByIdWithUnexpectedException
                )
            )
        }

        @JvmStatic
        fun failureUpdateOrderResponseProvider(): List<Arguments> {
            return listOf(
                Arguments.of(
                    OrderNotFoundException("Order not found"),
                    OrderFixture.failureUpdateOrderResponseWithOrderNotFoundException
                ),
                Arguments.of(
                    NullPointerException(),
                    OrderFixture.failureUpdateOrderResponseWitUnexpectedException
                )
            )
        }
    }
}
