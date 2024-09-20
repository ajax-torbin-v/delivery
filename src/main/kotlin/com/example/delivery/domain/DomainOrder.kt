package com.example.delivery.domain

import org.bson.types.ObjectId
import java.math.BigDecimal

data class DomainOrder(
    val id: ObjectId,
    val items: List<DomainOrderItem>,
    val shipmentDetails: DomainShipmentDetails,
    val status: String,
    val userId: ObjectId,
) {

    data class DomainShipmentDetails(
        val city: String = "none",
        val street: String = "none",
        val building: String = "none",
        val index: String = "none",
    )

    data class DomainOrderItem(
        val price: BigDecimal = BigDecimal.ZERO,
        val amount: Int = 0,
        val productId: ObjectId = ObjectId(),
    )
}
