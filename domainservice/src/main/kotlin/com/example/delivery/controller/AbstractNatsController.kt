package com.example.delivery.controller

import com.example.delivery.annotaion.NatsController
import com.example.delivery.annotaion.NatsHandler
import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Parser
import io.nats.client.Connection
import io.nats.client.Dispatcher
import jakarta.annotation.PostConstruct
import reactor.core.publisher.Mono

abstract class AbstractNatsController(
    private val connection: Connection,
    private val dispatcher: Dispatcher,
) {
    @PostConstruct
    fun init() {
        initializeDispatchers()
    }

    private fun initializeDispatchers() {
        val classAnnotation = this::class.java.getAnnotation(NatsController::class.java)
        val queueGroup = classAnnotation.queueGroup
        val methods = this::class.java.methods.filter { it.isAnnotationPresent(NatsHandler::class.java) }

        for (method in methods) {
            val methodAnnotation = method.getAnnotation(NatsHandler::class.java)
            val requestType = method.parameters.map { it.type }.first()
            val parser = requestType.getMethod("parser").invoke(null) as Parser<*>
            val subject = if (classAnnotation.subjectPrefix.isEmpty()) {
                methodAnnotation.subject
            } else {
                "${classAnnotation.subjectPrefix}.${methodAnnotation.subject}"
            }

            dispatcher.subscribe(subject, queueGroup) { message ->
                val request = parser.parseFrom(message.data)
                val response = method.invoke(this, request) as Mono<GeneratedMessage>
                response.subscribe { connection.publish(message.replyTo, it.toByteArray()) }
            }
        }
    }
}
