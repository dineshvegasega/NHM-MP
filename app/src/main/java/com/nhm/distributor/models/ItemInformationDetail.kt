package com.nhm.distributor.models

data class ItemInformationDetail(
    val cover_image: CoverImage,
    val description: String,
    val information_id: Int,
    val information_link: String,
    val title: String
)
