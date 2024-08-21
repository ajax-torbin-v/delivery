package com.example.delivery.mapper

import com.example.delivery.domain.DomainUser
import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.response.UserDTO
import com.example.delivery.mongo.MongoUser

object UserMapper {
    fun DomainUser.toDTO(): UserDTO = UserDTO(
        (id ?: "none").toString(),
        fullName,
        phone,
    )

    fun CreateUserDTO.toMongo(): MongoUser = MongoUser(
        id = null,
        fullName,
        phone,
        password,
    )

    fun MongoUser.toDomain(): DomainUser = DomainUser(
        id,
        fullName ?: "none",
        phone ?: "none",
        password ?: ""
    )

    fun DomainUser.toMongo(): MongoUser = MongoUser(
        id, fullName, phone, password
    )
}
