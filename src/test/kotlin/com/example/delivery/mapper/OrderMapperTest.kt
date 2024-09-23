package com.example.delivery.mapper

import com.example.delivery.domain.DomainOrder
import com.example.delivery.mapper.OrderMapper.toDomain
import com.example.delivery.mongo.MongoOrder
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OrderMapperTest {
    @Test
    fun `mapper should handle nullable fields of shipment details`() {
        // GIVEN
        val mongoShipmentDetails = MongoOrder.MongoShipmentDetails()
        val domainShipmentDetails = DomainOrder.DomainShipmentDetails()

        // WHEN
        val actual = mongoShipmentDetails.toDomain()

        // THEN
        assertEquals(domainShipmentDetails, actual)
    }

    @Test
    fun `mapper should handle nullable fields of order item`() {
        // GIVEN
        val id = ObjectId()
        val mongoOrderItem = MongoOrder.MongoOrderItem(productId = id)
        val domainOrderItem = DomainOrder.DomainOrderItem(productId = id)

        // WHEN
        val actual = mongoOrderItem.toDomain()

        // THEN
        assertEquals(domainOrderItem, actual)
    }

    @Test
    fun `mapper should handle nullable fields of order`() {
        // GIVEN
        val id = ObjectId()
        val mongoOrder = MongoOrder(id = id, userId = id)
        val domainOrder = DomainOrder(
            id = id,
            emptyList(),
            DomainOrder.DomainShipmentDetails(),
            DomainOrder.Status.UNKNOWN,
            userId = id
        )

        // WHEN
        val actual = mongoOrder.toDomain()

        // THEN
        assertEquals(domainOrder, actual)
    }
}
