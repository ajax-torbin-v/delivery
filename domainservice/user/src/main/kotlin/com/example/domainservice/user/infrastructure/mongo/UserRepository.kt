package com.example.domainservice.user.infrastructure.mongo

import com.example.domainservice.user.application.port.output.UserRepositoryOutputPort
import com.example.domainservice.user.domain.DomainUser
import com.example.domainservice.user.infrastructure.mongo.entity.MongoUser
import com.example.domainservice.user.infrastructure.mongo.mapper.UserMapper.toDomain
import com.example.domainservice.user.infrastructure.mongo.mapper.UserMapper.toMongo
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class UserRepository(
    val mongoTemplate: ReactiveMongoTemplate,
) : UserRepositoryOutputPort {
    override fun save(user: DomainUser): Mono<DomainUser> {
        return mongoTemplate.save(user.toMongo()).map { it.toDomain() }
    }

    override fun findById(id: String): Mono<DomainUser> {
        return mongoTemplate.findById<MongoUser>(id).map { it.toDomain() }
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query().addCriteria(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.remove(query, MongoUser::class.java).thenReturn(Unit)
    }

    override fun update(user: DomainUser): Mono<DomainUser> {
        return mongoTemplate.save(user.toMongo()).map { it.toDomain() }
    }
}
