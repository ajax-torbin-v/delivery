package com.example.delivery.domain

import org.bson.types.ObjectId

data class DomainUser(
    val id: ObjectId? = null,
    val fullName: String,
    val phone: String,
    val password: String,
)
