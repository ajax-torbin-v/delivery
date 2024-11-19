package com.example.domainservice.order.infrastructure.kafka

import com.example.commonmodels.order.OrderStatusUpdateNotification
import com.example.core.OrderFixture.randomAmount
import com.example.domainservice.OrderFixture.domainOrder
import com.example.domainservice.ProductFixture.unsavedDomainProduct
import com.example.domainservice.UserFixture.unsavedDomainUser
import com.example.domainservice.order.AbstractIntegrationTest
import com.example.domainservice.order.application.service.OrderService
import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.product.application.service.ProductService
import com.example.domainservice.user.application.service.UserService
import com.example.internal.api.KafkaTopic
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.parallel.ResourceLock
import org.springframework.beans.factory.annotation.Autowired
import systems.ajax.kafka.mock.KafkaMockExtension

@ResourceLock(KafkaTopic.KafkaOrderStatusUpdateEvents.NOTIFICATIONS)
class OrderUpdateStatusProcessorTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var productService: ProductService

    @Test
    fun `should produce message on entity update`() {
        // GIVEN
        val savedUser = userService.save(unsavedDomainUser).block()!!
        val savedProduct = productService.save(unsavedDomainProduct).block()!!
        val items = listOf(DomainOrder.DomainOrderItem(savedProduct.id!!, savedProduct.price, randomAmount))
        val savedOrder = orderService.save(domainOrder.copy(userId = savedUser.id!!, items = items)).block()!!
        val expected = OrderStatusUpdateNotification.newBuilder().apply {
            orderId = savedOrder.id
            userId = savedUser.id
            status = OrderStatusUpdateNotification.Status.STATUS_SHIPPING
        }.build()

        // WHEN
        orderService.updateOrderStatus(savedOrder.id!!, DomainOrder.Status.SHIPPING).block()!!

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
