package com.example.delivery.kafka

import com.example.commonmodels.order.OrderStatusUpdateNotification
import com.example.core.OrderFixture.createOrderDTO
import com.example.core.OrderFixture.randomAmount
import com.example.core.ProductFixture.createProductDTO
import com.example.core.UserFixture.createUserDTO
import com.example.core.dto.request.CreateOrderItemDTO
import com.example.delivery.AbstractIntegrationTest
import com.example.delivery.domain.DomainOrder
import com.example.delivery.service.OrderService
import com.example.delivery.service.ProductService
import com.example.delivery.service.UserService
import com.example.internal.api.KafkaTopic
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.parallel.ResourceLock
import org.springframework.beans.factory.annotation.Autowired
import systems.ajax.kafka.mock.KafkaMockExtension

@ResourceLock(KafkaTopic.KafkaOrderStatusUpdateEvents.NOTIFICATIONS)
class KafkaTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var productService: ProductService

    @Test
    fun `should produce message on entity update`() {
        // GIVEN
        val savedUser = userService.add(createUserDTO).block()!!
        val savedProduct = productService.add(createProductDTO).block()!!
        val items = listOf(CreateOrderItemDTO(savedProduct.id, randomAmount))
        val savedOrder = orderService.add(createOrderDTO.copy(userId = savedUser.id, items = items)).block()!!
        val expected = OrderStatusUpdateNotification.newBuilder().apply {
            orderId = savedOrder.id
            userId = savedUser.id
            status = OrderStatusUpdateNotification.Status.STATUS_SHIPPING
        }.build()

        // WHEN
        orderService.updateOrderStatus(savedOrder.id, DomainOrder.Status.SHIPPING.toString()).block()!!

        // THEN
        val eventProvider = kafkaMockExtension.listen<OrderStatusUpdateNotification>(
            KafkaTopic.KafkaOrderStatusUpdateEvents.NOTIFICATIONS,
            OrderStatusUpdateNotification.parser()
        )

        val event = eventProvider.awaitFirst({ it.toBuilder().clearTimestamp().build() == expected })
        assertThat(event).isNotNull
    }

    companion object {
        @JvmField
        @RegisterExtension
        val kafkaMockExtension: KafkaMockExtension = KafkaMockExtension()
    }
}
