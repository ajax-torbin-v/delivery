package com.example.domainservice.order.infrastructure.mongo.entity.projection

import com.example.domainservice.order.infrastructure.mongo.entity.MongoOrder
import com.example.domainservice.product.infrastructure.mongo.entity.MongoProduct
import org.bson.types.ObjectId
import java.math.BigDecimal

data class MongoOrderWithProduct(
    val id: ObjectId? = null,
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
