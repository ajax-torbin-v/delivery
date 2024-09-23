package com.example.delivery.repository.impl

import com.example.delivery.mongo.MongoOrder
import com.example.delivery.mongo.MongoProduct
import com.example.delivery.mongo.projection.MongoOrderWithProduct
import com.example.delivery.repository.OrderRepository
import org.bson.Document
import org.springframework.data.mongodb.MongoExpression
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.AggregationResults
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

@Repository
internal class OrderRepositoryImpl(var mongoTemplate: MongoTemplate) : OrderRepository {

    override fun existsById(id: String): Boolean {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.exists<MongoOrder>(query)
    }

    override fun findById(id: String): MongoOrderWithProduct? {
        val matchStage = MatchOperation(Criteria.where("_id").isEqualTo(id))

        val lookupStage = LookupOperation.newLookup()
            .from("product")
            .localField("items.productId")
            .foreignField("_id")
            .`as`("fetchedProducts")

        val addFieldsStage = Aggregation.addFields()
            .addFieldWithValue(
                "items",
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

        val results: AggregationResults<MongoOrderWithProduct> = mongoTemplate.aggregate(
            pipeline,
            MongoOrder::class.java,
            MongoOrderWithProduct::class.java
        )
        return results.uniqueMappedResult
    }

    override fun save(order: MongoOrder): MongoOrder {
        return mongoTemplate.save(order)
    }

    override fun updateOrderStatus(id: String, status: MongoOrder.Status): MongoOrder? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        val update = Update.update(MongoOrder::status.name, status)
        return mongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions().returnNew(true),
            MongoOrder::class.java
        )
    }

    override fun deleteById(id: String) {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        mongoTemplate.findAndRemove(query, MongoOrder::class.java)
    }

    override fun updateOrder(id: String, update: Update): MongoOrder? {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions.options().returnNew(true),
            MongoOrder::class.java
        )
    }

    override fun fetchProducts(productIds: List<String>): List<MongoProduct> {
        val query = Query(Criteria.where("_id").`in`(productIds))
        return mongoTemplate.find<MongoProduct>(query)
    }

    private fun mapOperation(): String {
        val mapBody = Document("price", "$\$item.price")
            .append("amount", "$\$item.amount")
            .append("product", elementAtOperation())

        val map = VariableOperators.Map
            .itemsOf("\$items")
            .`as`("item")
            .andApply(AggregationExpression.from(MongoExpression.create(mapBody.toJson())))
        return map.toDocument().toJson()
    }

    private fun elementAtOperation(): Document {
        val filter = ArrayOperators.Filter
            .filter("\$fetchedProducts")
            .`as`("product")
            .by(
                Eq
                    .valueOf("$\$product._id")
                    .equalTo("$\$item.productId")
            )
            .toDocument()
            .toJson()

        return ArrayOperators.ArrayElemAt
            .arrayOf(AggregationExpression.from(MongoExpression.create(filter)))
            .elementAt(0)
            .toDocument()
    }
}
