package com.nhm.distributor.screens.main.profiles.nomineeDetails

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.nhm.distributor.networking.ApiInterface
import com.nhm.distributor.networking.CallHandler
import com.nhm.distributor.R
import com.nhm.distributor.networking.Repository
import com.nhm.distributor.models.BaseResponseDC
import com.nhm.distributor.models.ItemNomineeData
import com.nhm.distributor.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class NomineeDetailsVM @Inject constructor(private val repository: Repository): ViewModel() {
    var isEditable = MutableLiveData<Boolean>(false)

    var relationType1 = ""
    var relationName1 = ""

    var relationType2 = ""
    var relationName2 = ""

    var relationType3 = ""
    var relationName3 = ""

    var relationType4 = ""
    var relationName4 = ""

    var relationType5 = ""
    var relationName5 = ""





    var updateNominee = MutableLiveData<Boolean>(false)
    fun  updateNomineeDetails(view: View, requestBody: RequestBody) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.updateNomineeDetails(requestBody)

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        updateNominee.value = true
                        showSnackBar(view.resources.getString(R.string.nominee_added_updated_successfully))
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




    var nomineeMutableLiveData = MutableLiveData<Boolean>(false)
    val nomineeArrayList = ArrayList<Pair<String, String>>()
    fun nomineeDetails(view: View, requestBody: RequestBody) = viewModelScope.launch {
        nomineeArrayList.clear()
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.nomineeDetails(requestBody = requestBody)
                @SuppressLint("SuspiciousIndentation")
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        val typeToken = object : TypeToken<ItemNomineeData>() {}.type
                        if(Gson().toJson(response.body()!!.data) != "[]"){
                            val changeValue = Gson().fromJson<ItemNomineeData>(Gson().toJson(response.body()!!.data), typeToken)
                                changeValue?.nominee?.forEach {
                                val product: HashMap<String, String> ?= it
                                var count = 0
                                product?.values?.forEach {
                                    nomineeArrayList.add(Pair(""+product?.keys?.elementAt(count), ""+it))
                                    count ++
                                }
                                    nomineeMutableLiveData.value = true
                            }
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


    override fun onCleared() {
        super.onCleared()
        relationType1 = ""
        relationName1 = ""

        relationType2 = ""
        relationName2 = ""

        relationType3 = ""
        relationName3 = ""

        relationType4 = ""
        relationName4 = ""

        relationType5 = ""
        relationName5 = ""
    }


}







