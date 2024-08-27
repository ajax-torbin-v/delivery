package com.example.delivery.repository.impl

import com.example.delivery.mongo.MongoUser
import com.example.delivery.repository.UserRepository
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(val mongoTemplate: MongoTemplate) : UserRepository {
    val className = MongoUser::class.java
    override fun existsById(id: String): Boolean {
        val query = Query().addCriteria(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.exists(query, MongoUser.COLLECTION_NAME)
    }

    override fun save(user: MongoUser): MongoUser {
        return mongoTemplate.save(user)
    }

    override fun findById(id: String): MongoUser? {
        return mongoTemplate.findById(id, className)
    }

    override fun deleteById(id: String) {
        val query = Query().addCriteria(Criteria.where("_id").isEqualTo(id))
        mongoTemplate.findAndRemove(query, className)
    }

    override fun update(id: String, update: Update): MongoUser? {
        val query = Query().addCriteria(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true), className)
    }
}
