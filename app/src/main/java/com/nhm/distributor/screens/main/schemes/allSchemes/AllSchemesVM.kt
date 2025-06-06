package com.nhm.distributor.screens.main.schemes.allSchemes

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhm.distributor.networking.ApiInterface
import com.nhm.distributor.networking.CallHandler
import com.nhm.distributor.networking.Repository
import com.google.gson.JsonElement
import com.nhm.distributor.databinding.LoaderBinding
import com.nhm.distributor.models.BaseResponseDC
import com.nhm.distributor.networking.*
import com.nhm.distributor.networking.getJsonRequestBody
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AllSchemesVM @Inject constructor(private val repository: Repository): ViewModel() {


    val adapter by lazy { AllSchemesAdapter(this) }


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



    private var itemLiveSchemesResult = MutableLiveData<BaseResponseDC<Any>>()
    val itemLiveSchemes : LiveData<BaseResponseDC<Any>> get() = itemLiveSchemesResult
    fun liveScheme(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.allSchemeList(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        itemLiveSchemesResult.value = response.body() as BaseResponseDC<Any>
                    }
                }

                override fun error(message: String) {
                    super.error(message)
//                    showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }



    private var itemLiveSchemesResultSecond = MutableLiveData<BaseResponseDC<Any>>()
    val itemLiveSchemesSecond : LiveData<BaseResponseDC<Any>> get() = itemLiveSchemesResultSecond
    fun liveSchemeSecond(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.allSchemeList(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        itemLiveSchemesResultSecond.value =  response.body() as BaseResponseDC<Any>
                    }
                }

                override fun error(message: String) {
                    super.error(message)
//                    showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }




    var applyLink = MutableLiveData<Int>(-1)
    fun applyLink(jsonObject: JSONObject, position: Int) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.applyLink(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        applyLink.value =  position
                    } else {
                        applyLink.value =  -1
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


//    fun viewDetail(oldItemLiveScheme: ItemLiveScheme, position: Int, root: View, status : Int) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.schemeDetail(id = ""+oldItemLiveScheme.scheme_id)
//                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
//                    if (response.isSuccessful){
//                        val data = Gson().fromJson(response.body()!!.data, ItemSchemeDetail::class.java)
//
//                        when(status){
//                            in 1..2 -> {
//                                val dialogBinding = DialogBottomLiveSchemeBinding.inflate(root.context.getSystemService(
//                                    Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//                                )
//                                val dialog = BottomSheetDialog(root.context)
//                                dialog.setContentView(dialogBinding.root)
//                                dialog.setOnShowListener { dia ->
//                                    val bottomSheetDialog = dia as BottomSheetDialog
//                                    val bottomSheetInternal: FrameLayout =
//                                        bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
//                                    bottomSheetInternal.setBackgroundResource(R.drawable.bg_top_round_corner)
//                                }
//                                dialog.show()
//
//                                dialogBinding.apply {
//                                    data.scheme_image?.url?.glideImage(root.context, ivMap)
//                                    textTitle.setText(oldItemLiveScheme.name)
//                                    textDesc.setText(oldItemLiveScheme.description)
//
//                                    if (data.status == "Active" && oldItemLiveScheme.user_scheme_status == "applied"){
//                                        btApply.visibility = View.GONE
//                                        textHeaderTxt4.text = root.context.resources.getText(R.string.applied)
//                                        textHeaderTxt4.backgroundTintList = ContextCompat.getColorStateList(root.context,R.color._138808)
//                                    } else if (data.status == "Active"){
//                                        btApply.visibility = View.VISIBLE
//                                        textHeaderTxt4.text = root.context.resources.getText(R.string.active)
//                                        textHeaderTxt4.backgroundTintList = ContextCompat.getColorStateList(root.context,R.color._138808)
//                                    }  else {
//                                        btApply.visibility = View.GONE
//                                        textHeaderTxt4.text = root.context.resources.getText(R.string.expired)
//                                        textHeaderTxt4.backgroundTintList = ContextCompat.getColorStateList(root.context,R.color._F02A2A)
//                                    }
//
//                                    data.start_at?.let {
//                                        textStartDate.text = HtmlCompat.fromHtml("${root.context.resources.getString(R.string.start_date, "<b>"+data.start_at.changeDateFormat("yyyy-MM-dd", "dd MMM, yyyy")+"</b>")}", HtmlCompat.FROM_HTML_MODE_LEGACY)
//                                    }
//
//                                    data.end_at?.let {
//                                        textEndDate.text = HtmlCompat.fromHtml("${root.context.resources.getString(R.string.end_date, "<b>"+data.end_at.changeDateFormat("yyyy-MM-dd", "dd MMM, yyyy")+"</b>")}", HtmlCompat.FROM_HTML_MODE_LEGACY)
//                                    }
//
//
////                                    if (status == 1){
////                                        btApply.setText(view.resources.getString(R.string.view))
////                                        btApply.visibility = View.GONE
////                                    }else{
////                                        btApply.setText(view.resources.getString(R.string.apply))
////                                        btApply.visibility = View.VISIBLE
////                                    }
//
//                                    btApply.singleClick {
//                                        if (status == 1){
//                                            Handler(Looper.getMainLooper()).post(Thread {
//                                                MainActivity.activity.get()?.runOnUiThread {
//                                                    data.apply_link?.let {
//                                                        val webIntent = Intent(
//                                                            Intent.ACTION_VIEW,
//                                                            Uri.parse(data.apply_link)
//                                                        )
//                                                        try {
//                                                            root.context.startActivity(webIntent)
//                                                        } catch (ex: ActivityNotFoundException) {
//                                                        }
//                                                    }
//                                                }
//                                            })
//                                        } else {
//                                            readData(LOGIN_DATA) { loginUser ->
//                                                if (loginUser != null) {
//                                                    val obj: JSONObject = JSONObject().apply {
//                                                        put(scheme_id, data?.scheme_id)
//                                                        put(user_type, USER_TYPE)
//                                                        put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
//                                                    }
//                                                    if(networkFailed) {
//                                                        applyLink(obj, position)
//                                                    } else {
//                                                        root.context.callNetworkDialog()
//                                                    }
//                                                }
//                                            }
//                                        }
//                                        dialog.dismiss()
//                                    }
//
//                                    btClose.singleClick {
//                                        dialog.dismiss()
//                                    }
//                                }
//                            } else -> {
//                            Handler(Looper.getMainLooper()).post(Thread {
//                                MainActivity.activity.get()?.runOnUiThread {
//                                    data.apply_link?.let {
//                                        val webIntent = Intent(
//                                            Intent.ACTION_VIEW, Uri.parse(data.apply_link))
//                                        try {
//                                            root.context.startActivity(webIntent)
//                                        } catch (ex: ActivityNotFoundException) {
//                                        }
//                                    }
//                                }
//                            })
//                        }
//                        }
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



    fun callApiTranslate(_lang : String, _words: String) : String{
        return repository.callApiTranslate(_lang, _words)
    }
}