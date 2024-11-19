package com.example.domainservice.order.application.mapper

import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.domain.DomainOrder.DomainShipmentDetails

object OrderMapper {
    fun DomainOrder.applyPartialUpdate(update: DomainOrder): DomainOrder {
        return DomainOrder(
            id = id,
            items = items,
            shipmentDetails = update.shipmentDetails.applyPartialUpdate(update.shipmentDetails),
            status = status,
            userId = update.userId.ifEmpty { userId }
        )
    }

    private fun DomainShipmentDetails.applyPartialUpdate(update: DomainShipmentDetails): DomainShipmentDetails {
        return DomainShipmentDetails(
            city = update.city.ifEmpty { city },
            street = update.street.ifEmpty { street },
            building = update.building.ifEmpty { building },
            index = update.index.ifEmpty { index },
        )
    }
}
