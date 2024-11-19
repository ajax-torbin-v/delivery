package com.example.domainservice.order.infrastructure.mongo.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@TypeAlias("Order")
@Document(collection = MongoOrder.COLLECTION_NAME)
data class MongoOrder(
    @Id val id: ObjectId? = null,
    val items: List<MongoOrderItem>? = null,
    val shipmentDetails: MongoShipmentDetails? = null,
    val status: Status? = null,
    val userId: ObjectId? = null,
) {
    enum class Status {
        NEW,
        SHIPPING,
        COMPLETED,
        CANCELED,
    }

    companion object {
        const val COLLECTION_NAME = "order"
    }

    @TypeAlias("ShipmentDetails")
    data class MongoShipmentDetails(
        val city: String? = null,
        val street: String? = null,
        val building: String? = null,
        val index: String? = null,
    )

    @TypeAlias("OrderItem")
    data class MongoOrderItem(
        val productId: ObjectId? = null,
        val price: BigDecimal? = null,
        val amount: Int? = null,
    )
}
