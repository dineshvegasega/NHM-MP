package com.nhm.distributor.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ItemNBPAFormRoot(
    @SerializedName("data")
    val data: List<ItemNBPAForm> = emptyList(),
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("status_code")
    val statusCode: Int? = null,
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("success")
    val success: Boolean? = false,
    @SerializedName("vendor_id")
    val vendor_id: String? = null,
    @SerializedName("meta")
    val meta: @RawValue Meta? = null,
) : Parcelable {

    override fun hashCode(): Int {
        var result = message.hashCode()
        if(message.isNullOrEmpty()){
            result = 31 * result + message.hashCode()
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemNBPAFormRoot

        if (statusCode != other.statusCode) return false
        if (success != other.success) return false
        if (data != other.data) return false
        if (message != other.message) return false
        if (token != other.token) return false
        if (vendor_id != other.vendor_id) return false
        if (meta != other.meta) return false

        return true
    }

}

//@Parcelize
//data class Meta(
//    val first_page: String,
//    val last_page: String,
//    val per_page: Int,
//    val prev_page_url: String,
//    val total_items: Int,
//    val total_pages: Int
//): Parcelable {
//
//}




@Parcelize
data class ItemNBPAForm(
    val aadhaarNumber: String,
    var address: String,
    val age: String,
    val bankAccount: String,
    val bankIFSC: String,
    val block: String,
    val business: String,
    val cardTypeAPLBPL: Int,
    val districtState: String,
    val dmcName: String,
    val fatherHusband: String,
    val fatherHusbandType: Int,
    val foodDate: String,
    val foodHeight: String,
    val foodIdentityImageTwo: @RawValue List<FoodIdentityImageTwo> = emptyList(),
    val foodIdentityImage: @RawValue FoodIdentityImage,
    val foodItemImage: @RawValue FoodItemImage,
    val foodMonth: String,
    val foodSignatureImage: @RawValue FoodSignatureImage,
    val gender: String,
    val height: String,
    val hemoglobinCheckupDate: String,
    val hemoglobinLevelAge: String,
    val id: Int,
    val mobileNumber: String,
    val mother: String,
    val muktiID: String,
    val nakshayID: String,
    var name: String,
    val numberOfChildren: Int,
    val numberOfMembers: Int,
    val patientCheckupDate: String,
    val treatmentSupporterEndDate: String,
    val treatmentSupporterMobileNumber: String,
    val treatmentSupporterName: String,
    val treatmentSupporterPost: String,
    val treatmentSupporterResult: String,
    val typeOfPatient: String,
    val weight: String,
    val created_at: String,
    val updated_at: String,
    val user_id: String

): Parcelable {
    override fun hashCode(): Int {
        var result = name.hashCode()
        if(name.isEmpty()){
            result = 31 * result + name.hashCode()
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemNBPAForm

        if (cardTypeAPLBPL != other.cardTypeAPLBPL) return false
        if (fatherHusbandType != other.fatherHusbandType) return false
        if (id != other.id) return false
        if (numberOfChildren != other.numberOfChildren) return false
        if (numberOfMembers != other.numberOfMembers) return false
        if (aadhaarNumber != other.aadhaarNumber) return false
        if (address != other.address) return false
        if (age != other.age) return false
        if (bankAccount != other.bankAccount) return false
        if (bankIFSC != other.bankIFSC) return false
        if (block != other.block) return false
        if (business != other.business) return false
        if (districtState != other.districtState) return false
        if (dmcName != other.dmcName) return false
        if (fatherHusband != other.fatherHusband) return false
        if (foodDate != other.foodDate) return false
        if (foodHeight != other.foodHeight) return false
        if (foodIdentityImageTwo != other.foodIdentityImageTwo) return false
        if (foodIdentityImage != other.foodIdentityImage) return false
        if (foodItemImage != other.foodItemImage) return false
        if (foodMonth != other.foodMonth) return false
        if (foodSignatureImage != other.foodSignatureImage) return false
        if (gender != other.gender) return false
        if (height != other.height) return false
        if (hemoglobinCheckupDate != other.hemoglobinCheckupDate) return false
        if (hemoglobinLevelAge != other.hemoglobinLevelAge) return false
        if (mobileNumber != other.mobileNumber) return false
        if (mother != other.mother) return false
        if (muktiID != other.muktiID) return false
        if (nakshayID != other.nakshayID) return false
        if (name != other.name) return false
        if (patientCheckupDate != other.patientCheckupDate) return false
        if (treatmentSupporterEndDate != other.treatmentSupporterEndDate) return false
        if (treatmentSupporterMobileNumber != other.treatmentSupporterMobileNumber) return false
        if (treatmentSupporterName != other.treatmentSupporterName) return false
        if (treatmentSupporterPost != other.treatmentSupporterPost) return false
        if (treatmentSupporterResult != other.treatmentSupporterResult) return false
        if (typeOfPatient != other.typeOfPatient) return false
        if (weight != other.weight) return false
        if (created_at != other.created_at) return false
        if (updated_at != other.updated_at) return false

        return true
    }
}

@Parcelize
data class FoodIdentityImageTwo(
    val name: String,
    val url: String
): Parcelable{
    override fun hashCode(): Int {
        var result = name.hashCode()
        if(name.isEmpty()){
            result = 31 * result + name.hashCode()
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodIdentityImage

        if (name != other.name) return false
        if (url != other.url) return false

        return true
    }
}





@Parcelize
data class FoodIdentityImage(
    val name: String,
    val url: String
): Parcelable{
    override fun hashCode(): Int {
        var result = name.hashCode()
        if(name.isEmpty()){
            result = 31 * result + name.hashCode()
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodIdentityImage

        if (name != other.name) return false
        if (url != other.url) return false

        return true
    }
}


@Parcelize
data class FoodItemImage(
    val name: String,
    val url: String
): Parcelable{
    override fun hashCode(): Int {
        var result = name.hashCode()
        if(name.isEmpty()){
            result = 31 * result + name.hashCode()
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodItemImage

        if (name != other.name) return false
        if (url != other.url) return false

        return true
    }
}


@Parcelize
data class FoodSignatureImage(
    val name: String,
    val url: String
): Parcelable{
    override fun hashCode(): Int {
        var result = name.hashCode()
        if(name.isEmpty()){
            result = 31 * result + name.hashCode()
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodSignatureImage

        if (name != other.name) return false
        if (url != other.url) return false

        return true
    }
}