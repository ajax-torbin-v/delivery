package com.example.internal.api

object KafkaTopic {
    private const val REQUEST_PREFIX = "com.example.delivery.output.pub"

    object KafkaOrderStatusUpdateEvents {
        private const val ORDER_PREFIX = "$REQUEST_PREFIX.order"

        const val UPDATE = "$ORDER_PREFIX.update"
        const val NOTIFICATIONS = "$UPDATE.notifications"
    }
}
