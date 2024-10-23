package com.example.delivery.domain.projection

import com.example.delivery.domain.DomainOrder
import com.example.delivery.domain.DomainProduct
import java.math.BigDecimal

data class DomainOrderWithProduct(
    val id: String,
    val items: List<DomainOrderItemWithProduct>,
    val shipmentDetails: DomainOrder.DomainShipmentDetails,
    val status: DomainOrder.Status,
    val userId: String,
) {

    data class DomainOrderItemWithProduct(
        val price: BigDecimal = BigDecimal.ZERO,
        val amount: Int = 0,
        val product: DomainProduct,
    )
}
