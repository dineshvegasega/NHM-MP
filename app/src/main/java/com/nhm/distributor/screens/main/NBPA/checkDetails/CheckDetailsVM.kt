package com.nhm.distributor.screens.main.NBPA.checkDetails

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhm.distributor.models.ItemNBPAFormRoot
import com.nhm.distributor.networking.ApiInterface
import com.nhm.distributor.networking.CallHandler
import com.nhm.distributor.networking.Repository
import com.nhm.distributor.networking.getJsonRequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CheckDetailsVM @Inject constructor(private val repository: Repository): ViewModel() {


//    fun checkAadhaarNo(
//        view: View,
//        _id: String,  callBack: String.() -> Unit
//    ) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<ItemFormListDetail>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.checkAadhaarNo(_id)
//
//                override fun success(response: Response<ItemFormListDetail>) {
//                    if (response.isSuccessful) {
////                        isProductLoad = true
////                        isProductLoadMember = true
////                        showSnackBar(view.resources.getString(R.string.forms_added_successfully))
//                        callBack("response.body()!!")
//
////                        view.findNavController()
////                            .navigate(R.id.action_nbpa_to_nbpaList)
//                    } else {
//                        showSnackBar(response.body()?.message.orEmpty())
//                    }
//                }
//
//                override fun error(message: String) {
//                    super.error(message)
//                    showSnackBar(message)
//                }
//
//                override fun loading() {
//                    super.loading()
//                }
//            }
//        )
//    }






//    private var itemProducstResult = MutableLiveData<ItemNBPAFormRoot>()
//    val itemProducts: LiveData<ItemNBPAFormRoot> get() = itemProducstResult
    fun checkAadhaarNo(emptyMap: JSONObject, callBack: ItemNBPAFormRoot.() -> Unit) =
        viewModelScope.launch {
                repository.callApi(
                    callHandler = object : CallHandler<Response<ItemNBPAFormRoot>> {
                        override suspend fun sendRequest(apiInterface: ApiInterface) =
                            apiInterface.checkAadhaarNo(emptyMap.getJsonRequestBody())

                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<ItemNBPAFormRoot>) {
                            if (response.isSuccessful) {
                                callBack(response.body()!!)
                            }
                        }

                        override fun error(message: String) {

                        }

                        override fun loading() {
                            super.loading()
                        }
                    }
                )
        }
}