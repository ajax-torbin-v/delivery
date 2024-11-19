package com.example.domainservice.product.infrastructure.mongo.mapper

import com.example.domainservice.product.domain.DomainProduct
import com.example.domainservice.product.infrastructure.mongo.entity.MongoProduct
import org.bson.types.ObjectId
import java.math.BigDecimal

object ProductMapper {

    fun DomainProduct.toMongo(): MongoProduct = MongoProduct(
        id?.let { ObjectId(it) },
        name,
        price,
        amountAvailable,
        measurement
    )

    fun MongoProduct.toDomain(): DomainProduct = DomainProduct(
        id!!.toString(),
        name ?: "no name",
        price ?: BigDecimal.ZERO,
        amountAvailable ?: 0,
        measurement ?: "none"
    )
}
