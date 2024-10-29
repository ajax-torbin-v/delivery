package com.example.delivery.mapper

import com.example.core.exception.OrderNotFoundException
import com.example.core.exception.ProductAmountException
import com.example.core.exception.ProductNotFoundException
import com.example.core.exception.UserNotFoundException
import com.example.delivery.OrderFixture
import com.example.delivery.domain.DomainOrder
import com.example.delivery.mapper.OrderProtoMapper.toFailureCreateOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureFindOrderByIdResponse
import com.example.delivery.mapper.OrderProtoMapper.toFailureUpdateOrderResponse
import com.example.delivery.mapper.OrderProtoMapper.toNotificationStatus
import com.example.delivery.mapper.OrderProtoMapper.toProto
import com.example.internal.commonmodels.order.Order.Status
import com.example.internal.commonmodels.order.OrderStatusUpdateNotification
import com.example.internal.input.reqreply.order.CreateOrderResponse
import com.example.internal.input.reqreply.order.FindOrderByIdResponse
import com.example.internal.input.reqreply.order.UpdateOrderResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
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

    @ParameterizedTest
    @MethodSource("domainStatusToProtoProvider")
    fun `should return proper status`(
        domainStatus: DomainOrder.Status,
        protoStatus: Status,
    ) {
        // WHEN // THEN
        assertEquals(protoStatus, domainStatus.toProto())
    }

    @ParameterizedTest
    @MethodSource("orderToNotificationStatusProvider")
    fun `should map Status in Notification`(
        orderStatus: Status,
        notificationStatus: OrderStatusUpdateNotification.Status,
    ) {
        // WHEN // THEN
        assertEquals(notificationStatus, orderStatus.toNotificationStatus())
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

        @JvmStatic
        fun domainStatusToProtoProvider(): List<Arguments> {
            return listOf(
                Arguments.of(DomainOrder.Status.NEW, Status.STATUS_NEW),
                Arguments.of(DomainOrder.Status.UNKNOWN, Status.STATUS_UNKNOWN),
                Arguments.of(DomainOrder.Status.COMPLETED, Status.STATUS_COMPLETED),
                Arguments.of(DomainOrder.Status.SHIPPING, Status.STATUS_SHIPPING),
                Arguments.of(DomainOrder.Status.CANCELED, Status.STATUS_CANCELED),
            )
        }

        @JvmStatic
        fun orderToNotificationStatusProvider(): List<Arguments> {
            return listOf(
                Arguments.of(Status.STATUS_UNSPECIFIED, OrderStatusUpdateNotification.Status.STATUS_UNSPECIFIED),
                Arguments.of(Status.STATUS_NEW, OrderStatusUpdateNotification.Status.STATUS_NEW),
                Arguments.of(Status.STATUS_CANCELED, OrderStatusUpdateNotification.Status.STATUS_CANCELED),
                Arguments.of(Status.STATUS_COMPLETED, OrderStatusUpdateNotification.Status.STATUS_COMPLETED),
                Arguments.of(Status.STATUS_SHIPPING, OrderStatusUpdateNotification.Status.STATUS_SHIPPING)
            )
        }
    }
}
