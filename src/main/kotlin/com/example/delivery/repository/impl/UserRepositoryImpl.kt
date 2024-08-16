package com.example.delivery.repository.impl

import com.example.delivery.model.MongoUser
import com.example.delivery.repository.UserRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(val mongoTemplate: MongoTemplate) : UserRepository {
    val className = MongoUser::class.java
    override fun existsById(id: String): Boolean {
        return mongoTemplate.exists(Query().addCriteria(Criteria.where("_id").`is`(id)), MongoUser.COLLECTION_NAME)
    }

    override fun save(user: MongoUser): MongoUser {
        return mongoTemplate.save(user)
    }

    override fun findById(id: String): MongoUser? = mongoTemplate.findById(id, MongoUser::class.java)

    override fun addOrder(userId: String, orderId: String) {
        val query = Query().addCriteria(Criteria.where("_id").`is`(userId))
        val update = Update().push("orderIds", orderId)
        mongoTemplate.updateFirst(query, update, MongoUser::class.java)
    }

    override fun deleteById(id: String) {
        TODO("Not yet implemented")
    }


}