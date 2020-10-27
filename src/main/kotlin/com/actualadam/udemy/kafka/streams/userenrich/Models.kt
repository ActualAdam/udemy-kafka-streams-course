package com.actualadam.udemy.kafka.streams.userenrich

import com.actualadam.udemy.kafka.streams.lib.InstantSerializer
import com.actualadam.udemy.kafka.streams.lib.UuidSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import java.util.UUID

val userDataSourceTopic = "userenrich.user.data"
val purchaseSourceTopic = "userenrich.purchases"


@Serializable
data class User(
    val userName: String,
    @Serializable(with = InstantSerializer::class)
    val userId: UUID
)

@Serializable
data class Purchase(
    @Serializable(with = UuidSerializer::class)
    val itemId: UUID,
    @Serializable(with = UuidSerializer::class)
    val userId: UUID,
    @Serializable(with = InstantSerializer::class)
    val transactionDate: Instant
)

data class UserPurchases(
    val user: User,
    val purchase: Purchase
)

data class PurchasesByUser(
    val user: User,
    val purchases: List<Purchase>
)
