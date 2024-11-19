package com.example.domainservice.user.infrastructure.mongo.mapper

import com.example.domainservice.user.domain.DomainUser
import com.example.domainservice.user.infrastructure.mongo.entity.MongoUser
import org.bson.types.ObjectId

object UserMapper {
    fun DomainUser.toMongo(): MongoUser = MongoUser(
        id?.let { ObjectId(id) },
        fullName,
        phone,
        password,
    )

    fun MongoUser.toDomain(): DomainUser = DomainUser(
        id?.toString(),
        fullName ?: "none",
        phone ?: "none",
        password ?: ""
    )
}
