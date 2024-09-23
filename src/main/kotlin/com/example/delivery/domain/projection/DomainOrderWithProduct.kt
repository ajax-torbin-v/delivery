package com.example.delivery.domain.projection

import com.example.delivery.domain.DomainOrder
import com.example.delivery.domain.DomainProduct
import org.bson.types.ObjectId
import java.math.BigDecimal

data class DomainOrderWithProduct(
    val id: ObjectId,
    val items: List<DomainOrderItemWithProduct>,
    val shipmentDetails: DomainOrder.DomainShipmentDetails,
    val status: String,
    val userId: ObjectId,
) {

    data class DomainOrderItemWithProduct(
        val price: BigDecimal = BigDecimal.ZERO,
        val amount: Int = 0,
        val product: DomainProduct,
    )
}