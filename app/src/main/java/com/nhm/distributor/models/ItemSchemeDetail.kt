package com.nhm.distributor.models

data class ItemSchemeDetail(
    val apply_link: String,
    val description: String,
    val district_id: List<String>,
    val educational_qualification: String,
    val end_at: String,
    val gender: String,
    val municipality_id: List<String>,
    val name: String,
    val scheme_id: Int,
    val scheme_image: SchemeImage,
    val select_demography: Boolean,
    val social_category: List<String>,
    val start_at: String,
    val state_id: List<String>,
    val status: String,
    val type_of_marketplace: List<String>,
    val type_of_vending: List<String>
)

