package com.example.delivery.repository.impl

import com.example.delivery.mongo.MongoUser
import com.example.delivery.repository.UserRepository
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.exists
import org.springframework.data.mongodb.core.findAndModify
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
internal class UserRepositoryImpl(
    val mongoTemplate: ReactiveMongoTemplate,
) : UserRepository {

    override fun existsById(id: String): Mono<Boolean> {
        val query = Query().addCriteria(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.exists<MongoUser>(query)
    }

    override fun save(user: MongoUser): Mono<MongoUser> {
        return mongoTemplate.save(user)
    }

    override fun findById(id: String): Mono<MongoUser> {
        return mongoTemplate.findById<MongoUser>(id)
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query().addCriteria(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findAndRemove(query, MongoUser::class.java).thenReturn(Unit)
    }

    override fun update(id: String, update: Update): Mono<MongoUser> {
        val query = Query().addCriteria(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findAndModify<MongoUser>(
            query,
            update,
            FindAndModifyOptions.options().returnNew(true),
        )
    }
}
