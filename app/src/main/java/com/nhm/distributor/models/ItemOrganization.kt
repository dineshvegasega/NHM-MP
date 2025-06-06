package com.nhm.distributor.models

data class ItemOrganization(
    val district_id: String,
    val id: Int,
    val leader_name: String,
    var local_organisation_name: String,
    val mobile_no: String,
    val state_id: String,
    val status: String
)