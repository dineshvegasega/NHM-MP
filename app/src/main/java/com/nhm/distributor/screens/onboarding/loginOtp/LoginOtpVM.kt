package com.nhm.distributor.screens.onboarding.loginOtp

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhm.distributor.networking.ApiInterface
import com.nhm.distributor.networking.CallHandler
import com.nhm.distributor.networking.Repository
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.nhm.distributor.datastore.DataStoreKeys
import com.nhm.distributor.R
import com.nhm.distributor.datastore.DataStoreKeys.TOKEN
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.datastore.DataStoreUtil.saveData
import com.nhm.distributor.datastore.DataStoreUtil.saveObject
import com.nhm.distributor.models.BaseResponseDC
import com.nhm.distributor.models.Login
import com.nhm.distributor.networking.getJsonRequestBody
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.networking.*
import com.nhm.distributor.utils.getToken
import com.nhm.distributor.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginOtpVM @Inject constructor(private val repository: Repository): ViewModel() {

    var isSend = MutableLiveData<Boolean>(false)
    var isSendMutable = MutableLiveData<Boolean>(false)

    var isOtpVerified = false

    fun sendOTP(view: View, jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<Any>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.sendOTP(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<Any>>) {
                    if (response.isSuccessful){
                        if(response.body()?.message == "OTP Sent successfully"){
                            isSend.value = true
                            val number = jsonObject.getString("mobile_no")
                            showSnackBar(view.resources.getString(R.string.otp_sent, number))
                        } else if(response.body()?.message == "User already exist"){
                            isSend.value = false
                            showSnackBar(view.resources.getString(R.string.user_already_exist))
                        } else {
                            isSend.value = false
                            showSnackBar(view.resources.getString(R.string.user_does_not_exist))
                        }

                    } else{
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
                    if (response.isSuccessful){
                        if(response.body()?.data != null){
                            isOtpVerified = true
                            isSendMutable.value = true
                            showSnackBar(view.resources.getString(R.string.otp_Verified_successfully))
                        } else {
                            isOtpVerified = false
                            isSendMutable.value = false
                            showSnackBar(view.resources.getString(R.string.invalid_OTP))
                        }
                    } else{
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



    fun verifyOTPData(view: View, jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.verifyOTPData(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        if(response.body()?.data != null){
                            saveData(DataStoreKeys.AUTH, response.body()!!.token ?: "")
                            val data = Gson().fromJson(response.body()!!.data, Login::class.java)
                            saveObject(DataStoreKeys.LOGIN_DATA, data)
                            showSnackBar(view.resources.getString(R.string.otp_Verified_successfully))
                            readData(TOKEN) { token ->
                                getToken(){
                                    val obj: JSONObject = JSONObject()
                                    obj.put(user_id, ""+data.id)
                                    obj.put(mobile_token, ""+this)
                                    token(obj)
                                }
                            }
                            val last = if(data.language == null){
                                "en"
                            }else if(data.language.contains("/")){
                                data.language.substring(data.language.lastIndexOf('/') + 1).replace("'", "")
                            } else {
                                data.language
                            }

                            MainActivity.mainActivity.get()?.reloadActivity(last, Main)
                        } else {
                            showSnackBar(view.resources.getString(R.string.invalid_OTP))
                        }
                    } else {
                        showSnackBar(response.body()?.message.orEmpty())
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



    fun token(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApiWithoutLoader(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.mobileToken(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                    }
                }
                override fun error(message: String) {
//                    super.error(message)
                }
                override fun loading() {
//                    super.loading()
                }
            }
        )
    }

}