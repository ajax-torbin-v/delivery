package com.example.domainservice.order.infrastructure.mongo

import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.infrastructure.mongo.entity.MongoOrder
import com.example.domainservice.order.infrastructure.mongo.mapper.OrderMapper.toDomain
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
        val domainOrderItem = DomainOrder.DomainOrderItem(productId = id.toString())

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
            id = id.toString(),
            emptyList(),
            DomainOrder.DomainShipmentDetails(),
            DomainOrder.Status.UNKNOWN,
            userId = id.toString()
        )

        // WHEN
        val actual = mongoOrder.toDomain()

        // THEN
        assertEquals(domainOrder, actual)
    }
}
