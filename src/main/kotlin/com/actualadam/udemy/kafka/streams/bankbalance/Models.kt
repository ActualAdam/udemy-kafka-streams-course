package com.actualadam.udemy.kafka.streams.bankbalance

import com.actualadam.udemy.kafka.streams.lib.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer

@Serializable
data class Transaction(
    val name: String = "",
    val amount: Int = 0,
    @Serializable(with = InstantSerializer::class)
    val time: Instant = Clock.System.now()
)

class TransactionKafkaSerializer: Serializer<Transaction> {
    private val format = Json { encodeDefaults = true }

    override fun serialize(topic: String, data: Transaction): ByteArray {
        return format.encodeToString(data).toByteArray()
    }

    override fun close() { }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) { }
}

class TransactionKafkaDeserializer: Deserializer<Transaction> {
    override fun deserialize(topic: String, data: ByteArray): Transaction {
        return Json.decodeFromString(data.decodeToString())
    }

    override fun close() { }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) { }
}

class TransactionKafkaSerde: Serde<Transaction> {
    override fun serializer(): Serializer<Transaction> {
        return TransactionKafkaSerializer()
    }

    override fun deserializer(): Deserializer<Transaction> {
        return TransactionKafkaDeserializer()
    }
}


