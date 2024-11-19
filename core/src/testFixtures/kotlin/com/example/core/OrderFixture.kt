package com.example.core

import io.github.serpro69.kfaker.Faker
import org.bson.types.ObjectId

object OrderFixture {
    val randomOrderId = ObjectId().toString()
    val randomAmount = Faker().random.nextInt(1, 10)
    val randomCity = Faker().address.city()
    val randomStreet = Faker().address.streetName()
    val randomBuilding = Faker().address.buildingNumber()
    val randomIndex = Faker().address.countryCodeLong()
    val randomUpdateCity = Faker().address.city()
    val randomUpdateStreet = Faker().address.streetName()
    val randomUpdateBuilding = Faker().address.buildingNumber()
    val randomUpdateIndex = Faker().address.countryCodeLong()

}
