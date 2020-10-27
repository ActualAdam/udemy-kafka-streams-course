package com.actualadam.udemy.kafka.streams

import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import java.util.Properties

private val log = KotlinLogging.logger("WORD_COUNT")

fun main() {
    // confluent control center port 9021
    val config = Properties()
    config[StreamsConfig.APPLICATION_ID_CONFIG] = "word.count.app"
    config[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
    config[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
    config[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String()::class.java
    config[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String()::class.java

    val builder = StreamsBuilder()
    val wordCountInput =
        builder.stream<String, String>("word.count.in")

    val wordCounts = wordCountInput.mapValues { value -> value.toLowerCase() }
        .flatMapValues { value -> value.split(" ") }
        .selectKey { _, word -> word }
        .groupByKey()
        .count(Materialized.`as`("Counts"))

    wordCounts.toStream().to("word.count.out", Produced.with(Serdes.String(), Serdes.Long()))

    val streams = KafkaStreams(builder.build(), config)

    streams.start()
    log.info("HI")
    log.info(streams.toString())
    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}

