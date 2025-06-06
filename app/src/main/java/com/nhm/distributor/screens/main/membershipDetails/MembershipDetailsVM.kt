package com.nhm.distributor.screens.main.membershipDetails

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
import com.nhm.distributor.databinding.LoaderBinding
import com.nhm.distributor.models.BaseResponseDC
import com.nhm.distributor.models.ItemMarketplace
import com.nhm.distributor.models.ItemOrganization
import com.nhm.distributor.models.ItemVending
import com.nhm.distributor.networking.getJsonRequestBody
import com.nhm.distributor.screens.mainActivity.MainActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MembershipDetailsVM @Inject constructor(private val repository: Repository): ViewModel() {

    var fontSize : Float = 0f
    var scale10 : Float = 0f
    init {
        scale10 = MainActivity.scale10.toFloat()
        fontSize = MainActivity.fontSize
    }



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


    var itemVending : ArrayList<ItemVending> = ArrayList()
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



    var itemLocalOrganizationVending : ArrayList<ItemOrganization> = ArrayList()
    var localOrganizationVendingTrue = MutableLiveData<Boolean>(false)
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
//                                localOrganizationVendingTrue.value = true
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
//                                    localOrganizationVendingTrue.value = true
//                                    hide()
//                                }
//                            }
//                        } else {
                            itemLocalOrganizationVending =
                                response.body()?.data as ArrayList<ItemOrganization>
                            localOrganizationVendingTrue.value = true
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




    fun callApiTranslate(_lang : String, _words: String) : String{
        return repository.callApiTranslate(_lang, _words)
    }
}