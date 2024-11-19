package com.example.domainservice.order.domain

import java.math.BigDecimal

data class DomainOrder(
    val id: String?,
    val items: List<DomainOrderItem>,
    val shipmentDetails: DomainShipmentDetails,
    val status: Status,
    val userId: String,
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
        val productId: String = "",
        val price: BigDecimal = BigDecimal.ZERO,
        val amount: Int = 0,
    )
}
