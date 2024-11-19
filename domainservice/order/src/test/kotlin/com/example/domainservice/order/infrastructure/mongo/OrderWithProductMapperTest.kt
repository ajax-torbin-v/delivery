package com.example.domainservice.order.infrastructure.mongo

import com.example.domainservice.ProductFixture.product
import com.example.domainservice.order.domain.projection.DomainOrderWithProduct
import com.example.domainservice.order.infrastructure.mongo.entity.projection.MongoOrderWithProduct
import com.example.domainservice.order.infrastructure.mongo.mapper.OrderMapper.toDomain
import com.example.domainservice.product.infrastructure.mongo.mapper.ProductMapper.toDomain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrderWithProductMapperTest {
    @Test
    fun `mapper should handle nullable fields of shipment details`() {
        // GIVEN
        val mongoOrderItemWithProduct = MongoOrderWithProduct.MongoOrderItemWithProduct(product = product)
        val domainOrderItemWithProduct = DomainOrderWithProduct.DomainOrderItemWithProduct(
            product.toDomain(),
            BigDecimal.ZERO,
            0,
        )
        // WHEN
        val actual = mongoOrderItemWithProduct.toDomain()

        // THEN
        assertEquals(domainOrderItemWithProduct, actual)
    }
}
