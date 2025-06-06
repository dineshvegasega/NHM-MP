package com.nhm.distributor.models

data class ItemComplaintFeedback(
    val complaint_type: String,
    val date: String,
    val feedback_id: Int,
    val status: String,
    val subject: String,
    val type: String,
    val user_id: Int
)