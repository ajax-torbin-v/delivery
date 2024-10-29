package com.example.delivery.controller

import com.example.delivery.annotaion.NatsController
import com.example.delivery.annotaion.NatsHandler
import com.google.protobuf.Descriptors
import com.google.protobuf.GeneratedMessage
import com.google.protobuf.Message
import com.google.protobuf.Parser
import io.nats.client.Connection
import io.nats.client.Dispatcher
import jakarta.annotation.PostConstruct
import reactor.core.publisher.Mono
import java.lang.reflect.ParameterizedType

abstract class AbstractNatsController(
    private val connection: Connection,
    private val dispatcher: Dispatcher,
) {
    @PostConstruct
    fun init() {
        initializeDispatchers()
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    private fun initializeDispatchers() {
        val classAnnotation = this::class.java.getAnnotation(NatsController::class.java)
        val queueGroup = classAnnotation.queueGroup
        val methods = this::class.java.methods.filter { it.isAnnotationPresent(NatsHandler::class.java) }

        for (method in methods) {
            val methodAnnotation = method.getAnnotation(NatsHandler::class.java)
            val requestType = method.parameters.map { it.type }.first()
            val responseType = method.genericReturnType as ParameterizedType
            val typeArgument = responseType.actualTypeArguments[0] as Class<*>
            val parser = requestType.getMethod("parser").invoke(null) as Parser<*>
            val subject = if (classAnnotation.subjectPrefix.isEmpty()) {
                methodAnnotation.subject
            } else {
                "${classAnnotation.subjectPrefix}.${methodAnnotation.subject}"
            }

            dispatcher.subscribe(subject, queueGroup) { message ->
                try {
                    val request = parser.parseFrom(message.data)
                    val response = method.invoke(this, request) as Mono<GeneratedMessage>
                    response.subscribe { connection.publish(message.replyTo, it.toByteArray()) }
                } catch (e: RuntimeException) {
                    connection.publish(message.replyTo, buildErrorResponse(typeArgument, e).toByteArray())
                }
            }
        }
    }

    private fun buildErrorResponse(returnType: Class<*>, exception: Throwable): GeneratedMessage {
        val message = exception.message.orEmpty()
        returnType.methods.forEach { println(it) }
        val builder = returnType.getMethod("newBuilder").invoke(null) as Message.Builder
        val descriptor = returnType.getMethod("getDescriptor").invoke(null) as Descriptors.Descriptor

        val failureDescriptor = descriptor.findFieldByName("failure")
        val messageDescriptor = failureDescriptor.messageType.findFieldByName("message")

        return builder.apply {
            val failure = newBuilderForField(failureDescriptor).setField(messageDescriptor, message).build()
            setField(failureDescriptor, failure)
        }.build() as GeneratedMessage
    }
}
