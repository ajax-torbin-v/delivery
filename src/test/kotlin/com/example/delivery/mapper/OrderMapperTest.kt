package com.example.delivery.mapper

import com.example.delivery.domain.DomainOrder
import com.example.delivery.mapper.OrderMapper.toDomain
import com.example.delivery.mongo.MongoOrder
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OrderMapperTest {
    @Test
    fun `mapper should handle nullable fields of shipment details`() {
        // GIVEN
        val mongoShipmentDetails = MongoOrder.MongoShipmentDetails()
        val domainShipmentDetails = DomainOrder.DomainShipmentDetails("", "", "", "")

        // WHEN
        val actual = mongoShipmentDetails.toDomain()

        // THEN
        assertEquals(domainShipmentDetails, actual)
    }
}
