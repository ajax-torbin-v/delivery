package com.example.delivery.service

import com.example.delivery.domain.DomainProduct
import com.example.delivery.exception.ProductAmountException
import com.example.delivery.exception.ProductPriceException
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ProductReserveService {
    data class AccountedProduct(
        val product: DomainProduct,
        val amount: Int,
    )

    fun reserveProducts(items: List<AccountedProduct>) {
        items.forEach { (product, amount) ->
            validateProductAvailability(product, amount)
        }
    }

    private fun validateProductAvailability(product: DomainProduct, amount: Int) {
        val availableAmount = product.amountAvailable
        if (availableAmount < amount) {
            throw ProductAmountException("Not enough items for product ${product.id}")
        }
    }

    fun calculateTotalPrice(items: List<AccountedProduct>): BigDecimal {
        return items.fold(BigDecimal.ZERO) { acc, (product, amount) ->
            val price = product.price
            if (price <= BigDecimal.ZERO) throw ProductPriceException("Price of product ${product.id} is 0 or negative")
            acc + price.multiply(BigDecimal.valueOf(amount.toDouble()))
        }
    }
}
