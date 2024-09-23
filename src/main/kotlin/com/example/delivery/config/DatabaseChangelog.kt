package com.example.delivery.config

import com.mongodb.client.MongoDatabase
import io.mongock.api.annotations.ChangeUnit
import io.mongock.api.annotations.Execution
import io.mongock.api.annotations.RollbackExecution
import org.bson.Document
import org.slf4j.LoggerFactory

@ChangeUnit(id = "OrderIndexCreationChangelog", order = "001", author = "torbin.v@ajax.systems")
class DatabaseChangelog {

    @Execution
    fun createIndexOnUserCollection(db: MongoDatabase) {
        val orderCollection = db.getCollection("order")

        val indexDocument = Document("userId", 1)

        orderCollection.createIndex(indexDocument)
    }

    @RollbackExecution
    fun rollback(db: MongoDatabase) {
        log.atInfo()
            .setMessage("Performing rollback on {}")
            .addArgument {
                val annotation = this::class.java.getAnnotation(ChangeUnit::class.java)
                annotation.id
            }
            .log()

        val orderCollection = db.getCollection("order")

        val indexExists = orderCollection.listIndexes()
            .any { it.getString("name") == "userId_1" }

        if (indexExists) {
            orderCollection.dropIndex("userId_1")
            log.atInfo()
                .setMessage("Dropped index on userId from order collection.")
                .log()
        } else {
            log.atInfo()
                .setMessage("No index on userId found in order collection to drop.")
                .log()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DatabaseChangelog::class.java)
    }
}
