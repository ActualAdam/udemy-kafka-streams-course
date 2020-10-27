package com.actualadam.udemy.kafka.streams.bankbalance

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Named
import java.util.Properties

private const val sourceTopic = "bank.balance.in"
private const val sinkTopic = "bank.balance.out"

fun main() {

    val config = Properties()
    config[StreamsConfig.APPLICATION_ID_CONFIG] = "bank.balance.app"
    config[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
    config[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
    config[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String()::class.java
    config[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = TransactionKafkaSerde::class.java
    config[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = 2000L
    config[StreamsConfig.PROCESSING_GUARANTEE_CONFIG] = StreamsConfig.EXACTLY_ONCE

    val builder = StreamsBuilder()
    val balanceStream = builder.stream<String, Transaction>(sourceTopic)
    balanceStream
        .groupByKey()
        .aggregate(
            { Transaction() },
            { key, cur, acc -> Transaction(name = key, amount = acc.amount + cur.amount, time = maxOf(cur.time, acc.time)) },
            Named.`as`("latest.balance.by.customer.aggregator"),
            Materialized.`as`("customer.balances")
        )
        .toStream()
        .to(sinkTopic)
    val streamsApp = KafkaStreams(builder.build(), config)
    Runtime.getRuntime().addShutdownHook(Thread(streamsApp::close))
    streamsApp.cleanUp()
    streamsApp.start()
}
