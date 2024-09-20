package com.example.delivery.mongo

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import java.math.BigDecimal

data class MongoOrderWithProduct(
    @Id val id: ObjectId? = null,
    val items: List<MongoOrderItemWithProduct>? = null,
    val shipmentDetails: MongoOrder.MongoShipmentDetails? = null,
    val status: MongoOrder.Status? = null,
    val userId: ObjectId? = null,
) {

    data class MongoOrderItemWithProduct(
        val product: MongoProduct? = null,
        val price: BigDecimal? = null,
        val amount: Int? = null,
    )
}
