package com.example.delivery.mapper

import com.example.delivery.domain.DomainOrder
import com.example.delivery.domain.projection.DomainOrderWithProduct
import com.example.delivery.mapper.OrderMapper.toDomain
import com.example.delivery.mapper.ProductMapper.toDomain
import com.example.delivery.mongo.projection.MongoOrderWithProduct
import java.math.BigDecimal

object OrderWithProductMapper {
    fun MongoOrderWithProduct.toDomain(): DomainOrderWithProduct = DomainOrderWithProduct(
        id.toString(),
        items = items?.map { it.toDomain() } ?: emptyList(),
        shipmentDetails = shipmentDetails?.toDomain() ?: DomainOrder.DomainShipmentDetails(),
        status = status?.name ?: "UNKNOWN",
        userId = userId.toString(),
    )

    fun MongoOrderWithProduct.MongoOrderItemWithProduct.toDomain(): DomainOrderWithProduct.DomainOrderItemWithProduct =
        DomainOrderWithProduct.DomainOrderItemWithProduct(
            price ?: BigDecimal.ZERO,
            amount ?: 0,
            product!!.toDomain()
        )
}
