package com.example.delivery.repository.impl

import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.projection.MongoOrderWithProduct
import com.example.delivery.repository.OrderRepository
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.mongodb.MongoExpression
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.ArrayOperators
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Eq
import org.springframework.data.mongodb.core.aggregation.LookupOperation
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.aggregation.VariableOperators
import org.springframework.data.mongodb.core.exists
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
internal class OrderRepositoryImpl(var mongoTemplate: ReactiveMongoTemplate) : OrderRepository {

    override fun existsById(id: String): Mono<Boolean> {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.exists<MongoOrder>(query)
    }

    override fun findById(id: String): Mono<MongoOrderWithProduct> {
        val matchStage = MatchOperation(Criteria.where("_id").isEqualTo(id))

        val lookupStage = LookupOperation.newLookup()
            .from(MongoOrderWithProduct.MongoOrderItemWithProduct::product.name)
            .localField("items.${MongoOrder.MongoOrderItem::productId.name}")
            .foreignField("_id")
            .`as`("fetchedProducts")

        val addFieldsStage = Aggregation.addFields()
            .addFieldWithValue(
                MongoOrder::items.name,
                AggregationExpression.from(
                    MongoExpression.create(
                        mapOperation()
                    )
                )
            ).build()

        val pipeline = Aggregation.newAggregation(
            matchStage,
            lookupStage,
            addFieldsStage,
            Aggregation.project().andExclude("fetchedProducts")
        )

        val results = mongoTemplate.aggregate(
            pipeline,
            MongoOrder::class.java,
            MongoOrderWithProduct::class.java
        )
        return results.singleOrEmpty()
    }

    override fun save(order: MongoOrder): Mono<MongoOrder> {
        return mongoTemplate.save(order, MongoOrder.COLLECTION_NAME)
    }

    override fun updateOrderStatus(id: String, status: MongoOrder.Status): Mono<MongoOrder> {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        val update = Update.update(MongoOrder::status.name, status)
        return mongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions().returnNew(true),
            MongoOrder::class.java
        )
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findAndRemove(query, MongoOrder::class.java).thenReturn(Unit)
    }

    override fun updateOrder(id: String, update: Update): Mono<MongoOrder> {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions.options().returnNew(true),
            MongoOrder::class.java
        )
    }

    override fun findAllByUserId(userId: String): Flux<MongoOrder> {
        val query = Query(Criteria.where("userId").isEqualTo(ObjectId(userId)))
        return mongoTemplate.find<MongoOrder>(query)
    }

    private fun mapOperation(): String {
        val price = MongoOrderWithProduct.MongoOrderItemWithProduct::price.name
        val amount = MongoOrderWithProduct.MongoOrderItemWithProduct::amount.name
        val product = MongoOrderWithProduct.MongoOrderItemWithProduct::product.name
        val items = MongoOrder::items.name

        val mapBody = Document(price, "$\$item.$price")
            .append(amount, "$\$item.$amount")
            .append(product, elementAtOperation())

        val map = VariableOperators.Map
            .itemsOf("\$$items")
            .`as`("item")
            .andApply(AggregationExpression.from(MongoExpression.create(mapBody.toJson())))
        return map.toDocument().toJson()
    }

    private fun elementAtOperation(): Document {
        val productId = MongoOrder.MongoOrderItem::productId.name
        val product = MongoOrderWithProduct.MongoOrderItemWithProduct::product.name
        val filter = ArrayOperators.Filter
            .filter("\$fetchedProducts")
            .`as`(product)
            .by(
                Eq
                    .valueOf("$\$$product._id")
                    .equalTo("$\$item.$productId")
            )
            .toDocument()
            .toJson()

        return ArrayOperators.ArrayElemAt
            .arrayOf(AggregationExpression.from(MongoExpression.create(filter)))
            .elementAt(0)
            .toDocument()
    }
}
