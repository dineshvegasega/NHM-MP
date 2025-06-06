package com.nhm.distributor.models

data class ItemLiveNotice(
    var description: String,
    val district_id: Any,
    val educational_qualification: Any,
    val end_date: String,
    val gender: Any,
    val municipality_id: Any,
    var name: String,
    val notice_id: Int,
    val notice_image: NoticeImage,
    val select_demography: Boolean,
    val social_category: Any,
    val state_id: Any,
    val status: String,
    val type_of_marketplace: Any,
    val type_of_vending: Any
)

data class NoticeImage(
    val name: String,
    val url: String
)