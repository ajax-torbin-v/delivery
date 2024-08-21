package com.example.delivery

import com.example.delivery.domain.DomainUser
import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.response.UserDTO
import com.example.delivery.mongo.MongoUser
import org.bson.types.ObjectId

object UserFixture {
    val createUserDTO = CreateUserDTO(
        fullName = "FULL NAME",
        phone = "+31243123",
        password = "password",
    )
    val user = MongoUser(
        ObjectId("123456789011121314151617"),
        fullName = "FULL NAME",
        phone = "+31243123",
        password = "password",
    )

    val domainUser = DomainUser(
        ObjectId("123456789011121314151617"),
        fullName = "FULL NAME",
        phone = "+31243123",
        password = "password",
    )

    val userDTO = UserDTO(
        "123456789011121314151617",
        fullName = "FULL NAME",
        phone = "+31243123",
    )

}
