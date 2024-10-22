package com.example.delivery.annotaion

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class NatsHandler(
    val subject: String,
)
