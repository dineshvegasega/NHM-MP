package com.nhm.distributor.models

data class ItemNotification(
    var position: Int,
    val notification_id: Int,
    val user_id: Int,
    val title: String,
    val status: String,
    val type: String,
    val type_id: Int,
    val sent_at: String,
    val is_read: Boolean,
)
