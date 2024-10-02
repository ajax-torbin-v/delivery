package com.example.delivery

import com.example.delivery.domain.DomainUser
import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.request.UpdateUserDTO
import com.example.delivery.dto.response.UserDTO
import com.example.delivery.mongo.MongoUser
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Update

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

    val unsavedUser = user.copy(id = null)

    val domainUser = DomainUser(
        id = "123456789011121314151617",
        fullName = "FULL NAME",
        phone = "+31243123",
        password = "password",
    )

    val userDTO = UserDTO(
        "123456789011121314151617",
        fullName = "FULL NAME",
        phone = "+31243123",
    )

    val userUpdateObject = Update()
        .set("fullName", "UpdatedName")
        .set("phone", "+38-new-phone")

    val updatedDomainUser = domainUser.copy(fullName = "new full name", phone = "new phone")

    val updatedUser = user.copy(fullName = "UpdatedName", phone = "+38-new-phone")

    val updateUserDTO = UpdateUserDTO(
        fullName = "UpdatedName",
        phone = "+38-new-phone"
    )

}
