package com.example.gateway.client

import com.example.commonmodels.order.Order
import com.example.internal.api.NatsSubject
import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import io.nats.client.Dispatcher
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks

@Component
class NatsClient(
    private val natsConnection: Connection,
    private val dispatcher: Dispatcher,
) {
    fun <RequestT : GeneratedMessage, ResponseT : GeneratedMessage> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): Mono<ResponseT> {
        return Mono.fromFuture {
            natsConnection.request(
                subject,
                payload.toByteArray()
            )
        }.map {
            parser.parseFrom(it.data)
        }
    }

    fun subscribeByUserId(userId: String): Flux<Order> {
        val sink = Sinks.many().unicast().onBackpressureBuffer<Order>()
        val subject = NatsSubject.Order.getUpdateStatusByUserId(userId)
        val subscription = dispatcher.subscribe(subject) { message ->
            sink.tryEmitNext(Order.parseFrom(message.data))
        }
        return sink.asFlux()
            .log()
            .doFinally {
                dispatcher.unsubscribe(subscription)
            }
    }
}
