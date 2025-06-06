package com.nhm.distributor.models

data class Login(
    val approval_letter_image: ApprovalLetterImage,
    val availed_scheme: String,
    val aadhar_card: String,
    val aadhar_card_doc: AadharCardDoc,
    val challan_image: ChallanImage,
    val cov_image: CovImage,
    val current_location_of_business: String,
    val date_of_birth: String,
    val education_qualification: String,
    val gender: String,
    val id: Int,
    val identity_image_name: IdentityImageName,
    val is_current_add_and_birth_add_is_same: Any,
    val language: String,
    val local_organisation: LocalOrganisation,
    val local_organisation_others: String,
    val lor_image: LorImage,
    val marital_status: String,
    val marketpalce_others: String,
    val member_id: String,
    val membership_id: String,
    val membership_image: MembershipImage,
    val membership_validity: String,
    val mobile_no: String,
    val mobile_no_verification_status: String,
    val notification: String,
    val parent_first_name: String,
    val parent_last_name: String,
    val profile_image_name: ProfileImageName,
    val referral_code: String,
    val residential_address: String,
    val residential_district: ResidentialDistrict,
    val residential_municipality_panchayat: ResidentialMunicipalityPanchayat,
    val residential_pincode: ResidentialPincode,
    val residential_state: ResidentialState,
    val setting_id: Int,
    val shop_image: ShopImage,
    val social_category: String,
    val spouse_name: String,
    var status: String,
    val survey_receipt_image: SurveyReceiptImage,
    val text_size: Any,
    val total_years_of_business: String,
    val type_of_marketplace: Int,
    val type_of_vending: Int,
    val user_role: String,
    val user_type: String,
    val validity_from: String,
    val validity_to: String,
    val vending_address: String,
    val vending_district: VendingDistrict,
    val vending_documents: String,
    val vending_municipality_panchayat: VendingMunicipalityPanchayat,
    val vending_others: String,
    val vending_pincode: VendingPincode,
    val vending_state: VendingState,
    val vending_time_from: String,
    val vending_time_to: String,
    val vendor_first_name: String,
    val vendor_aadhaar_number: String,
    val vendor_last_name: String,
    var subscription_status : String ?= null
)

data class LorImage(
    val name: String,
    val url: String
)


data class ApprovalLetterImage(
    val name: String,
    val url: String
)

data class BirthDistrict(
    val id: Int,
    val name: String,
    val state_id: Int,
    val status: String
)

data class BirthPincode(
    val district_id: Int,
    val id: Int,
    val pincode: String
)

data class BirthState(
    val country_id: Int,
    val id: Int,
    val name: String
)

data class ChallanImage(
    val name: String,
    val url: String
)

data class CovImage(
    val name: String,
    val url: String
)

data class CurrentDistrict(
    val id: Int,
    val name: String,
    val state_id: Int,
    val status: String
)

data class CurrentPincode(
    val district_id: Int,
    val id: Int,
    val pincode: String
)

data class CurrentState(
    val country_id: Int,
    val id: Int,
    val name: String
)

data class AadharCardDoc(
    val name: String,
    val url: String
)

data class IdentityImageName(
    val name: String,
    val url: String
)

data class LocalOrganisation(
    val id: Int,
    val name: String
)

data class MembershipImage(
    val name: String,
    val url: String
)

data class MunicipalityPanchayatBirth(
    val id: Int,
    val name: String,
    val state_id: Int,
    val status: String
)

data class MunicipalityPanchayatCurrent(
    val id: Int,
    val name: String,
    val state_id: Int,
    val status: String
)

data class ProfileImageName(
    val name: String,
    val url: String
)

data class ResidentialDistrict(
    val id: Int,
    val name: String,
    val state_id: Int,
    val status: String
)

data class ResidentialMunicipalityPanchayat(
    val id: Int,
    val name: String,
    val state_id: Int,
    val status: String
)

data class ResidentialPincode(
    val district_id: Int,
    val id: Int,
    val pincode: String
)

data class ResidentialState(
    val country_id: Int,
    val id: Int,
    val name: String
)

data class SurveyReceiptImage(
    val name: String,
    val url: String
)

data class ShopImage(
    val name: String,
    val url: String
)


data class VendingDistrict(
    val id: Int,
    val name: String,
    val state_id: Int,
    val status: String
)

data class VendingPincode(
    val district_id: Int,
    val id: Int,
    val pincode: String
)

data class VendingMunicipalityPanchayat(
    val id: Int,
    val name: String,
    val state_id: Int,
    val status: String
)

data class VendingState(
    val country_id: Int,
    val id: Int,
    val name: String
)