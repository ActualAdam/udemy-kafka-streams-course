package com.actualadam.udemy.kafka.streams.userenrich

import kotlinx.serialization.json.Json
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties
import java.util.Timer
import java.util.UUID

/*
 * There are 2 topics. One has general information about the users, and one is basically a stream
 * of purchases by the users.
 */

private val users = listOf("Bovary, Mulva, Loreola, Celeste, Dolores").map {
    User(it, UUID.randomUUID())
}

fun main() {
    val format = Json { encodeDefaults = true }
    val config = Properties()
    config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    config[ProducerConfig.RETRIES_CONFIG] = 3
    config[ProducerConfig.ACKS_CONFIG] = "all" // this setting appears to not be taking. logs as -1
    config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
    config[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true

    val purchaseGenerator = Timer("bank.balance.data.gen")
    val purchaseProducer = KafkaProducer<String, String>(config)
    Runtime.getRuntime().addShutdownHook(Thread(purchaseGenerator::cancel))
    Runtime.getRuntime().addShutdownHook(Thread(purchaseProducer::close))

}
