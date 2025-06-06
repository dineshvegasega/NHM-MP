package com.nhm.distributor.screens.mainActivity.menu

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ItemMenuModel(
    @SerializedName("name")
    var title: String? = null,
    @SerializedName("headlines")
    var titleChildArray: List<ItemChildMenuModel>? = null,
    @SerializedName("is_expanded")
    var isExpanded: Boolean? = false,
) : Serializable {
    override
    fun toString(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }
}