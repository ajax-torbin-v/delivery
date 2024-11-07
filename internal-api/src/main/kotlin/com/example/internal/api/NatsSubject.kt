package com.example.internal.api

object NatsSubject {
    object Order {
        const val ORDER_PREFIX = "order"
        const val SAVE = "$ORDER_PREFIX.save"
        const val FIND_BY_ID = "$ORDER_PREFIX.find_by_id"
        const val UPDATE = "$ORDER_PREFIX.update"
        const val UPDATE_STATUS = "$ORDER_PREFIX.update_status"
        const val FIND_ALL_BY_USER_ID = "$ORDER_PREFIX.find_all_by_user_id"
        const val DELETE = "$ORDER_PREFIX.delete"
        fun getUpdateStatusByUserId(userId: String) = "$UPDATE_STATUS.$userId"
    }

    object Product {
        const val PRODUCT_PREFIX = "product"
        const val SAVE = "$PRODUCT_PREFIX.save"
        const val FIND_BY_ID = "$PRODUCT_PREFIX.find_by_id"
        const val UPDATE = "$PRODUCT_PREFIX.update"
        const val DELETE = "$PRODUCT_PREFIX.delete"
    }

    object User {
        const val USER_PREFIX = "user"
        const val SAVE = "$USER_PREFIX.save"
        const val FIND_BY_ID = "$USER_PREFIX.find_by_id"
        const val UPDATE = "$USER_PREFIX.update"
        const val DELETE = "$USER_PREFIX.delete"
    }
}
