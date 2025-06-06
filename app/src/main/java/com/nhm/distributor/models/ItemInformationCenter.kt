package com.nhm.distributor.models

data class ItemInformationCenter(
    val cover_image: CoverImage,
    var description: String,
    val information_id: Int,
    val information_link: String,
    var title: String
)


