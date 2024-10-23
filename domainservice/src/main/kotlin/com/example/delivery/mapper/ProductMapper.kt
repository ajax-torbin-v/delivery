package com.example.delivery.mapper

import com.example.core.dto.request.CreateProductDTO
import com.example.core.dto.request.UpdateProductDTO
import com.example.delivery.domain.DomainProduct
import com.example.delivery.mongo.MongoProduct
import org.springframework.data.mongodb.core.query.Update
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

    fun UpdateProductDTO.toUpdate(): Update {
        val update = Update()
        name?.let { update.set(MongoProduct::name.name, it) }
        price?.let { update.set(MongoProduct::price.name, it) }
        amountAvailable?.let { update.set(MongoProduct::amountAvailable.name, it) }
        measurement?.let { update.set(MongoProduct::measurement.name, it) }
        return update
    }
}
