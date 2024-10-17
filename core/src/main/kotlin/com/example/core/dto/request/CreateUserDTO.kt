package com.example.core.dto.request

data class CreateUserDTO(
    val fullName: String,
    val phone: String,
    val password: String,
)
