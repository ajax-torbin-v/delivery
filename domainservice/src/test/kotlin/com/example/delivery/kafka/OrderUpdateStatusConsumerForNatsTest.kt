package com.example.delivery.kafka

import com.example.commonmodels.order.Order
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
import com.example.internal.api.NatsSubject
import io.nats.client.Dispatcher
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import java.time.Duration

class OrderUpdateStatusConsumerForNatsTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var dispatcher: Dispatcher

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
        val receivedMessages = mutableListOf<Order>()
        val updateOrderStatusResponse = savedOrder.toUpdateOrderStatusResponse()
        subscribe(savedUser.id)
            .doOnNext { receivedMessages.add(it) }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()

        // WHEN
        updateProducer.sendOrderUpdateStatus(updateOrderStatusResponse)
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()

        // THEN
        await()
            .atMost(Duration.ofSeconds(5))
            .untilAsserted {
                assertTrue(receivedMessages.isNotEmpty())
            }
    }

    private fun subscribe(userId: String): Flux<Order> {
        val sink = Sinks.many().unicast().onBackpressureBuffer<Order>()
        val subscription =
            dispatcher.subscribe(NatsSubject.Order.getUpdateStatusByUserId(userId)) { message ->
                sink.tryEmitNext(Order.parseFrom(message.data))
            }
        return sink.asFlux()
            .log()
            .doFinally {
                dispatcher.unsubscribe(subscription)
            }
    }
}
