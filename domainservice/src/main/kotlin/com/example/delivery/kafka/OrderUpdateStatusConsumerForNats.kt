package com.example.delivery.kafka

import com.example.internal.api.KafkaTopic
import com.example.internal.api.NatsSubject
import com.example.internal.input.reqreply.order.UpdateOrderStatusResponse
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import systems.ajax.kafka.handler.KafkaEvent
import systems.ajax.kafka.handler.KafkaHandler
import systems.ajax.kafka.handler.subscription.topic.TopicSingle
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@Component
class OrderUpdateStatusConsumerForNats(
    private val publisher: NatsMessagePublisher,
) : KafkaHandler<UpdateOrderStatusResponse, TopicSingle> {
    override val groupId: String = UPDATE_STATUS_CONSUMER_GROUP_NATS
    override val parser: Parser<UpdateOrderStatusResponse> = UpdateOrderStatusResponse.parser()
    override val subscriptionTopics: TopicSingle = TopicSingle(KafkaTopic.KafkaOrderStatusUpdateEvents.UPDATE)

    override fun handle(kafkaEvent: KafkaEvent<UpdateOrderStatusResponse>): Mono<Unit> {
        return publisher.publish(
            NatsSubject.Order.getUpdateStatusByUserId(kafkaEvent.data.success.order.userId),
            kafkaEvent.data
        )
    }

    companion object {
        private const val UPDATE_STATUS_CONSUMER_GROUP_NATS = "natsUpdateOrderStatusConsumerGroup"
    }
}
