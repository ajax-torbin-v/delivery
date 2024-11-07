package com.example.gateway.mapper

import com.example.core.OrderFixture.createOrderDTO
import com.example.gateway.OrderProtoFixture
import com.example.gateway.OrderProtoFixture.grpcCreateOrderRequest
import com.example.gateway.mapper.OrderProtoMapper.toCreateOrderRequest
import com.example.gateway.mapper.OrderProtoMapper.toGrpc
import com.example.gateway.mapper.OrderProtoMapper.toInternal
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderProtoMapperTest {

    @Test
    fun `should return mapped internal proto CreateOrderRequest`() {
        // GIVEN
        val expected = createOrderDTO.toCreateOrderRequest()

        // WHEN // THEN
        assertEquals(expected, grpcCreateOrderRequest.toInternal())
    }

    @Test
    fun `should return mapped grpc CreateOrderRequest`() {
        // GIVEN
        val expected = OrderProtoFixture.grpcCreateOrderResponse

        // WHEN // THEN
        assertEquals(expected, OrderProtoFixture.createOrderResponse.toGrpc())
    }

    @Test
    fun `should throw exception for default instance of CreateOrderResponse `() {
        // GIVEN
        val defaultInstance: CreateOrderResponse = CreateOrderResponse.getDefaultInstance()

        // WHEN // THEN
        assertThrows<RuntimeException> { defaultInstance.toGrpc() }
    }

    @Test
    fun `should return mapped grpc FindOrderByIdResponse`() {
        // GIVEN
        val expected = OrderProtoFixture.grpcFindOrderByIdResponse

        // WHEN // THEN
        assertEquals(expected, OrderProtoFixture.findOrderByIdResponse.toGrpc())
    }

    @Test
    fun ` should throw exception for default instance of FindOrderByIdResponse`() {
        // GIVEN
        val defaultInstance: FindOrderByIdResponse = FindOrderByIdResponse.getDefaultInstance()

        // WHEN // THEN
        assertThrows<RuntimeException> { defaultInstance.toGrpc() }
    }
}
