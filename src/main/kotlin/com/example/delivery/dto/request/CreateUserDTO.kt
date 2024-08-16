package com.example.delivery.dto.request

import com.example.delivery.model.MongoUser

data class CreateUserDTO(
    val fullName: String,
    val phone: String,
    val password: String
    ){
    fun toEntity() = MongoUser(null, fullName, phone, password, mutableListOf())
}
