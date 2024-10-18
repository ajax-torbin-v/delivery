package com.example.delivery.annotaion

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NatsController(
    val subjectPrefix: String = "",
    val queueGroup: String,
)
