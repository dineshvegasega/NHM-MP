package com.nhm.distributor.screens.onboarding.loginPassword


import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhm.distributor.networking.ApiInterface
import com.nhm.distributor.networking.CallHandler
import com.nhm.distributor.networking.Repository
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.nhm.distributor.R
import com.nhm.distributor.datastore.DataStoreKeys.AUTH
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
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
class LoginPasswordVM @Inject constructor(private val repository: Repository
): ViewModel() {

    fun login(view: View, jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.login(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        if(response.body()!!.data != null){
                            val _id = Gson().fromJson(response.body()!!.data, Login::class.java).id
                            getToken() {
                                val obj: JSONObject = JSONObject()
                                obj.put(user_id, ""+_id)
                                obj.put(mobile_token, ""+this)
                                token(obj)
                            }
                            profile(view, ""+_id)
                            showSnackBar(view.resources.getString(R.string.logged_in_successfully))
                        }else if(response.body()!!.message == "User does not exist"){
                            showSnackBar(view.resources.getString(R.string.user_does_not_exist))
                        } else {
                            showSnackBar(view.resources.getString(R.string.please_provide_valid_password))
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



    fun profile(view: View, _id: String) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.profile(_id)

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        if(response.body()!!.data != null){
                            saveData(AUTH, response.body()!!.token ?: "")
                            val data = Gson().fromJson(response.body()!!.data, Login::class.java)
                            saveObject(LOGIN_DATA, data)
                            val last = if(data.language == null){
                                "en"
                            }else if(data.language.contains("/")){
                                data.language.substring(data.language.lastIndexOf('/') + 1).replace("'", "")
                            } else {
                                data.language
                            }
                            MainActivity.mainActivity.get()?.reloadActivity(last, Main)
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

}