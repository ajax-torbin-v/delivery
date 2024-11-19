package com.example.domainservice.order.infrastructure.kafka

import com.example.domainservice.order.infrastructure.kafka.mapper.OrderMapper.toNotification
import com.example.internal.api.KafkaTopic
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.KafkaHandler
import systems.ajax.kafka.handler.subscription.topic.TopicSingle

@Component
class OrderUpdateStatusProcessor(
    private val updateStatusNotificationSender: OrderUpdateStatusNotificationProducer,
) : KafkaHandler<UpdateOrderStatusResponse, TopicSingle> {
    override val groupId: String = UPDATE_STATUS_CONSUMER_GROUP
    override val parser: Parser<UpdateOrderStatusResponse> = UpdateOrderStatusResponse.parser()
    override val subscriptionTopics: TopicSingle = TopicSingle(KafkaTopic.KafkaOrderStatusUpdateEvents.UPDATE)

    override fun handle(kafkaEvent: KafkaEvent<UpdateOrderStatusResponse>): Mono<Unit> {
        return updateStatusNotificationSender.notify(kafkaEvent.data.success.order.toNotification())
            .doOnSuccess {
                kafkaEvent.ack()
            }
    }

    companion object {
        private const val UPDATE_STATUS_CONSUMER_GROUP = "updateOrderStatusConsumerGroup"
    }
}
