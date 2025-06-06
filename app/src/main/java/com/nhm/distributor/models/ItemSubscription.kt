package com.nhm.distributor.models

data class ItemSubscription(
    val id: Int,
    val state_id: Int,
    val subscription_cost: Double = 0.00
)