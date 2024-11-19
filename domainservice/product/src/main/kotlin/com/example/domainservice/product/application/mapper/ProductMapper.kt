package com.example.domainservice.product.application.mapper

import com.example.domainservice.product.domain.DomainProduct
import java.math.BigDecimal

object ProductMapper {
    fun DomainProduct.applyPartialUpdate(partialUpdate: DomainProduct): DomainProduct {
        return DomainProduct(
            partialUpdate.id!!,
            partialUpdate.name.ifEmpty { name },
            if (partialUpdate.price == BigDecimal.valueOf(-1L)) price else partialUpdate.price,
            if (partialUpdate.amountAvailable == -1) amountAvailable else partialUpdate.amountAvailable,
            partialUpdate.measurement.ifEmpty { measurement },
        )
    }
}
