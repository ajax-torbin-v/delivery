package com.example.delivery.mongo

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@TypeAlias("Product")
@Document(collection = MongoProduct.COLLECTION_NAME)
data class MongoProduct(
    @Id val id: ObjectId? = null,
    val name: String? = null,
    val price: BigDecimal? = null,
    val amountAvailable: Int? = null,
    val measurement: String? = null,
) {

    companion object {
        const val COLLECTION_NAME = "product"
    }
}
