package com.example.delivery.model

import com.example.delivery.dto.response.UserDTO
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("User")
@Document(collection = MongoUser.COLLECTION_NAME)
data class MongoUser (
    @Id val id: String?,
    val fullName: String?,
    val phone: String?,
    val password: String?,
    val orderIds: MutableList<String>?) {

    fun toDTO(): UserDTO = UserDTO(
        id ?: "none",
        fullName ?: "no name",
        phone ?: "no phone",
        orderIds ?: emptyList()
    )

    companion object {
        const val COLLECTION_NAME = "user"
    }
}
