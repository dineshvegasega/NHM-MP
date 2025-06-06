package com.nhm.distributor.screens.onboarding.register

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.nhm.distributor.networking.ApiInterface
import com.nhm.distributor.networking.CallHandler
import com.nhm.distributor.R
import com.nhm.distributor.networking.Repository
import com.nhm.distributor.databinding.LoaderBinding
import com.nhm.distributor.models.BaseResponseDC
import com.nhm.distributor.models.ItemDistrict
import com.nhm.distributor.models.ItemMarketplace
import com.nhm.distributor.models.ItemOrganization
import com.nhm.distributor.models.ItemPanchayat
import com.nhm.distributor.models.ItemPincode
import com.nhm.distributor.models.ItemState
import com.nhm.distributor.models.ItemVending
import com.nhm.distributor.networking.getJsonRequestBody
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.locale
import com.nhm.distributor.utils.mainThread
import com.nhm.distributor.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class RegisterVM @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    var data: Model = Model()

    var itemVending: ArrayList<ItemVending> = ArrayList()
    var vendingId: Int = 0

    var itemMarketplace: ArrayList<ItemMarketplace> = ArrayList()
    var marketplaceId: Int = 0


    var isAgree = MutableLiveData<Boolean>(false)

    var alertDialog: AlertDialog? = null
    init {
        val alert = AlertDialog.Builder(MainActivity.activity.get())
        val binding =
            LoaderBinding.inflate(LayoutInflater.from(MainActivity.activity.get()), null, false)
        alert.setView(binding.root)
        alert.setCancelable(false)
        alertDialog = alert.create()
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun show() {
        viewModelScope.launch {
            if (alertDialog != null) {
                alertDialog?.dismiss()
                alertDialog?.show()
            }
        }
    }

    fun hide() {
        viewModelScope.launch {
            if (alertDialog != null) {
                alertDialog?.dismiss()
            }
        }
    }


    fun vending(view: View) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemVending>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.vending()

                override fun success(response: Response<BaseResponseDC<List<ItemVending>>>) {
                    if (response.isSuccessful) {
//                        if (IS_LANGUAGE_ALL){
//                            if (MainActivity.context.get()!!
//                                    .getString(R.string.englishVal) == "" + locale
//                            ) {
//                                itemVending = response.body()?.data as ArrayList<ItemVending>
//                            } else {
//                                val itemStateTemp = response.body()?.data as ArrayList<ItemVending>
//                                show()
//                                mainThread {
//                                    itemStateTemp.forEach {
//                                        delay(50)
//                                        val nameChanged: String = callApiTranslate(""+locale, it.name)
//                                        apply {
//                                            it.name = nameChanged
//                                        }
//                                    }
//                                    itemVending = itemStateTemp
//                                    hide()
//                                }
//                            }
//                        } else {
                            itemVending = response.body()?.data as ArrayList<ItemVending>
//                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    fun marketplace(view: View) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemMarketplace>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.marketplace()

                override fun success(response: Response<BaseResponseDC<List<ItemMarketplace>>>) {
                    if (response.isSuccessful) {
//                        if (IS_LANGUAGE_ALL){
//                            if (MainActivity.context.get()!!
//                                    .getString(R.string.englishVal) == "" + locale
//                            ) {
//                                itemMarketplace = response.body()?.data as ArrayList<ItemMarketplace>
//                            } else {
//                                val itemStateTemp = response.body()?.data as ArrayList<ItemMarketplace>
//                                show()
//                                mainThread {
//                                    itemStateTemp.forEach {
//                                        delay(50)
//                                        val nameChanged: String = callApiTranslate(""+locale, it.name)
//                                        apply {
//                                            it.name = nameChanged
//                                        }
//                                    }
//                                    itemMarketplace = itemStateTemp
//                                    hide()
//                                }
//                            }
//                        } else {
                            itemMarketplace = response.body()?.data as ArrayList<ItemMarketplace>
//                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }




    var itemState: ArrayList<ItemState> = ArrayList()
    var stateId: Int = 0

    var itemDistrict: ArrayList<ItemDistrict> = ArrayList()
    var districtId: Int = 0

    var itemPanchayat: ArrayList<ItemPanchayat> = ArrayList()
    var panchayatId: Int = 0

    var itemPincode: ArrayList<ItemPincode> = ArrayList()
    var pincodeId: String = ""

    var currentAddress: String = ""


    fun state(view: View) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemState>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.state()
                @SuppressLint("SuspiciousIndentation")
                override fun success(response: Response<BaseResponseDC<List<ItemState>>>) {
                    if (response.isSuccessful) {
                        if (IS_LANGUAGE_ALL){
                            if (MainActivity.context.get()!!
                                    .getString(R.string.englishVal) == "" + locale
                            ) {
                                itemState = response.body()?.data as ArrayList<ItemState>
                            } else {
                                val itemStateTemp = response.body()?.data as ArrayList<ItemState>
                                show()
                                mainThread {
                                    itemStateTemp.forEach {
                                        delay(50)
                                        val nameChanged: String = callApiTranslate("" + locale, it.name)
                                        apply {
                                            it.name = nameChanged
                                        }
                                    }
                                    itemState = itemStateTemp
                                    hide()
                                }
                            }
                        } else {
                            itemState = response.body()?.data as ArrayList<ItemState>
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    fun district(view: View, id: Int) = viewModelScope.launch {
        val obj: JSONObject = JSONObject()
        obj.put(state_id, id)
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemDistrict>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.district(requestBody = obj.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<List<ItemDistrict>>>) {
                    if (response.isSuccessful) {
                        if (IS_LANGUAGE_ALL){
                            if (MainActivity.context.get()!!
                                    .getString(R.string.englishVal) == "" + locale
                            ) {
                                itemDistrict = response.body()?.data as ArrayList<ItemDistrict>
                                panchayat(view, id)
                            } else {
                                val itemStateTemp = response.body()?.data as ArrayList<ItemDistrict>
                                show()
                                mainThread {
                                    itemStateTemp.forEach {
                                        delay(50)
                                        val nameChanged: String = callApiTranslate(""+locale, it.name)
                                        apply {
                                            it.name = nameChanged
                                        }
                                    }
                                    itemDistrict = itemStateTemp
                                    hide()
                                    panchayat(view, id)
                                }
                            }
                        } else {
                            itemDistrict = response.body()?.data as ArrayList<ItemDistrict>
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    fun panchayat(view: View, id: Int) = viewModelScope.launch {
        val obj: JSONObject = JSONObject()
        obj.put(state_id, id)
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemPanchayat>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.panchayat(requestBody = obj.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<List<ItemPanchayat>>>) {
                    if (response.isSuccessful) {
                        if (IS_LANGUAGE_ALL){
                            if (MainActivity.context.get()!!
                                    .getString(R.string.englishVal) == "" + locale
                            ) {
                                itemPanchayat = response.body()?.data as ArrayList<ItemPanchayat>
                            } else {
                                val itemStateTemp = response.body()?.data as ArrayList<ItemPanchayat>
                                show()
                                mainThread {
                                    itemStateTemp.forEach {
                                        delay(50)
                                        val nameChanged: String = callApiTranslate(""+locale, it.name)
                                        apply {
                                            it.name = nameChanged
                                        }
                                    }
                                    itemPanchayat = itemStateTemp
                                    hide()
                                }
                            }
                        } else {
                            itemPanchayat = response.body()?.data as ArrayList<ItemPanchayat>
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    fun pincode(view: View, id: Int) = viewModelScope.launch {
        val obj: JSONObject = JSONObject()
        obj.put(district_id, id)
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemPincode>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.pincode(requestBody = obj.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<List<ItemPincode>>>) {
                    if (response.isSuccessful) {
                        itemPincode = response.body()?.data as ArrayList<ItemPincode>
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    var itemStateVending: ArrayList<ItemState> = ArrayList()
    var stateIdVending: Int = 0

    var itemDistrictVending: ArrayList<ItemDistrict> = ArrayList()
    var districtIdVending: Int = 0

    var itemPanchayatVending: ArrayList<ItemPanchayat> = ArrayList()
    var panchayatIdVending: Int = 0

    var itemPincodeVending: ArrayList<ItemPincode> = ArrayList()
    var pincodeIdVending: String = ""

    var itemLocalOrganizationVending: ArrayList<ItemOrganization> = ArrayList()
    var localOrganizationIdVending: Int = 0

    var currentAddressVending: String = ""

    fun stateCurrent(view: View) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemState>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.state()

                override fun success(response: Response<BaseResponseDC<List<ItemState>>>) {
                    if (response.isSuccessful) {
                        if (IS_LANGUAGE_ALL){
                            if (MainActivity.context.get()!!
                                    .getString(R.string.englishVal) == "" + locale
                            ) {
                                itemStateVending = response.body()?.data as ArrayList<ItemState>
                            } else {
                                val itemStateTemp = response.body()?.data as ArrayList<ItemState>
                                show()
                                mainThread {
                                    itemStateTemp.forEach {
                                        delay(50)
                                        val nameChanged: String = callApiTranslate(""+locale, it.name)
                                        apply {
                                            it.name = nameChanged
                                        }
                                    }
                                    itemStateVending = itemStateTemp
                                    hide()
                                }
                            }
                        } else {
                            itemStateVending = response.body()?.data as ArrayList<ItemState>
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }

    fun districtCurrent(view: View, id: Int) = viewModelScope.launch {
        val obj: JSONObject = JSONObject()
        obj.put(state_id, id)
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemDistrict>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.district(requestBody = obj.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<List<ItemDistrict>>>) {
                    if (response.isSuccessful) {
                        if (IS_LANGUAGE_ALL){
                            if (MainActivity.context.get()!!
                                    .getString(R.string.englishVal) == "" + locale
                            ) {
                                itemDistrictVending = response.body()?.data as ArrayList<ItemDistrict>
                                panchayatCurrent(view, id)
                            } else {
                                val itemStateTemp = response.body()?.data as ArrayList<ItemDistrict>
                                show()
                                mainThread {
                                    itemStateTemp.forEach {
                                        delay(50)
                                        val nameChanged: String = callApiTranslate(""+locale, it.name)
                                        apply {
                                            it.name = nameChanged
                                        }
                                    }
                                    itemDistrictVending = itemStateTemp
                                    hide()
                                    panchayatCurrent(view, id)
                                }
                            }
                        } else {
                            itemDistrictVending = response.body()?.data as ArrayList<ItemDistrict>
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }

    fun panchayatCurrent(view: View, id: Int) = viewModelScope.launch {
        val obj: JSONObject = JSONObject()
        obj.put(state_id, id)
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemPanchayat>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.panchayat(requestBody = obj.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<List<ItemPanchayat>>>) {
                    if (response.isSuccessful) {
                        if (IS_LANGUAGE_ALL){
                            if (MainActivity.context.get()!!
                                    .getString(R.string.englishVal) == "" + locale
                            ) {
                                itemPanchayatVending = response.body()?.data as ArrayList<ItemPanchayat>
                            } else {
                                val itemStateTemp = response.body()?.data as ArrayList<ItemPanchayat>
                                show()
                                mainThread {
                                    itemStateTemp.forEach {
                                        delay(50)
                                        val nameChanged: String = callApiTranslate(""+locale, it.name)
                                        apply {
                                            it.name = nameChanged
                                        }
                                    }
                                    itemPanchayatVending = itemStateTemp
                                    hide()
                                }
                            }
                        } else {
                            itemPanchayatVending = response.body()?.data as ArrayList<ItemPanchayat>
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }

    fun pincodeCurrent(view: View, id: Int) = viewModelScope.launch {
        val obj: JSONObject = JSONObject()
        obj.put(district_id, id)
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemPincode>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.pincode(requestBody = obj.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<List<ItemPincode>>>) {
                    if (response.isSuccessful) {
                        itemPincodeVending = response.body()?.data as ArrayList<ItemPincode>
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }

    fun localOrganisation(view: View, jsonObj: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemOrganization>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.localOrganisation(requestBody = jsonObj.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<List<ItemOrganization>>>) {
                    if (response.isSuccessful) {
//                        if (IS_LANGUAGE_ALL){
//                            if (MainActivity.context.get()!!
//                                    .getString(R.string.englishVal) == "" + locale
//                            ) {
//                                itemLocalOrganizationVending = response.body()?.data as ArrayList<ItemOrganization>
//                            } else {
//                                val itemStateTemp = response.body()?.data as ArrayList<ItemOrganization>
//                                show()
//                                mainThread {
//                                    itemStateTemp.forEach {
//                                        delay(50)
//                                        val nameChanged: String = callApiTranslate(""+locale, it.local_organisation_name)
//                                        apply {
//                                            it.local_organisation_name = nameChanged
//                                        }
//                                    }
//                                    itemLocalOrganizationVending = itemStateTemp
//                                    hide()
//                                }
//                            }
//                        } else {
                            itemLocalOrganizationVending =
                                response.body()?.data as ArrayList<ItemOrganization>
//                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    var documentDetails: Boolean? = false
    var governmentScheme: Boolean? = false

    var isSend = MutableLiveData<Boolean>(false)
    var isSendMutable = MutableLiveData<Boolean>(false)
    var isOtpVerified = false

    fun sendOTP(view: View, jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<Any>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.sendOTP(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<Any>>) {
                    if (response.isSuccessful) {
                        if (response.body()?.message == "OTP Sent successfully") {
                            isSend.value = true
                            var number = jsonObject.getString("mobile_no")
                            showSnackBar(view.resources.getString(R.string.otp_sent, number))
                        } else {
                            isSend.value = false
                            showSnackBar(view.resources.getString(R.string.user_already_exist))
                        }
                    } else {
                        isSend.value = false
                        showSnackBar(response.body()?.message.orEmpty())
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }

    fun verifyOTP(view: View, jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<Any>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.verifyOTP(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<Any>>) {
                    if (response.isSuccessful) {
                        if (response.body()?.data != null) {
                            isOtpVerified = true
                            isSendMutable.value = true
                            showSnackBar(view.resources.getString(R.string.otp_Verified_successfully))
                        } else {
                            isOtpVerified = false
                            isSendMutable.value = false
                            showSnackBar(view.resources.getString(R.string.invalid_OTP))
                        }
                    } else {
                        isOtpVerified = false
                        isSendMutable.value = false
                        showSnackBar(response.body()?.message.orEmpty())
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }

    fun onClick(view: View) {
        when (view.id) {

        }
    }


    fun register(view: View, jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<Any>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.register(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<Any>>) {
                    if (response.isSuccessful) {
                        showSnackBar(response.body()?.message.orEmpty())
                        Handler(Looper.getMainLooper()).postDelayed({
                            view.findNavController().navigate(
                                R.id.action_register_to_registerSuccessful,
                                Bundle().apply {
                                    putString("key", jsonObject.getString("vendor_first_name"))
                                })
                        }, 100)
                    } else {
                        showSnackBar(response.body()?.message.orEmpty())
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    fun registerWithFiles(
        view: View,
        hashMap: RequestBody,
        _string: String
    ) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<Any>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.registerWithFiles(hashMap)

                override fun success(response: Response<BaseResponseDC<Any>>) {
                    if (response.isSuccessful) {
                        showSnackBar(response.body()?.message.orEmpty())
                        view.findNavController()
                            .navigate(R.id.action_register_to_registerSuccessful, Bundle().apply {
                                putString("key", _string)
                            })
                    } else {
                        showSnackBar(response.body()?.message.orEmpty())
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    data class Model(
        var vendor_first_name: String? = null,
        var vendor_last_name: String? = null,
        var parent_first_name: String? = null,
        var parent_last_name: String? = null,
        var gender: String? = null,
        var date_of_birth: String? = null,
        var social_category: String? = null,
        var education_qualification: String? = null,
        var marital_status: String? = null,
        var spouse_name: String? = null,
        var current_state: String? = null,
        var current_district: String? = null,
        var municipality_panchayat_current: String? = null,
        var current_pincode: String? = null,
        var current_address: String? = null,

        var passportSizeImage: String? = null,
        var identificationImage: String? = null,


        var type_of_marketplace: String? = null,
        var marketpalce_others: String? = null,
        var type_of_vending: String? = null,
        var vending_others: String? = null,

        var total_years_of_business: String? = null,

        var open: String? = null,
        var close: String? = null,

        var vending_state: String? = null,
        var vending_district: String? = null,
        var vending_municipality_panchayat: String? = null,
        var vending_pincode: String? = null,
        var vending_address: String? = null,
        var localOrganisation: String? = null,


        var shopImage: String? = null,

        var documentDetails: Boolean? = false,
        var ImageUploadCOV: String? = null,
        var ImageUploadLOR: String? = null,
        var UploadSurveyReceipt: String? = null,
        var UploadChallan: String? = null,
        var UploadApprovalLetter: String? = null,

        var ImageUploadCOVBoolean: Boolean? = false,
        var ImageUploadLORBoolean: Boolean? = false,
        var UploadSurveyReceiptBoolean: Boolean? = false,
        var UploadChallanBoolean: Boolean? = false,
        var UploadApprovalLetterBoolean: Boolean? = false,

        var governmentScheme: Boolean? = false,
        var pmSwanidhiScheme: Boolean? = false,
        var otherScheme: Boolean? = false,
        var schemeName: String? = null,

        var mobile_no: String? = null,
        var otp: String? = null,
        var password: String? = null
    )



    fun callApiTranslate(_lang : String, _words: String) : String{
        return repository.callApiTranslate(_lang, _words)
    }
}