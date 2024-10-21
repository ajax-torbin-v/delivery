package com.example.internal.api.subject

object NatsSubject {
    object Order {
        const val ORDER_PREFIX = "order"
        const val ORDER_SAVE = "${ORDER_PREFIX}.save"
        const val ORDER_FIND_BY_ID = "${ORDER_PREFIX}.find_by_id"
        const val ORDER_UPDATE = "${ORDER_PREFIX}.update"
        const val ORDER_UPDATE_STATUS = "${ORDER_PREFIX}.update_status"
        const val ORDER_FIND_ALL_BY_USER_ID = "${ORDER_PREFIX}.find_all_by_user_id"
        const val ORDER_DELETE = "${ORDER_PREFIX}.delete"
    }

    object Product {
        const val PRODUCT_PREFIX = "product"
        const val PRODUCT_SAVE = "${PRODUCT_PREFIX}.save"
        const val PRODUCT_FIND_BY_ID = "${PRODUCT_PREFIX}.find_by_id"
        const val PRODUCT_UPDATE = "${PRODUCT_PREFIX}.update"
        const val PRODUCT_DELETE = "${PRODUCT_PREFIX}.delete"
    }

    object User {
        const val USER_PREFIX = "user"
        const val USER_SAVE = "${USER_PREFIX}.save"
        const val USER_FIND_BY_ID = "${USER_PREFIX}.find_by_id"
        const val USER_UPDATE = "${USER_PREFIX}.update"
        const val USER_DELETE = "${USER_PREFIX}.delete"
    }
}
