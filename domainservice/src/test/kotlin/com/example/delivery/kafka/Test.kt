package com.example.delivery.kafka

import com.example.core.OrderFixture.createOrderDTO
import com.example.core.OrderFixture.randomAmount
import com.example.core.ProductFixture.createProductDTO
import com.example.core.UserFixture.createUserDTO
import com.example.core.dto.request.CreateOrderItemDTO
import com.example.delivery.domain.DomainOrder
import com.example.delivery.kafka.Test.MyKafkaTestConfiguration
import com.example.delivery.kafka.configuration.KafkaConfiguration
import com.example.delivery.service.OrderService
import com.example.delivery.service.ProductService
import com.example.delivery.service.UserService
import com.example.internal.api.KafkaTopic
import com.example.internal.commonmodels.order.OrderStatusUpdateNotification
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import java.util.concurrent.TimeUnit

@ActiveProfiles("test")
@Import(MyKafkaTestConfiguration::class)
@SpringBootTest
class Test {
    @Autowired
    private lateinit var orderService: OrderService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var kafkaTestReceiver: KafkaReceiver<String, ByteArray>

    @Test
    fun `should produce message on entity update`() {
        // GIVEN
        val savedUser = userService.add(createUserDTO).block()!!
        val savedProduct = productService.add(createProductDTO).block()!!
        val items = listOf(CreateOrderItemDTO(savedProduct.id, randomAmount))
        val savedOrder = orderService.add(createOrderDTO.copy(userId = savedUser.id, items = items)).block()!!
        val receivedMessages = mutableListOf<OrderStatusUpdateNotification>()
        val expected = OrderStatusUpdateNotification.newBuilder().apply {
            orderId = savedOrder.id
            userId = savedUser.id
            status = OrderStatusUpdateNotification.Status.STATUS_SHIPPING
        }.build()

        kafkaTestReceiver.receive()
            .doOnNext { record ->
                receivedMessages.add(
                    OrderStatusUpdateNotification.parseFrom(record.value()).toBuilder()
                        .clearTimestamp()
                        .build()
                )
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()

        // WHEN
        orderService.updateOrderStatus(savedOrder.id, DomainOrder.Status.SHIPPING.toString()).block()!!

        // THEN
        await()
            .atMost(15, TimeUnit.SECONDS)
            .untilAsserted {
                assertTrue(receivedMessages.contains(expected))
            }
    }

    class MyKafkaTestConfiguration(
        @Value("\${spring.kafka.bootstrap-servers}") val bootstrapServer: String,
        kafkaProperties: KafkaProperties,
    ) : KafkaConfiguration(
        bootstrapServer,
        kafkaProperties
    ) {
        @Bean
        fun kafkaTestReceiver(): KafkaReceiver<String, ByteArray> {
            return createKafkaReceiver(
                baseConsumerProperties(),
                KafkaTopic.KafkaOrderStatusUpdateEvents.NOTIFICATIONS,
                NOTIFICATION_GROUP
            )
        }
    }

    companion object {
        const val NOTIFICATION_GROUP = "notificationsGroup"
    }
}
