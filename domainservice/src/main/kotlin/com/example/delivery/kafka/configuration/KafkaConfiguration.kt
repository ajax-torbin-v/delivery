package com.example.delivery.kafka.configuration

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

open class KafkaConfiguration(
    private val bootstrapServers: String,
    private val kafkaProperties: KafkaProperties,
) {
    fun createKafkaSender(
        properties: MutableMap<String, Any>,
    ): KafkaSender<String, ByteArray> {
        return KafkaSender.create(SenderOptions.create(properties))
    }

    fun createKafkaReceiver(
        properties: MutableMap<String, Any>,
        topic: String,
        groupId: String,
    ): KafkaReceiver<String, ByteArray> {
        properties[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        val options = ReceiverOptions.create<String, ByteArray>(properties).subscription(setOf(topic))
        return KafkaReceiver.create(options)
    }

    fun baseProducerProperties(
        customProperties: Map<String, Any> = mapOf(),
    ): MutableMap<String, Any> {
        val buildProperties: MutableMap<String, Any> = kafkaProperties.producer.buildProperties(null)
        val baseProperties: Map<String, Any> = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java.name,
        )
        buildProperties.putAll(baseProperties)
        buildProperties.putAll(customProperties)
        return buildProperties
    }

    fun baseConsumerProperties(
        customProperties: Map<String, Any> = mapOf(),
    ): MutableMap<String, Any> {
        val buildProperties: MutableMap<String, Any> = kafkaProperties.consumer.buildProperties(null)
        val baseProperties: Map<String, Any> = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java.name,
        )
        buildProperties.putAll(baseProperties)
        buildProperties.putAll(customProperties)
        return buildProperties
    }
}
