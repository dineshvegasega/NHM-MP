package com.nhm.distributor.screens.main.schemes.liveSchemes

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhm.distributor.networking.ApiInterface
import com.nhm.distributor.networking.CallHandler
import com.nhm.distributor.networking.Repository
import com.google.gson.JsonElement
import com.nhm.distributor.databinding.ItemMenuBinding
import com.nhm.distributor.databinding.LoaderBinding
import com.nhm.distributor.genericAdapter.GenericAdapter
import com.nhm.distributor.models.BaseResponseDC
import com.nhm.distributor.models.ItemLiveScheme
import com.nhm.distributor.networking.*
import com.nhm.distributor.networking.getJsonRequestBody
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.isBackApp
import com.nhm.distributor.utils.parseResult
import com.nhm.distributor.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class LiveSchemesVM @Inject constructor(private val repository: Repository): ViewModel() {

    val adapter by lazy { LiveSchemesAdapter(this) }


    val menuAdapter = object : GenericAdapter<ItemMenuBinding, ItemLiveScheme>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemMenuBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
        override fun onBindHolder(binding: ItemMenuBinding, dataClass: ItemLiveScheme, position: Int) {

//            binding.apply {
//                if(selectedPosition == position) {
//                    ivArrow.setImageResource(if (dataClass.isExpanded == true) R.drawable.ic_arrow_down else R.drawable.ic_arrow_up)
//                    recyclerViewChild.visibility = if (dataClass.isExpanded == true) View.VISIBLE else View.GONE
//                } else {
//                    ivArrow.setImageResource(R.drawable.ic_arrow_up)
//                    recyclerViewChild.visibility = View.GONE
//                    dataClass.apply {
//                        isExpanded = false
//                    }
//                }
//
//
//                if(selectedColorPosition == position) {
//                    header.setBackgroundTintList(
//                        ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color._EDB678)))
//                } else {
//                    header.setBackgroundTintList(
//                        ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.white)))
//                }
//
//
//                title.text = dataClass.title
//                if(dataClass.titleChildArray!!.isEmpty()){
//                    ivArrow .visibility = View.GONE
//                }else{
//                    ivArrow .visibility = View.VISIBLE
//                }
//
//
//                recyclerViewChild.setHasFixedSize(true)
//                val headlineAdapter = MainActivityVM.ChildMenuAdapter(
//                    binding.root.context,
//                    dataClass.titleChildArray,
//                    position
//                )
//                recyclerViewChild.adapter = headlineAdapter
//                recyclerViewChild.layoutManager = LinearLayoutManager(binding.root.context)
//
//
////                ivArrow.singleClick {
////                    selectedPosition = position
////                    dataClass.isExpanded = !dataClass.isExpanded!!
////                    selectedColorPosition = position
////                    notifyDataSetChanged()
////                }
//
//
//                root.singleClick {
//                    selectedColorPosition = position
//                    if(dataClass.titleChildArray!!.isEmpty()){
//                        var fragmentInFrame = MainActivity.navHostFragment!!.getChildFragmentManager().getFragments().get(0)
//                        readData(DataStoreKeys.LOGIN_DATA) { loginUser ->
//                            if (loginUser != null) {
//                                val data = Gson().fromJson(loginUser, Login::class.java)
//                                when (data.status) {
//                                    "approved" -> {
//                                        when(position) {
//                                            0 -> {
//                                                if (fragmentInFrame !is Dashboard){
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.dashboard)
//                                                }
//                                            }
//                                            1 -> {
//                                                if (fragmentInFrame !is Profiles){
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.profiles)
//                                                }
//                                            }
//                                            2 -> {
//                                                readData(DataStoreKeys.LOGIN_DATA) { loginUser ->
//                                                    if (loginUser != null) {
//                                                        val isNotification = Gson().fromJson(
//                                                            loginUser,
//                                                            Login::class.java
//                                                        )?.notification ?: ""
////                                                        Log.e("TAG", "isNotification"+isNotification)
//                                                        if(isNotification == "Yes"){
//                                                            if (fragmentInFrame !is Notifications){
//                                                                NotificationsVM.isNotificationNext = false
//                                                                MainActivity.navHostFragment?.navController?.navigate(R.id.notifications)
//                                                            }
//                                                        } else {
//                                                            showSnackBar(root.resources.getString(R.string.notification_not_enabled))
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                            3 -> {
//                                                val densityDpi = root.context.getDensityName()
//                                                when (densityDpi) {
//                                                    "xxhdpi" -> {
//                                                        if (fragmentInFrame !is MembershipDetailsXX){
//                                                            MainActivity.navHostFragment?.navController?.navigate(R.id.membershipDetailsXX)
//                                                        }
//                                                    } else -> {
//                                                    if (fragmentInFrame !is MembershipDetails){
//                                                        MainActivity.navHostFragment?.navController?.navigate(R.id.membershipDetails)
//                                                    }
//                                                }
//                                                }
//                                            }
//                                            8 -> {
//                                                if (fragmentInFrame !is InformationCenter){
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.informationCenter)
//                                                }
//                                            }
//                                            9 -> {
//                                                if (fragmentInFrame !is Subscription){
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.subscription)
//                                                }
//                                            }
//                                            10 -> {
//                                                if (fragmentInFrame !is Settings){
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.settings)
//                                                }
//                                            }
//                                        }
//                                    }
//                                    "unverified" -> {
//                                        when(position) {
//                                            0 -> {
//                                                if (fragmentInFrame !is Dashboard){
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.dashboard)
//                                                }
//                                            }
//                                            1 -> {
//                                                if (fragmentInFrame !is Profiles) {
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.profiles)
//                                                }
//                                            }
//                                            10 -> {
//                                                if (fragmentInFrame !is Subscription) {
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.subscription)
//                                                }
//                                            }
//                                            else -> {
//                                                showSnackBar(root.resources.getString(R.string.registration_processed))
//                                            }
//                                        }
//                                    }
//                                    "pending" -> {
//                                        when(position) {
//                                            0 -> {
//                                                if (fragmentInFrame !is Dashboard){
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.dashboard)
//                                                }
//                                            }
//                                            1 -> {
//                                                if (fragmentInFrame !is Profiles) {
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.profiles)
//                                                }
//                                            }
//                                            10 -> {
//                                                if (fragmentInFrame !is Subscription) {
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.subscription)
//                                                }
//                                            }
//                                            else -> {
//                                                showSnackBar(root.resources.getString(R.string.registration_processed))
//                                            }
//                                        }
//                                    }
//                                    "rejected" -> {
//                                        when(position) {
//                                            0 -> {
//                                                if (fragmentInFrame !is Dashboard){
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.dashboard)
//                                                }
//                                            }
//                                            1 -> {
//                                                if (fragmentInFrame !is Profiles) {
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.profiles)
//                                                }
//                                            }
//                                            10 -> {
//                                                if (fragmentInFrame !is Subscription) {
//                                                    MainActivity.navHostFragment?.navController?.navigate(R.id.subscription)
//                                                }
//                                            }
//                                            else -> {
//                                                showSnackBar(root.resources.getString(R.string.registration_processed))
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        MainActivity.binding.drawerLayout.close()
//                    }
//
//                    selectedPosition = position
//                    dataClass.isExpanded = !dataClass.isExpanded!!
//                    selectedColorPosition = position
//                    notifyDataSetChanged()
//                }
//
//
//            }
        }
    }





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
                    apiInterface.liveScheme(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        itemLiveSchemesResult.value = response.body() as BaseResponseDC<Any>
                    }
                    isBackApp = false
                }

                override fun error(message: String) {
                    super.error(message)
//                    showSnackBar(message)
                    isBackApp = false
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
                    apiInterface.liveScheme(requestBody = jsonObject.getJsonRequestBody())
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        itemLiveSchemesResultSecond.value =  response.body() as BaseResponseDC<Any>
                    }
                    isBackApp = false
                }

                override fun error(message: String) {
                    super.error(message)
//                    showSnackBar(message)
                    isBackApp = false
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
//                @SuppressLint("ResourceAsColor")
//                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
//                    if (response.isSuccessful){
//                        var data = Gson().fromJson(response.body()!!.data, ItemSchemeDetail::class.java)
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
//                                        textHeaderTxt4.text = root.context.resources.getText(R.string.applied)
//                                        textHeaderTxt4.backgroundTintList = ContextCompat.getColorStateList(root.context,R.color._138808)
//                                    } else if (data.status == "Active"){
//                                        textHeaderTxt4.text = root.context.resources.getText(R.string.live)
//                                        textHeaderTxt4.backgroundTintList = ContextCompat.getColorStateList(root.context,R.color._138808)
//                                    }  else {
//                                        textHeaderTxt4.text = root.context.resources.getText(R.string.not_live)
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
//                                    if (status == 1){
//                                        btApply.setText(view.resources.getString(R.string.view))
//                                        btApply.visibility = View.GONE
//                                    }else{
//                                        btApply.setText(view.resources.getString(R.string.apply))
//                                        if (data.status == "Active"){
//                                            btApply.visibility = View.VISIBLE
//                                        } else {
//                                            btApply.visibility = View.GONE
//                                        }
//                                    }
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
//                                                        dialogBinding.view.context.callNetworkDialog()
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
//                                Handler(Looper.getMainLooper()).post(Thread {
//                                       MainActivity.activity.get()?.runOnUiThread {
//                                           data.apply_link?.let {
//                                               val webIntent = Intent(
//                                                   Intent.ACTION_VIEW,Uri.parse(data.apply_link))
//                                               try {
//                                                   root.context.startActivity(webIntent)
//                                               } catch (ex: ActivityNotFoundException) {
//                                               }
//                                           }
//                                       }
//                                  })
//                            }
//                        }
//                    } else {
//
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





    @Throws(Exception::class)
    suspend fun callUrlAndParseResult(
        langTo: String,
        word: String
    ): String {
        val url = "https://translate.googleapis.com/translate_a/single?" +
                "client=gtx&" +
                "sl=" + "en" +
                "&tl=" + langTo +
                "&dt=t&q=" + URLEncoder.encode(word, "UTF-8")
        val obj = URL(url)
        val con = obj.openConnection() as HttpURLConnection
        con.setRequestProperty("User-Agent", "Mozilla/5.0")
        val `in` = BufferedReader(
            InputStreamReader(con.inputStream)
        )
        var inputLine: String?
        val response = StringBuffer()
        while (`in`.readLine().also { inputLine = it } != null) {
            response.append(inputLine)
        }
        `in`.close()
        return response.toString().parseResult()
    }



    fun callApiTranslate(_lang : String, _words: String) : String{
        return repository.callApiTranslate(_lang, _words)
    }
}



