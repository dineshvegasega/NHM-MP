package com.nhm.distributor.models

data class ItemChat(
    val complaint_type: String,
    val `data`: Data,
    val feedback_id: String,
    val media: MediaX,
    val message: String,
    val name: String,
    val registration_date: String,
    val status: String,
    val status_code: Int,
    val subject: String,
    val success: Boolean,
    val type: String,
    val user_id: Int,
    val user_type: String
)


data class Data(
    val current_page: Int,
    val `data`: List<DataX>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val links: List<Link>,
    val next_page_url: Any,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

data class DataX(
    val media: MediaX?,
    val reply: String,
    val reply_date: String,
    val status: String,
    val user_id: Int,
    val user_type: String,
    var dateShow: Boolean = false,
)

data class MediaX(
    val name: String,
    val url: String
)

data class Link(
    val active: Boolean,
    val label: String,
    val url: String
)