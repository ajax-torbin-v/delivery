package com.example.delivery.config

import com.example.delivery.config.DatabaseChangelog.Companion.ID
import com.example.delivery.mongo.MongoOrder
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort.DEFAULT_DIRECTION
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index

@ChangeUnit(id = ID, order = "001", author = "torbin.v@ajax.systems")
class DatabaseChangelog {

    @Execution
    fun createIndexOnUserCollection(mongoTemplate: MongoTemplate) {
        mongoTemplate.indexOps(MongoOrder.COLLECTION_NAME)
            .ensureIndex(Index(MongoOrder::userId.name, DEFAULT_DIRECTION))
    }

    @RollbackExecution
    fun rollback(mongoTemplate: MongoTemplate) {
        log.atInfo()
            .setMessage("Performing rollback on {}")
            .addArgument(ID)
            .log()

        val orderCollection = mongoTemplate.getCollection(MongoOrder.COLLECTION_NAME)
        val indexExists = orderCollection.listIndexes().any { it.getString("name") == "userId_1" }

        log.atInfo()
            .setMessage(
                if (indexExists) {
                    orderCollection.dropIndex("userId_1")
                    "Dropped index on userId from {} collection"
                } else {
                    "No index on userId found in {} collection to drop"
                }
            )
            .addArgument(MongoOrder.COLLECTION_NAME)
            .log()
    }

    companion object {
        const val ID = "OrderIndexCreationChangelog"
        private val log = LoggerFactory.getLogger(DatabaseChangelog::class.java)
    }
}
