package com.nhm.distributor.models

data class ItemCouponLiveList(
    val coupon_code: String,
    val coupon_discount: Double,
    val coupon_name: String,
    val coupon_validity: String,
    val created_at: String,
    val id: Int,
    val month_year: String,
    val no_of_month_year: Int,
    val state_id: String,
    val updated_at: String
)