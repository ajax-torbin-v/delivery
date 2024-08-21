package com.example.delivery.service

import com.example.delivery.domain.DomainProduct
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ProductReserveService {
    fun reserveProducts(items: Map<DomainProduct, Int>) {
        items.forEach { (product, amount) ->
            validateProductAvailability(product, amount)
        }
    }

    private fun validateProductAvailability(product: DomainProduct, amount: Int) {
        val availableAmount = product.amountAvailable
        if (availableAmount < amount) {
            throw ArithmeticException("Not enough items for product ${product.id}")
        }
    }

    fun calculateTotalPrice(items: Map<DomainProduct, Int>): BigDecimal {
        return items.entries.fold(BigDecimal.ZERO) { acc, (product, amount) ->
            val price = product.price
            if (price < BigDecimal.ZERO) throw ArithmeticException("Price of product ${product.id} is 0 or negative")
            acc + price.multiply(BigDecimal.valueOf(amount.toDouble()))

        }
    }
}
