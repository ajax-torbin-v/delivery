package com.example.delivery.kafka

import com.example.core.OrderFixture.createOrderDTO
import com.example.core.OrderFixture.randomAmount
import com.example.core.ProductFixture.createProductDTO
import com.example.core.UserFixture.createUserDTO
import com.example.core.dto.request.CreateOrderItemDTO
import com.example.delivery.AbstractIntegrationTest
import com.example.delivery.mapper.OrderProtoMapper.toUpdateOrderStatusResponse
import com.example.delivery.service.OrderService
import com.example.delivery.service.ProductService
import com.example.delivery.service.UserService
import com.example.internal.api.KafkaTopic
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.parallel.ResourceLock
import org.springframework.beans.factory.annotation.Autowired
import systems.ajax.nats.mock.junit5.NatsMockExtension
import java.time.Duration

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
        // GIVEN
        val savedUser = userService.add(createUserDTO).block()!!
        val savedProduct = productService.add(createProductDTO).block()!!
        val items = listOf(CreateOrderItemDTO(savedProduct.id, randomAmount))
        val savedOrder = orderService.add(createOrderDTO.copy(userId = savedUser.id, items = items)).block()!!
        val updateOrderStatusResponse = savedOrder.toUpdateOrderStatusResponse()

        val captor = natsMockExt.subscribe(
            NatsSubject.Order.getUpdateStatusByUserId(savedUser.id),
            parser = UpdateOrderStatusResponse.parser()
        ).capture()

        // WHEN
        updateProducer.sendOrderUpdateStatus(updateOrderStatusResponse).subscribe()

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
