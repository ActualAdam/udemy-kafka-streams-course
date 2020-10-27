@file:Suppress("DuplicatedCode")

package com.actualadam.udemy.kafka.streams

import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import java.util.Properties

private val log = KotlinLogging.logger("fav.color")

fun main() {
    // confluent control center port 9021
    val config = Properties()
    config[StreamsConfig.APPLICATION_ID_CONFIG] = "fav.color.app.1"
    config[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
    config[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
    config[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String()::class.java
    config[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String()::class.java
    config[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = 2000L
    config[StreamsConfig.PROCESSING_GUARANTEE_CONFIG] = StreamsConfig.EXACTLY_ONCE

    val builder = StreamsBuilder()
    val inTopic = "fav.color.in.1"
    val outTopic = "fav.color.out.1"
    val intermediaryTopic = "fav.color.mid.1"

    val instream =
        builder.stream<String, String>(inTopic)

    instream
        .mapValues { value -> value.toLowerCase() }
        .filter { _, color ->
            listOf("green", "red", "blue").contains(color)
        }.to(intermediaryTopic)

    val colorsByPerson = builder.table<String, String>(intermediaryTopic)
    colorsByPerson
        .groupBy { _, color -> KeyValue(color,color) }
        .count(Materialized.`as`("Counts"))
        .toStream()
        .to(outTopic, Produced.with(Serdes.String(), Serdes.Long()))


    val streams = KafkaStreams(builder.build(), config)
    streams.cleanUp()
    streams.start()
    log.info(streams.toString())
    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}
