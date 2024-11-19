package com.example.domainservice.order.infrastructure.mongo

import com.example.domainservice.order.application.port.output.OrderRepositoryOutputPort
import com.example.domainservice.order.domain.DomainOrder
import com.example.domainservice.order.domain.projection.DomainOrderWithProduct
import com.example.domainservice.order.infrastructure.mongo.entity.MongoOrder
import com.example.domainservice.order.infrastructure.mongo.entity.projection.MongoOrderWithProduct
import com.example.domainservice.order.infrastructure.mongo.mapper.OrderMapper.toDomain
import com.example.domainservice.order.infrastructure.mongo.mapper.OrderMapper.toMongo
import org.bson.Document
import org.bson.types.ObjectId
import org.springframework.data.mongodb.MongoExpression
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.AggregationExpression
import org.springframework.data.mongodb.core.aggregation.ArrayOperators
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators
import org.springframework.data.mongodb.core.aggregation.LookupOperation
import org.springframework.data.mongodb.core.aggregation.MatchOperation
import org.springframework.data.mongodb.core.aggregation.VariableOperators
import org.springframework.data.mongodb.core.exists
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoOrderRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
) : OrderRepositoryOutputPort {

    override fun existsById(id: String): Mono<Boolean> {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.exists<MongoOrder>(query)
    }

    override fun findById(id: String): Mono<DomainOrder> {
        return mongoTemplate.findById<MongoOrder>(id).map { it.toDomain() }
    }

    override fun findByIdFull(id: String): Mono<DomainOrderWithProduct> {
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
        return results.singleOrEmpty().map { it.toDomain() }
    }

    override fun save(order: DomainOrder): Mono<DomainOrder> {
        return mongoTemplate.save(order.toMongo(), MongoOrder.COLLECTION_NAME).map { it.toDomain() }
    }

    override fun updateOrderStatus(id: String, status: DomainOrder.Status): Mono<DomainOrder> {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        val update = Update.update(MongoOrder::status.name, status)
        return mongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions().returnNew(true),
            MongoOrder::class.java
        ).map { it.toDomain() }
    }

    override fun deleteById(id: String): Mono<Unit> {
        val query = Query.query(Criteria.where("_id").isEqualTo(id))
        return mongoTemplate.findAndRemove(query, MongoOrder::class.java).thenReturn(Unit)
    }

    override fun updateOrder(order: DomainOrder): Mono<DomainOrder> {
        return mongoTemplate.save(order.toMongo()).map { it.toDomain() }
    }

    override fun findAllByUserId(userId: String): Flux<DomainOrder> {
        val query = Query(Criteria.where("userId").isEqualTo(ObjectId(userId)))
        return mongoTemplate.find<MongoOrder>(query).map { it.toDomain() }
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
                ComparisonOperators.Eq
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
