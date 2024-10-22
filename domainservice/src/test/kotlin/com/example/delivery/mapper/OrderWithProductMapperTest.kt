package com.example.delivery.mapper

import com.example.delivery.ProductFixture.product
import com.example.delivery.domain.projection.DomainOrderWithProduct
import com.example.delivery.mapper.OrderWithProductMapper.toDomain
import com.example.delivery.mapper.ProductMapper.toDomain
import com.example.delivery.mongo.projection.MongoOrderWithProduct
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrderWithProductMapperTest {
    @Test
    fun `mapper should handle nullable fields of shipment details`() {
        // GIVEN
        val mongoOrderItemWithProduct = MongoOrderWithProduct.MongoOrderItemWithProduct(product = product)
        val domainOrderItemWithProduct = DomainOrderWithProduct.DomainOrderItemWithProduct(
            BigDecimal.ZERO,
            0,
            product.toDomain()

        )
        // WHEN
        val actual = mongoOrderItemWithProduct.toDomain()

        // THEN
        assertEquals(domainOrderItemWithProduct, actual)
    }
}
