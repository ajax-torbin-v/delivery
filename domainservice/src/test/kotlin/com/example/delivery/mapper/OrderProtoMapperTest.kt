package com.example.delivery.mapper

import com.example.delivery.OrderFixture
import com.example.delivery.mapper.OrderProtoMapper.toFailureCreateOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureFindOrderByIdResponse
import com.example.internal.input.reqreply.order.create.CreateOrderResponse
import com.example.internal.input.reqreply.order.find.FindOrderByIdResponse
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

    companion object {
        @JvmStatic
        fun failureCreateOrderResponseProvider(): List<Arguments> {
            return listOf(
                Arguments.of(
                    OrderFixture.userNotFoundException,
                    OrderFixture.failureCreateOrderResponseWithUserNotFoundException
                ),
                Arguments.of(
                    OrderFixture.productNotFoundException,
                    OrderFixture.failureCreateOrderResponseWithProductNotFoundException
                ),
                Arguments.of(
                    OrderFixture.unexpectedException,
                    OrderFixture.failureCreateOrderResponseWithUnexpectedException
                )
            )
        }

        @JvmStatic
        fun failureFindOrderByIdResponseProvider(): List<Arguments> {
            return listOf(
                Arguments.of(
                    OrderFixture.orderNotFoundException,
                    OrderFixture.failureFindOrderByIdWithOrderNotFoundException
                ),
                Arguments.of(
                    OrderFixture.unexpectedException,
                    OrderFixture.failureFindOrderByIdWithUnexpectedException
                )
            )
        }
    }
}
