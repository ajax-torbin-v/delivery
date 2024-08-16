package com.example.delivery.dto.request

import com.example.delivery.model.MongoOrder

data class UpdateOrderDTO (
    val id: String,
    val status: MongoOrder.Status
) {
}