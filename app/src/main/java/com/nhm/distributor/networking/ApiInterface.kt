package com.nhm.distributor.networking

import com.google.gson.JsonElement
import com.nhm.distributor.models.BaseResponseDC
import com.nhm.distributor.models.ItemChat
import com.nhm.distributor.models.ItemAds
import com.nhm.distributor.models.ItemComplaintType
import com.nhm.distributor.models.ItemDistrict
import com.nhm.distributor.models.ItemMarketplace
import com.nhm.distributor.models.ItemOrganization
import com.nhm.distributor.models.ItemPanchayat
import com.nhm.distributor.models.ItemPincode
import com.nhm.distributor.models.ItemState
import com.nhm.distributor.models.ItemVending
import com.nhm.distributor.models.ItemMemberRoot
import com.nhm.distributor.models.ItemNBPAFormRoot
import com.nhm.distributor.models.ItemFormListDetail
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @POST(MOBILE_TOKEN)
    suspend fun mobileToken(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(LOGIN)
    suspend fun login(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @GET(VENDER_PROFILE+ "/{id}")
    suspend fun profile(
        @Path("id") id: String,
    ): Response<BaseResponseDC<JsonElement>>


    @Headers("Accept: application/json")
    @POST(VENDER_PROFILE_UPDATE+ "/{id}")
    suspend fun profileUpdate(
        @Path("id") id: String,
        @Body hashMap: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(VERIFY_OTP)
    suspend fun verifyOTPData(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(SEND_OTP)
    suspend fun sendOTP(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<Any>>


    @POST(VERIFY_OTP)
    suspend fun verifyOTP(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<Any>>


    @POST(RESEND_OTP)
    suspend fun reSendOTP(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<Any>>


    @POST(SIGN_UP)
    suspend fun register(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<Any>>


    @Headers("Accept: application/json")
    @POST(NewFeedback)
    suspend fun newFeedback(
        @Body hashMap: RequestBody
    ): Response<BaseResponseDC<Any>>


    @Headers("Accept: application/json")
    @POST(SIGN_UP)
    suspend fun registerWithFiles(
        @Body hashMap: RequestBody
    ): Response<BaseResponseDC<Any>>


    @Headers("Accept: application/json")
    @POST(CREATE_FORM)
    suspend fun createFormWithFiles(
        @Body hashMap: RequestBody
    ): Response<BaseResponseDC<Any>>


    @Headers("Accept: application/json")
    @POST(ADD_SCHEME_DETAIL)
    suspend fun registerWithFilesFoodItems(
        @Body hashMap: RequestBody
    ): Response<BaseResponseDC<Any>>


    @POST(PASSWORD_UPDATE)
    suspend fun passwordUpdate2(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<Any>>


    @GET(Vending)
    suspend fun vending(): Response<BaseResponseDC<List<ItemVending>>>


    @GET(SCHEME_DETAIL+ "/{id}")
    suspend fun formListDetail(
        @Path("id") id: String
    ): Response<ItemFormListDetail>


    @GET(CHECK_AADHAAR+ "/{id}")
    suspend fun checkAadhaarNo(
        @Path("id") id: String
    ): Response<ItemFormListDetail>


    @GET(Marketplace)
    suspend fun marketplace(): Response<BaseResponseDC<List<ItemMarketplace>>>


    @GET(STATE)
    suspend fun state(): Response<BaseResponseDC<List<ItemState>>>


    @POST(DISTRICT)
    suspend fun district(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<List<ItemDistrict>>>


    @POST(PANCHAYAT)
    suspend fun panchayat(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<List<ItemPanchayat>>>


    @POST(PINCODE)
    suspend fun pincode(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<List<ItemPincode>>>


    @POST(LOCAL_ORGANISATION)
    suspend fun localOrganisation(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<List<ItemOrganization>>>


    @POST(SchemeHistoryList)
    suspend fun schemeHistoryList(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(LiveScheme)
    suspend fun liveScheme(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(GLOBALSchemeHistoryList)
    suspend fun GLOBALSchemeHistoryList(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(SchemeApply)
    suspend fun applyLink(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @GET(SchemeDetail+ "/{id}")
    suspend fun schemeDetail(
        @Path("id") id: String,
    ): Response<BaseResponseDC<JsonElement>>


    @GET(TrainingDetail+ "/{id}")
    suspend fun trainingDetail(
        @Path("id") id: String,
    ): Response<BaseResponseDC<JsonElement>>


    @GET(NoticeDetail+ "/{id}")
    suspend fun noticeDetail(
        @Path("id") id: String,
    ): Response<BaseResponseDC<JsonElement>>


    @POST(NoticeLiveList)
    suspend fun liveNotice(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @GET(InformationDetail+ "/{id}")
    suspend fun informationDetail(
        @Path("id") id: String,
    ): Response<BaseResponseDC<JsonElement>>


    @POST(LiveTraining)
    suspend fun liveTraining(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(AllSchemeHistory)
    suspend fun allSchemeList(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(AllTrainingHistory)
    suspend fun allTrainingList(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(AllNoticeHistory)
    suspend fun allNoticeList(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @GET(Notifications)
    suspend fun notifications(
        @Query("page")  page: Int,
        @Query("is_read")  is_read: Boolean,
        @Query("user_id")  user_id: String
    ): Response<BaseResponseDC<JsonElement>>


    @POST(DeleteNotification)
    suspend fun deleteNotification(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(UpdateNotification)
    suspend fun updateNotification(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(ComplaintFeedback)
    suspend fun complaintFeedback(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(ComplaintFeedbackHistory)
    suspend fun complaintFeedbackHistory(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(InformationCenter)
    suspend fun informationCenter(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @GET(ADS_LIST)
    suspend fun adsList(): Response<BaseResponseDC<List<ItemAds>>>


    @GET(Complaint_Type)
    suspend fun complaintType(): Response<BaseResponseDC<List<ItemComplaintType>>>


    @GET(FeedbackConversationDetails+ "/{id}")
    suspend fun feedbackConversationDetails(
        @Path("id") id: String,
        @Query("page") page: String,
    ): Response<ItemChat>


    @Headers("Accept: application/json")
    @POST(AddFeedbackConversation)
    suspend fun addFeedbackConversation(
        @Body hashMap: RequestBody
    ): Response<BaseResponseDC<Any>>


    @POST(SaveSettings)
    suspend fun saveSettings(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(LOGOUT)
    suspend fun logout(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(PasswordUpdate)
    suspend fun passwordUpdate(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @Headers("Accept: application/json")
    @POST(UpdateNomineeDetails)
    suspend fun updateNomineeDetails(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(NomineeDetails)
    suspend fun nomineeDetails(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(Subscription)
    suspend fun subscription(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(PurchaseSubscription)
    suspend fun purchaseSubscription(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(TransactionHistory)
    suspend fun subscriptionHistory(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(TransactionDetails)
    suspend fun subscriptionDetails(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(CouponLiveList)
    suspend fun couponLiveList(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>




    @POST(GLOBALSchemeHistoryList)
    suspend fun getPopularMoviesListXX(
        @Body requestBody: RequestBody
    ): Response<ItemNBPAFormRoot>


    @POST(Aadharnumbercheck)
    suspend fun checkAadhaarNo(
        @Body requestBody: RequestBody
    ): Response<ItemNBPAFormRoot>


    @POST(GLOBALSchemeHistoryList)
    suspend fun getPopularMoviesListCount(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


    @POST(GLOBALSchemeHistoryListMembers)
    suspend fun getPopularMoviesListMembersCount(
        @Body requestBody: RequestBody
    ): Response<BaseResponseDC<JsonElement>>


//    @POST(GLOBALSchemeHistoryListMembers)
//    suspend fun getPopularMoviesListMembers(
//        @Body requestBody: RequestBody
//    ): Response<ItemProductRoot>


    @POST(membershipList)
    suspend fun getPopularMoviesListMember(
        @Body requestBody: RequestBody
    ): Response<ItemMemberRoot>
}