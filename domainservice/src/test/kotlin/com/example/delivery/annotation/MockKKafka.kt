package com.example.delivery.annotation

import com.example.delivery.kafka.OrderUpdateStatusNotificationProducer
import com.example.delivery.kafka.OrderUpdateStatusProcessor
import com.example.delivery.kafka.OrderUpdateStatusProducer
import com.ninjasquad.springmockk.MockkBean

@MockkBean(
    relaxed = true,
    classes = [
        OrderUpdateStatusNotificationProducer::class,
        OrderUpdateStatusProcessor::class,
        OrderUpdateStatusProducer::class
    ]
)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class MockKKafka
