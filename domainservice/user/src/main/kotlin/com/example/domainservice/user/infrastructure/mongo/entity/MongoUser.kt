package com.example.domainservice.user.infrastructure.mongo.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("User")
@Document(collection = MongoUser.COLLECTION_NAME)
data class MongoUser(
    @Id val id: ObjectId? = null,
    val fullName: String? = null,
    val phone: String? = null,
    val password: String? = null,
) {

    companion object {
        const val COLLECTION_NAME = "user"
    }
}
