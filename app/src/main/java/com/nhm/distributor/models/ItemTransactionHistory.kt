package com.nhm.distributor.models

data class ItemTransactionHistory(
    val coupon_amount: String,
    val coupon_discount: String,
    val created_at: String,
    val date_time: String,
    val gst_amount: String,
    val gst_rate: String,
    val net_amount: String,
    val order_id: String,
    val membership_id: String,
    val payment_method: String,
    val payment_status: String,
    val payment_validity: String,
    val plan_type: String,
    val total_amount: String,
    val transaction_id: String,
    val updated_at: String,
    val user_id: Int,
    val id: Int
)
