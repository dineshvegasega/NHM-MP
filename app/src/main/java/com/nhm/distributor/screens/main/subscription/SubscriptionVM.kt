package com.nhm.distributor.screens.main.subscription

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.nhm.distributor.R
import com.nhm.distributor.databinding.DialogBottomSubscriptionBinding
import com.nhm.distributor.models.BaseResponseDC
import com.nhm.distributor.models.ItemSubscription
import com.nhm.distributor.models.ItemTransactionHistory
import com.nhm.distributor.networking.ApiInterface
import com.nhm.distributor.networking.CallHandler
import com.nhm.distributor.networking.Repository
import com.nhm.distributor.networking.getJsonRequestBody
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.utils.firstCharIfItIsLowercase
import com.nhm.distributor.utils.getFormatedStringFromDays
import com.nhm.distributor.utils.roundOffDecimal
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class SubscriptionVM @Inject constructor(private val repository: Repository) : ViewModel() {

    val adapter by lazy { SubscriptionHistoryAdapter(this) }


    var membershipCost: Double = 0.0
    var validity: String = ""
    var validityMonths: Int = 0
    var validityDays: Int = 0
    var gst: Double = 18.0
    var gstPrice : Double = 18.0
    var afterGst: Double = 0.0
    var couponDiscount: Double = 0.0
    var couponDiscountPrice: Double = 0.0
    var afterCouponDiscount: Double = 0.0
    var totalCost: Double = 0.0
    var monthYear: Int = 0
    var number: Int = 0

    var subscription = MutableLiveData<ItemSubscription>()
    fun subscription(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.subscription(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.data != null) {
                            subscription.value = Gson().fromJson(
                                response.body()!!.data,
                                ItemSubscription::class.java
                            )
                        }
                    }
                }

                override fun error(message: String) {
//                    super.error(message)
                    //  showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    var purchaseSubscription = MutableLiveData<Boolean>()
    fun purchaseSubscription(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.purchaseSubscription(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        purchaseSubscription.value = true
                        showSnackBar(MainActivity.context.get()?.resources!!.getString(R.string.subscription_added_successfully))
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    //  showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }



    private var itemLiveSubscriptionHistoryResult = MutableLiveData<BaseResponseDC<Any>>()
    val itemLiveSubscriptionHistory : LiveData<BaseResponseDC<Any>> get() = itemLiveSubscriptionHistoryResult
    fun subscriptionHistory(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.subscriptionHistory(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        itemLiveSubscriptionHistoryResult.value = response.body() as BaseResponseDC<Any>
                    }
                }

                override fun error(message: String) {
//                    super.error(message)
//                    showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }



    private var itemLiveSubscriptionHistoryResultSecond = MutableLiveData<BaseResponseDC<Any>>()
    val itemLiveSubscriptionHistorySecond : LiveData<BaseResponseDC<Any>> get() = itemLiveSubscriptionHistoryResultSecond
    fun subscriptionHistorySecond(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.subscriptionHistory(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        itemLiveSubscriptionHistoryResultSecond.value =  response.body() as BaseResponseDC<Any>
                    }
                }

                override fun error(message: String) {
//                    super.error(message)
//                    showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }

    var itemSubscriptionDetail = MutableLiveData<ItemTransactionHistory>()
    fun subscriptionDetail(jsonObject: JSONObject, root: View) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.subscriptionDetails(requestBody = jsonObject.getJsonRequestBody())
                @SuppressLint("SetTextI18n")
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.data != null) {
                            val typeToken = object : TypeToken<ItemTransactionHistory>() {}.type
                            val subsData = Gson().fromJson<ItemTransactionHistory>(Gson().toJson(
                                response.body()!!.data), typeToken)
                                val dialogBinding = DialogBottomSubscriptionBinding.inflate(
                                    root.context.getSystemService(
                                        Context.LAYOUT_INFLATER_SERVICE
                                    ) as LayoutInflater
                                )
                                val dialog = BottomSheetDialog(root.context)
                                dialog.setContentView(dialogBinding.root)
                                dialog.setOnShowListener { dia ->
                                    val bottomSheetDialog = dia as BottomSheetDialog
                                    val bottomSheetInternal: FrameLayout =
                                        bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
                                    bottomSheetInternal.setBackgroundResource(R.drawable.bg_top_round_corner)
                                }
                                dialog.show()
                                dialogBinding.apply {
                                    imageCross.singleClick {
                                        dialog.dismiss()
                                    }

                                    btSave.singleClick {
                                        itemSubscriptionDetail.value = subsData
                                    }

//                                    textUserIdVal.text = ""+subsData.user_id
                                    textMembershipIdVal.text = ""+subsData.membership_id
                                    textOrderIdVal.text = ""+subsData.order_id
                                    textDateTimeVal.text = ""+subsData.date_time
                                    textTransactionIdVal.text = ""+subsData.transaction_id
                                    textPlanTypeVal.text = ""+subsData.plan_type.firstCharIfItIsLowercase()
                                    textPaymentMethodVal.text = ""+subsData.payment_method
                                    textPaymentStatusVal.text = ""+subsData.payment_status.firstCharIfItIsLowercase()
                                    textValidityVal.text = ""+getFormatedStringFromDays(subsData.payment_validity.toInt(), 1, root.context)
                                    textNetAmountVal.text = ""+root.resources.getString(R.string.rupees, subsData.net_amount.toDouble().roundOffDecimal())
                                    textCouponDiscount.text = root.resources.getString(R.string.discount, "${subsData.coupon_discount}%")
                                    textCouponDiscountVal.text = ""+ root.resources.getString(R.string.rupees, subsData?.coupon_amount?.toDouble()?.roundOffDecimal())
                                    textGst.text = root.resources.getString(R.string.gst, "${subsData.gst_rate}%")
                                    textGstVal.text = ""+root.resources.getString(R.string.rupees, subsData.gst_amount.toDouble().roundOffDecimal())
                                    textTotalAmountVal.text = ""+root.resources.getString(R.string.rupees, subsData.total_amount.toDouble().roundOffDecimal())

                                    if(subsData.coupon_discount.toDouble() > 0.0){
                                        textCouponDiscount.visibility = View.VISIBLE
                                        textCouponDiscountVal.visibility = View.VISIBLE
                                    } else {
                                        textCouponDiscount.visibility = View.GONE
                                        textCouponDiscountVal.visibility = View.GONE
                                    }
                                }
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    //  showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }





    var couponLiveListCalled = MutableLiveData<Boolean>(false)
    private var itemCouponLiveListResult = MutableLiveData<BaseResponseDC<Any>>()
    val itemCouponLiveList: LiveData<BaseResponseDC<Any>> get() = itemCouponLiveListResult
    fun couponLiveList(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.couponLiveList(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        itemCouponLiveListResult.value = response.body() as BaseResponseDC<Any>
                        couponLiveListCalled.value = true
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    //  showSnackBar(message)
                   // couponLiveListCalled.value = false
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    public override fun onCleared() {
        super.onCleared()
        monthYear = 0
        number = 0
    }


}

