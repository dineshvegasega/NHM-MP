package com.nhm.distributor.models

data class ItemFormListDetail(
    val `data`: ListData,
    val message: String,
    val schemeDetail: List<SchemeDetail> = ArrayList(),
    val status_code: Int,
    val success: Boolean,
    val total_applied: Int
)


data class SchemeDetail(
    val dbtDate: String,
    val dbtDetails: String,
    val dbtRemark: String,
    val dbtTotalAmount: String,
    val dietChartDate: String,
    val dietChartEvaluation: String,
    val dietChartServiceProvider: String,
    val dietChartSuggestion: String,
    val extraGroceryPDS: String,
    val extraGroceryPDSDetails: String,
    val extraGroceryPDSRemark: String,
    val foodDate: String,
    val foodHeight: String,
    val foodIdentityImage: FoodIdentityImage,
    val foodItemImage: FoodItemImage,
    val foodMonth: String,
    val foodSignatureImage: FoodSignatureImage,
    val helpDetails: String,
    val helpRemark: String,
    val homeVisitDate: String,
    val homeVisitRemark: String,
    val homeVisitSignature: HomeVisitSignature,
    val homeVisitWeight: String,
    val multiVitaminDate: String,
    val multiVitaminDetails: String,
    val multiVitaminRemark: String,
    val multiVitaminTotalNumber: Int,
    val otherHelp: String,
    val projectCoordinatorSignature: ProjectCoordinatorSignature,
    val projectManagerSignature: ProjectManagerSignature,
    val schemeId: Int
)


data class ListData(
    val aadhaarNumber: String,
    val address: String,
    val age: String,
    val apply_link: String,
    val bankAccount: String,
    val bankIFSC: String,
    val block: String,
    val business: String,
    val cardTypeAPLBPL: Int,
    val description: String,
    val districtState: String,
    val district_id: String,
    val dmcName: String,
    val educational_qualification: String,
    val end_at: String,
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
    val hemoglobinLevelAge: String,
    val hemoglobinCheckupDate: String,
    val homeVisitFollowUp: String,
    val khaadySaamagreeVivaran: String,
    val labharthiDietChartMulyankan: String,
    val mobileNumber: String,
    val mother: String,
    val muktiID: String,
    val municipality_id: String,
    val nakshayID: String,
    val name: String,
    val numberOfChildren: Int,
    val numberOfMembers: Int,
    val pararaYojanaPrabindhakSignature: PararaYojanaPrabindhakSignature,
    val pararaYojanaSamanvayak: String,
    val pararaYojanaSamanvayakSignature: PararaYojanaSamanvayakSignature,
    val patientCheckupDate: String,
    val scheme_id: Int,
    val scheme_image: SchemeImageFor,
    val shaskiyaYojanaSahayata: String,
    val social_category: String,
    val start_at: String,
    val state_id: String,
    val status: String,
    val treatmentSupporterEndDate: String,
    val treatmentSupporterPost: String,
    val treatmentSupporterMobileNumber: String,
    val treatmentSupporterName: String,
    val treatmentSupporterResult: String,
    val typeOfPatient: String,
    val type_of_marketplace: String,
    val type_of_vending: String,
    val weight: String
)

// data class FoodIdentityImage(
//    val name: String,
//    val url: String
//)
//
//
//data class FoodItemImage(
//    val name: String,
//    val url: String
//)
//
//
//data class FoodSignatureImage(
//    val name: String,
//    val url: String
//)


data class HomeVisitSignature(
    val name: String,
    val url: String
)


data class ProjectCoordinatorSignature(
    val name: String,
    val url: String
)


data class ProjectManagerSignature(
    val name: String,
    val url: String
)

data class PararaYojanaPrabindhakSignature(
    val name: String,
    val url: String
)

data class PararaYojanaSamanvayakSignature(
    val name: String,
    val url: String
)

data class SchemeImageFor(
    val name: String,
    val url: String
)