package com.actualadam.udemy.kafka.streams.bankbalance

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random
import kotlin.random.nextInt

fun main() {
    val format = Json { encodeDefaults = true }
    val config = Properties()
    config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
    config[ProducerConfig.RETRIES_CONFIG] = 3
    config[ProducerConfig.ACKS_CONFIG] = "all" // this setting appears to not be taking. logs as -1
    config[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
    config[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true

    val generator = Timer("bank.balance.data.gen")
    val producer = KafkaProducer<String, String>(config)
    val appSourceTopic = "bank.balance.in"
    Runtime.getRuntime().addShutdownHook(Thread(generator::cancel))
    Runtime.getRuntime().addShutdownHook(Thread(producer::close))
    generator.scheduleAtFixedRate(
        object : TimerTask() {
            override fun run() {
                val txn = Transaction(
                    name = customers.random(),
                    amount = Random.nextInt(1..1000)
                )

                producer.send(
                    ProducerRecord(appSourceTopic, txn.name, format.encodeToString(txn))
                )
            }
        }, 0L, 10L
    )
}

private val customers = listOf("John", "Alex", "Astrud", "Megan", "Josef", "Lucy")



