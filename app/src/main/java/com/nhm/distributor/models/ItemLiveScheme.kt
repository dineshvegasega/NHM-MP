package com.nhm.distributor.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemLiveScheme(
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
    val foodIdentityImage: FoodIdentityImage,
    val foodItemImage: FoodItemImage,
    val foodMonth: String,
    val foodSignatureImage: FoodSignatureImage,
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
    val weight: String
): Parcelable{

}


//@Parcelize
//data class FoodIdentityImage(
//    val name: String,
//    val url: String
//): Parcelable{
//
//}
//
//
//@Parcelize
//data class FoodItemImage(
//    val name: String,
//    val url: String
//): Parcelable{
//
//}
//
//
//@Parcelize
//data class FoodSignatureImage(
//    val name: String,
//    val url: String
//): Parcelable{
//
//}