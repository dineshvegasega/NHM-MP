package com.nhm.distributor.screens.main.complaintsFeedback.createNew

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.nhm.distributor.networking.ApiInterface
import com.nhm.distributor.networking.CallHandler
import com.nhm.distributor.networking.Repository
import com.nhm.distributor.R
import com.nhm.distributor.databinding.LoaderBinding
import com.nhm.distributor.models.BaseResponseDC
import com.nhm.distributor.models.ItemComplaintType
import com.nhm.distributor.networking.IS_LANGUAGE
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.locale
import com.nhm.distributor.utils.mainThread
import com.nhm.distributor.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CreateNewVM @Inject constructor(private val repository: Repository): ViewModel() {

    var type : String = "complaint"


    var uploadMediaImage : String ?= null


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


    var itemComplaintType : ArrayList<ItemComplaintType> = ArrayList()
    var complaintTypeId : Int = 0
    fun complaintType(view: View) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemComplaintType>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.complaintType()

                override fun success(response: Response<BaseResponseDC<List<ItemComplaintType>>>) {
                    if (response.isSuccessful){
                        if (IS_LANGUAGE){
                            if (MainActivity.context.get()!!
                                    .getString(R.string.englishVal) == "" + locale
                            ) {
                                itemComplaintType = response.body()?.data as ArrayList<ItemComplaintType>
                            } else {
                                val itemStateTemp = response.body()?.data as ArrayList<ItemComplaintType>
                                show()
                                mainThread {
                                    itemStateTemp.forEach {
                                        delay(50)
                                        val nameChanged: String = callApiTranslate(""+locale, it.name)
                                        apply {
                                            it.name = nameChanged
                                        }
                                    }
                                    itemComplaintType = itemStateTemp
                                    hide()
                                }
                            }
                        } else {
                            itemComplaintType = response.body()?.data as ArrayList<ItemComplaintType>
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





    fun newFeedback(
        view: View,
        hashMap: RequestBody
    ) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<Any>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.newFeedback( hashMap)

                override fun success(response: Response<BaseResponseDC<Any>>) {
                    if (response.isSuccessful){
                        if (type == "complaint"){
                            showSnackBar(view.resources.getString(R.string.complaint_added_successfully))
                        } else if (type == "feedback"){
                            showSnackBar(view.resources.getString(R.string.feedback_added_successfully))
                        }
                        view.findNavController().navigate(R.id.action_createNew_to_history)
                    } else{
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


    fun callApiTranslate(_lang : String, _words: String) : String{
        return repository.callApiTranslate(_lang, _words)
    }
}