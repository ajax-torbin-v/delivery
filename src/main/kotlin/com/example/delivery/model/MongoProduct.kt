package com.example.delivery.model

import com.example.delivery.dto.response.ProductDTO
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Product")
@Document(collection = MongoProduct.COLLECTION_NAME)
data class MongoProduct(
    @Id val id: String? = null,
    val name: String?,
    val price: Double?,
    val amountAvailable: Int?,
    val measurement: String?) {

    fun toDTO(): ProductDTO = ProductDTO(
        id ?: "none",
        name ?: "no name",
        price ?: 0.0,
        amountAvailable ?: 0,
        measurement ?: "none")

    companion object {
        const val COLLECTION_NAME = "product"
    }
}
