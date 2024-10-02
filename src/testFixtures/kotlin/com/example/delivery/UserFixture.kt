package com.example.delivery

import com.example.delivery.domain.DomainUser
import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.request.UpdateUserDTO
import com.example.delivery.dto.response.UserDTO
import com.example.delivery.mongo.MongoUser
import io.github.serpro69.kfaker.Faker
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Update

object UserFixture {
    val randomPhone = Faker().phoneNumber.phoneNumber()
    val randomFullName = Faker().funnyName.name()
    val randomUpdatedPhone = Faker().phoneNumber.phoneNumber()
    val randomUpdatedFullName = Faker().funnyName.name()

    val createUserDTO = CreateUserDTO(
        fullName = randomFullName,
        phone = randomPhone,
        password = "password",
    )
    val user = MongoUser(
        ObjectId("123456789011121314151617"),
        fullName = randomFullName,
        phone = randomPhone,
        password = "password",
    )

    val unsavedUser = user.copy(id = null)

    val domainUser = DomainUser(
        id = "123456789011121314151617",
        fullName = randomFullName,
        phone = randomPhone,
        password = "password",
    )

    val userDTO = UserDTO(
        "123456789011121314151617",
        fullName = randomFullName,
        phone = randomPhone,
    )

    val userUpdateObject = Update()
        .set("fullName", randomUpdatedFullName)
        .set("phone", randomUpdatedPhone)

    val updatedDomainUser = domainUser.copy(fullName = randomUpdatedFullName, phone = randomUpdatedPhone)

    val updatedUser = user.copy(fullName = randomUpdatedFullName, phone = randomUpdatedPhone)

    val updateUserDTO = UpdateUserDTO(
        fullName = randomUpdatedFullName,
        phone = randomUpdatedPhone
    )

}
