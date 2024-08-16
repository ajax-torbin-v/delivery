package com.example.delivery.dto.response

data class UserDTO(
    val id: String,
    val fullName: String,
    val phone: String,
    val orderIds: List<String>
) {
}
