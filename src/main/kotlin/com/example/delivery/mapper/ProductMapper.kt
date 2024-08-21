package com.example.delivery.mapper

import com.example.delivery.domain.DomainProduct
import com.example.delivery.dto.request.CreateProductDTO
import com.example.delivery.dto.response.ProductDTO
import com.example.delivery.mongo.MongoProduct
import java.math.BigDecimal

object ProductMapper {
    fun DomainProduct.toDTO(): ProductDTO = ProductDTO(
        (id ?: "none").toString(), name, price, amountAvailable, measurement
    )

    fun CreateProductDTO.toMongo(): MongoProduct = MongoProduct(
        id = null, name, price, amount, measurement
    )

    fun DomainProduct.toMongo(): MongoProduct = MongoProduct(
        id, name, price, amountAvailable, measurement
    )

    fun MongoProduct.toDomain(): DomainProduct = DomainProduct(
        id,
        name ?: "no name",
        price ?: BigDecimal.ZERO,
        amountAvailable ?: 0,
        measurement ?: "none"
    )
}
