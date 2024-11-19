package com.example.core

import io.github.serpro69.kfaker.Faker
import org.bson.types.ObjectId
import java.math.BigDecimal

object ProductFixture {
    val randomProductId = ObjectId().toString()
    val randomProductName = Faker().food.vegetables()
    val randomUpdateProductName = Faker().food.vegetables()
    val randomPrice = BigDecimal.valueOf(Faker().random.nextDouble() * 100)
    val randomUpdatePrice = BigDecimal.valueOf(Faker().random.nextDouble() * 100)
    val randomAmountAvailable = Faker().random.nextInt(10, 100)
    val randomUpdateAmountAvailable = Faker().random.nextInt(100, 1000)
    val randomMeasurement = Faker().food.measurements()
    val randomUpdateMeasurement = Faker().food.measurements()
}
