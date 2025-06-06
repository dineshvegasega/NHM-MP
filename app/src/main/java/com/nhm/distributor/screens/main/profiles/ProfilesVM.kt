package com.nhm.distributor.screens.main.profiles

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhm.distributor.networking.ApiInterface
import com.nhm.distributor.networking.CallHandler
import com.nhm.distributor.networking.Repository
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.nhm.distributor.R
import com.nhm.distributor.databinding.LoaderBinding
import com.nhm.distributor.datastore.DataStoreKeys
import com.nhm.distributor.datastore.DataStoreUtil.saveData
import com.nhm.distributor.datastore.DataStoreUtil.saveObject
import com.nhm.distributor.models.BaseResponseDC
import com.nhm.distributor.models.Login
import com.nhm.distributor.models.ItemDistrict
import com.nhm.distributor.models.ItemMarketplace
import com.nhm.distributor.models.ItemOrganization
import com.nhm.distributor.models.ItemPanchayat
import com.nhm.distributor.models.ItemPincode
import com.nhm.distributor.models.ItemState
import com.nhm.distributor.models.ItemVending
import com.nhm.distributor.networking.*
import com.nhm.distributor.networking.getJsonRequestBody
import com.nhm.distributor.screens.mainActivity.MainActivity
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
class ProfilesVM @Inject constructor(private val repository: Repository): ViewModel() {

    var isEditable = MutableLiveData<Boolean>(false)

    var data : Model = Model()

    var itemState : ArrayList<ItemState> = ArrayList()
    var stateId : Int = 0

    var itemDistrict : ArrayList<ItemDistrict> = ArrayList()
    var districtId : Int = 0

    var itemPanchayat : ArrayList<ItemPanchayat> = ArrayList()
    var panchayatId : Int = 0

    var itemPincode : ArrayList<ItemPincode> = ArrayList()
    var pincodeId : String = ""

    var currentAddress : String = ""



//    var locale: Locale = Locale.getDefault()
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




    fun state(view: View) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemState>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.state()

                override fun success(response: Response<BaseResponseDC<List<ItemState>>>) {
                    if (response.isSuccessful){
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
                                        val nameChanged: String = callApiTranslate(""+locale, it.name)
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
        obj.put("state_id", id)
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemDistrict>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.district(requestBody = obj.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<List<ItemDistrict>>>) {
                    if (response.isSuccessful){
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
        obj.put("state_id", id)
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemPanchayat>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.panchayat(requestBody = obj.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<List<ItemPanchayat>>>) {
                    if (response.isSuccessful){
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
                    if (response.isSuccessful){
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







    var itemStateVending : ArrayList<ItemState> = ArrayList()
    var stateIdVending : Int = 0

    var itemDistrictVending : ArrayList<ItemDistrict> = ArrayList()
    var districtIdVending : Int = 0

    var itemPanchayatVending : ArrayList<ItemPanchayat> = ArrayList()
    var panchayatIdVending : Int = 0

    var itemPincodeVending : ArrayList<ItemPincode> = ArrayList()
    var pincodeIdVending : String = ""

    var itemLocalOrganizationVending : ArrayList<ItemOrganization> = ArrayList()
    var localOrganizationIdVending : Int = 0

    var currentAddressCurrent : String = ""

    fun stateCurrent(view: View) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemState>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.state()

                override fun success(response: Response<BaseResponseDC<List<ItemState>>>) {
                    if (response.isSuccessful){
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
                    if (response.isSuccessful){
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
                    if (response.isSuccessful){
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
                    if (response.isSuccessful){
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
                    if (response.isSuccessful){
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
                            itemLocalOrganizationVending = response.body()?.data as ArrayList<ItemOrganization>
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









    var itemVending : ArrayList<ItemVending> = ArrayList()
    var vendingId : Int = 0
    var vendingTrue = MutableLiveData<Boolean>(false)
    fun vending(view: View) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemVending>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.vending()

                override fun success(response: Response<BaseResponseDC<List<ItemVending>>>) {
                    if (response.isSuccessful){
//                        if (IS_LANGUAGE){
//                            if (MainActivity.context.get()!!
//                                    .getString(R.string.englishVal) == "" + locale
//                            ) {
//                                itemVending = response.body()?.data as ArrayList<ItemVending>
//                                vendingTrue.value = true
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
//                                    vendingTrue.value = true
//                                    hide()
//                                }
//                            }
//                        } else {
                            itemVending = response.body()?.data as ArrayList<ItemVending>
                            vendingTrue.value = true
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



    var itemMarketplace : ArrayList<ItemMarketplace> = ArrayList()
    var marketplaceId : Int = 0
    var marketPlaceTrue = MutableLiveData<Boolean>(false)
    fun marketplace(view: View) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemMarketplace>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.marketplace()

                override fun success(response: Response<BaseResponseDC<List<ItemMarketplace>>>) {
                    if (response.isSuccessful){
//                        if (IS_LANGUAGE){
//                            if (MainActivity.context.get()!!
//                                    .getString(R.string.englishVal) == "" + locale
//                            ) {
//                                itemMarketplace = response.body()?.data as ArrayList<ItemMarketplace>
//                                marketPlaceTrue.value = true
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
//                                    marketPlaceTrue.value = true
//                                    hide()
//                                }
//                            }
//                        } else {
                            itemMarketplace = response.body()?.data as ArrayList<ItemMarketplace>
                            marketPlaceTrue.value = true
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






    fun profileUpdate(view: View, _id: String, hashMap: RequestBody) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.profileUpdate(_id, hashMap)

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        profile(view, response.body()!!.vendor_id!!)
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






    fun profile(view: View, _id: String) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.profile(_id)

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        showSnackBar(view.resources.getString(R.string.profile_updated_successfully))
                        if(response.body()!!.data != null){
                            saveData(
                                DataStoreKeys.AUTH,
                                response.body()!!.token ?: ""
                            )
                            saveObject(
                                DataStoreKeys.LOGIN_DATA,
                                Gson().fromJson(response.body()!!.data, Login::class.java)
                            )
//                            Handler(Looper.getMainLooper()).postDelayed({
//                                Profiles.callBackListener!!.onCallBack(4)
//                            }, 100)
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






    data class Model(
        var vendor_first_name : String ?= null,
        var vendor_last_name : String ?= null,
        var parent_first_name : String ?= null,
        var parent_last_name : String ?= null,
        var gender : String ?= null,
        var date_of_birth : String ?= null,
        var social_category : String ?= null,
        var education_qualification : String ?= null,
        var marital_status : String ?= null,
        var spouse_name : String ?= null,
        var current_state : String ?= null,
        var current_district : String ?= null,
        var municipality_panchayat_current : String ?= null,
        var current_pincode : String ?= null,
        var current_address : String ?= null,

        var passportSizeImage : String ?= null,
        var identificationImage : String ?= null,



        var type_of_marketplace : String ?= null,
        var marketpalce_others : String ?= null,
        var type_of_vending : String ?= null,
        var vending_others : String ?= null,

        var total_years_of_business : String ?= null,

        var open : String ?= null,
        var close : String ?= null,

        var vending_state : String ?= null,
        var vending_district : String ?= null,
        var vending_municipality_panchayat : String ?= null,
        var vending_pincode : String ?= null,
        var vending_address : String ?= null,
        var localOrganisation : String ?= null,


        var shopImage : String ?= null,

        var documentDetails : Boolean ?= false,
        var ImageUploadCOV : String ?= null,
        var ImageUploadLOR : String ?= null,
        var UploadSurveyReceipt : String ?= null,
        var UploadChallan : String ?= null,
        var UploadApprovalLetter : String ?= null,

        var ImageUploadCOVBoolean : Boolean ?= false,
        var ImageUploadLORBoolean : Boolean ?= false,
        var UploadSurveyReceiptBoolean : Boolean ?= false,
        var UploadChallanBoolean : Boolean ?= false,
        var UploadApprovalLetterBoolean : Boolean ?= false,

        var vending_documents : String ?= "null",

        var governmentScheme : Boolean ?= false,
        var pmSwanidhiScheme : Boolean ?= false,
        var otherScheme : Boolean ?= false,
        var schemeName : String ?= null,

        var mobile_no : String ?= null,
        var otp : String ?= null,
        var password : String ?= null,


        var aadhar_card : String ?= null,
        var aadhar_card_doc : String ?= null,
        var address : String ?= null,
        var profile_image_name : String ?= null,
    )


    override fun onCleared() {
        super.onCleared()
//        itemState.clear()
//        itemDistrict.clear()
//        itemPanchayat.clear()
//        itemPincode.clear()
//        itemStateVending.clear()
//        itemDistrictVending.clear()
//        itemPanchayatVending.clear()
//        itemPincodeVending.clear()
//        itemLocalOrganizationVending.clear()
    }




    fun callApiTranslate(_lang : String, _words: String) : String{
        return repository.callApiTranslate(_lang, _words)
    }
}