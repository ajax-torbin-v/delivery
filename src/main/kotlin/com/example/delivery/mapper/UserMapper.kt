package com.example.delivery.mapper

import com.example.delivery.domain.DomainUser
import com.example.delivery.dto.request.CreateUserDTO
import com.example.delivery.dto.request.UpdateUserDTO
import com.example.delivery.dto.response.UserDTO
import com.example.delivery.mongo.MongoUser
import org.springframework.data.mongodb.core.query.Update

object UserMapper {
    fun DomainUser.toDTO(): UserDTO = UserDTO(
        id.toHexString(),
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
        id!!,
        fullName ?: "none",
        phone ?: "none",
        password ?: ""
    )

    fun DomainUser.toMongo(): MongoUser = MongoUser(
        id,
        fullName,
        phone,
        password
    )

    fun UpdateUserDTO.toUpdate(): Update {
        val update = Update()
        fullName?.let { update.set(MongoUser::fullName.name, it) }
        phone?.let { update.set(MongoUser::phone.name, it) }
        return update
    }
}
