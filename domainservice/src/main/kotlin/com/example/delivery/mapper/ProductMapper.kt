package com.example.delivery.mapper

import com.example.core.dto.request.CreateProductDTO
import com.example.core.dto.request.UpdateProductDTO
import com.example.delivery.domain.DomainProduct
import com.example.delivery.mongo.MongoProduct
import java.math.BigDecimal

object ProductMapper {

    fun CreateProductDTO.toMongo(): MongoProduct = MongoProduct(
        id = null,
        name,
        price,
        amount,
        measurement
    )

    fun MongoProduct.toDomain(): DomainProduct = DomainProduct(
        id.toString(),
        name ?: "no name",
        price ?: BigDecimal.ZERO,
        amountAvailable ?: 0,
        measurement ?: "none"
    )

    fun MongoProduct.toPartialUpdate(updateProductDTO: UpdateProductDTO): MongoProduct {
        return MongoProduct(
            id = id,
            name = updateProductDTO.name ?: name,
            price = updateProductDTO.price ?: price,
            amountAvailable = updateProductDTO.amountAvailable ?: amountAvailable,
            measurement = updateProductDTO.measurement ?: measurement
        )
    }
}
