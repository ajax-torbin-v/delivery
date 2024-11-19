package com.example.domainservice.order.domain.projection

import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.product.domain.DomainProduct
import java.math.BigDecimal

data class DomainOrderWithProduct(
    val id: String,
    val items: List<DomainOrderItemWithProduct>,
    val shipmentDetails: DomainOrder.DomainShipmentDetails,
    val status: DomainOrder.Status,
    val userId: String,
) {

    data class DomainOrderItemWithProduct(
        val product: DomainProduct,
        val price: BigDecimal = BigDecimal.ZERO,
        val amount: Int = 0,
    )
}
