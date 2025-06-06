package com.nhm.distributor.models

data class ItemHistory(
    val complaint_type: String,
    val date: String,
    val feedback_id: Int,
    val media: Media,
    val message: String,
    val name: String,
    val status: String,
    val subject: String,
    val type: String,
    val user_id: Int
)

data class Media(
    val name: String,
    val url: String
)