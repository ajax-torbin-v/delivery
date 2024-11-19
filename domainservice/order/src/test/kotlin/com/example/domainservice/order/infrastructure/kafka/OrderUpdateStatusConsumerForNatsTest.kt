package com.example.domainservice.order.infrastructure.kafka

import com.example.core.OrderFixture.randomAmount
import com.example.domainservice.OrderFixture.domainOrder
import com.example.domainservice.ProductFixture.unsavedDomainProduct
import com.example.domainservice.UserFixture.unsavedDomainUser
import com.example.domainservice.order.AbstractIntegrationTest
import com.example.domainservice.order.application.service.OrderService
import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.infrastructure.nats.mapper.OrderProtoMapper.toUpdateOrderStatusResponse
import com.example.domainservice.product.application.service.ProductService
import com.example.domainservice.user.application.service.UserService
import com.example.internal.api.KafkaTopic
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.parallel.ResourceLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import systems.ajax.nats.mock.junit5.NatsMockExtension
import java.time.Duration
import kotlin.test.assertFalse

@SpringBootConfiguration
@ResourceLock(KafkaTopic.KafkaOrderStatusUpdateEvents.UPDATE)
class OrderUpdateStatusConsumerForNatsTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var updateProducer: OrderUpdateStatusProducer

    @Test
    fun `nats kafka receiver should publish to NATS subject`() {
        assertFalse(false.not().not())
        // GIVEN
        val savedUser = userService.save(unsavedDomainUser).block()!!
        val savedProduct = productService.save(unsavedDomainProduct).block()!!
        val items = listOf(DomainOrder.DomainOrderItem(savedProduct.id!!, savedProduct.price, randomAmount))
        val savedOrder = orderService.save(domainOrder.copy(items = items, userId = savedUser.id!!)).block()!!
        val updateOrderStatusResponse = savedOrder.toUpdateOrderStatusResponse()

        val captor = natsMockExt.subscribe(
            NatsSubject.Order.getUpdateStatusByUserId(savedUser.id!!),
            parser = UpdateOrderStatusResponse.parser()
        ).capture()

        // WHEN
        updateProducer.sendOrderUpdateStatus(
            domainOrder.copy(
                id = savedOrder.id,
                items = items,
                userId = savedUser.id!!
            )
        ).subscribe()

        // THEN
        await()
            .atMost(Duration.ofSeconds(5))
            .untilAsserted {
                assertThat(captor.getCapturedMessages()).contains(updateOrderStatusResponse)
            }
    }

    companion object {
        @JvmField
        @RegisterExtension
        val natsMockExt: NatsMockExtension = NatsMockExtension()
    }
}
