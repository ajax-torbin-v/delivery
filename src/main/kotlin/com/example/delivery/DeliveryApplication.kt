package com.example.delivery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DeliveryApplication

@SuppressWarnings("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<DeliveryApplication>(*args)
}
