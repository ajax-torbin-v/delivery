package com.example.delivery.service

import com.example.delivery.domain.DomainProduct
import com.example.delivery.dto.request.CreateOrderItemDTO
import com.example.delivery.exception.ProductAmountException
import org.springframework.stereotype.Service

@Service
class ProductReserveService {
    fun reserveProducts(orderItems: List<CreateOrderItemDTO>, products: Map<String, DomainProduct>) {
        orderItems.forEach { orderItem ->
            val product = products[orderItem.productId]
                ?: throw IllegalArgumentException("Product with id ${orderItem.productId} does not exist")

            if (product.amountAvailable < orderItem.amount) {
                throw ProductAmountException(
                    "Insufficient stock for product ${product.name}. " +
                            "Available: ${product.amountAvailable}, " +
                            "Requested: ${orderItem.amount}"
                )
            }
        }
    }
}
