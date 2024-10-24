package com.example.core

import com.example.core.dto.request.CreateUserDTO
import com.example.core.dto.request.UpdateUserDTO
import io.github.serpro69.kfaker.Faker
import org.bson.types.ObjectId

object UserFixture {
    val randomUserId = ObjectId().toString()
    val randomPhone = Faker().phoneNumber.phoneNumber()
    val randomFullName = Faker().funnyName.name()
    val randomUpdatedPhone = Faker().phoneNumber.phoneNumber()
    val randomUpdatedFullName = Faker().funnyName.name()

    val createUserDTO = CreateUserDTO(
        fullName = randomFullName,
        phone = randomPhone,
        password = "password",
    )

    val updateUserDTO = UpdateUserDTO(
        fullName = randomUpdatedFullName,
        phone = randomUpdatedPhone
    )
}
