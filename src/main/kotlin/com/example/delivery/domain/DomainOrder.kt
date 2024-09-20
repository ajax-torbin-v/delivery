package com.example.delivery.domain

import org.bson.types.ObjectId
import java.math.BigDecimal

data class DomainOrder(
    val id: ObjectId,
    val items: List<DomainOrderItem>,
    val shipmentDetails: DomainShipmentDetails,
    val status: Status,
    val userId: ObjectId,
) {
    enum class Status {
        NEW,
        SHIPPING,
        COMPLETED,
        CANCELED,
        UNKNOWN
    }

    data class DomainShipmentDetails(
        val city: String = "",
        val street: String = "",
        val building: String = "",
        val index: String = "",
    )

    data class DomainOrderItem(
        val price: BigDecimal = BigDecimal.ZERO,
        val amount: Int = 0,
        val productId: ObjectId = ObjectId(),
    )
}
