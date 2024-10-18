package com.example.delivery.mapper

import com.example.core.exception.OrderNotFoundException
import com.example.core.exception.ProductNotFoundException
import com.example.core.exception.UserNotFoundException
import com.example.delivery.mapper.OrderProtoMapper.toFailureCreateOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureFindOrderByIdResponse
import com.example.internal.input.reqreply.order.create.CreateOrderResponse
import com.example.internal.input.reqreply.order.find.FindOrderByIdResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OrderProtoMapperTest {
    @Test
    fun `should build failure message for CreateOrderResponse`() {
        // GIVEN
        val productNotFoundException = ProductNotFoundException("Product not found")
        val userNotFoundException = UserNotFoundException("User not found")
        val unexpectedException = NullPointerException()
        val failureWithProductNotFoundException = CreateOrderResponse.newBuilder().apply {
            failureBuilder.message = "Product not found"
            failureBuilder.productNotFoundBuilder
        }.build()
        val failureWithUserNotFoundException = CreateOrderResponse.newBuilder().apply {
            failureBuilder.message = "User not found"
            failureBuilder.userNotFoundBuilder
        }.build()
        val failureWithUnexpectedException = CreateOrderResponse.newBuilder().apply {
            failureBuilder
        }.build()

        // WHEN // THEN
        assertEquals(
            failureWithProductNotFoundException,
            productNotFoundException.toFailureCreateOrderResponse()
        )
        assertEquals(
            failureWithUserNotFoundException,
            userNotFoundException.toFailureCreateOrderResponse()
        )
        assertEquals(
            failureWithUnexpectedException,
            unexpectedException.toFailureCreateOrderResponse()
        )
    }

    @Test
    fun `should build failure message for FindOrderByIdResponse`() {
        // GIVEN
        val orderNotFoundException = OrderNotFoundException("Order not found")
        val unexpectedException = NullPointerException("Oops")
        val failureWithOrderNotFoundException = FindOrderByIdResponse.newBuilder().apply {
            failureBuilder.message = "Order not found"
            failureBuilder.orderNotFoundBuilder
        }.build()
        val failureWithUnexpectedException = FindOrderByIdResponse.newBuilder().apply {
            failureBuilder.message = "Oops"
        }.build()

        // WHEN // THEN
        assertEquals(
            failureWithOrderNotFoundException,
            orderNotFoundException.toFailureFindOrderByIdResponse()
        )
        assertEquals(
            failureWithUnexpectedException,
            unexpectedException.toFailureFindOrderByIdResponse()
        )
    }
}
