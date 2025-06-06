package com.nhm.distributor.models

data class ItemAllScheme(
    val description: String,
    val end_at: String,
    val name: String,
    val scheme_id: Int,
    val scheme_image: SchemeImage,
    val start_at: String,
    val status: String
)

data class SchemeImage(
    val name: String,
    val url: String
)