package com.example.delivery.domain

import com.example.delivery.mongo.MongoOrder.MongoShipmentDetails
import org.bson.types.ObjectId
import java.math.BigDecimal

data class DomainOrder(
    val id: ObjectId,
    val items: Map<ObjectId, Int>,
    val totalPrice: BigDecimal,
    val shipmentDetails: MongoShipmentDetails,
    val status: String,
    val userId: ObjectId?,
)
