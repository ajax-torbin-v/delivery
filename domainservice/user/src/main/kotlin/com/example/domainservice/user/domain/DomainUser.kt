package com.example.domainservice.user.domain

data class DomainUser(
    val id: String? = null,
    val fullName: String,
    val phone: String,
    val password: String,
)
