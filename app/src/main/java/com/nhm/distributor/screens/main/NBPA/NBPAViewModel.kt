package com.nhm.distributor.screens.main.NBPA

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ItemForm3Binding
import com.nhm.distributor.databinding.ItemForm4Binding
import com.nhm.distributor.databinding.ItemForm5Binding
import com.nhm.distributor.databinding.LoaderBinding
import com.nhm.distributor.genericAdapter.GenericAdapter
import com.nhm.distributor.models.BaseResponseDC
import com.nhm.distributor.models.ItemLiveScheme
import com.nhm.distributor.models.ItemNBPAForm
import com.nhm.distributor.models.ItemNBPAFormRoot
import com.nhm.distributor.models.ItemFormListDetail
import com.nhm.distributor.models.ItemMemberRoot
import com.nhm.distributor.models.SchemeDetail
import com.nhm.distributor.networking.ApiInterface
import com.nhm.distributor.networking.CallHandler
import com.nhm.distributor.networking.Repository
import com.nhm.distributor.networking.getJsonRequestBody
//import com.nhm.distributor.screens.main.NBPA.NBPADetail.Companion.change
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.utils.loadImage
import com.nhm.distributor.utils.mainThread
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NBPAViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    var isAadhaar = ""
    var start = ""
    var scheme_id = ""
//    var editData : ItemNBPAForm ?= null
    var editDataNew : ItemFormListDetail ?= null

    var results: MutableList<ItemLiveScheme> = ArrayList()

    var name = ""
    var fatherHusbandType = 1 // 1 = Father, 2 = Husband
    var fatherHusband = ""
    var mother = ""
    var gender = ""
    var age = ""
    var height = ""
    var weight = ""
    var numberOfMembers = ""
    var numberOfChildren = ""
    var address = ""
    var dmcName = ""
    var block = ""
    var mobileNumber = ""
    var districtState = ""
    var cardTypeAPLBPL = 1 // 1 = APL, 2 = BPL

    var typeOfPatient = 1 // 1 = Pulmonary, 2 = Extra Pulmonary, 3 = Other
    var patientCheckupDate = ""
    var hemoglobinLevelAge = ""
    var hemoglobinCheckupDate = ""
    var muktiID = ""
    var nakshayID = ""
    var aadhaarNumber = ""
    var business = ""
    var bankAccount = ""
    var bankIFSC = ""
    var treatmentSupporterName = ""
    var treatmentSupporterPost = ""
    var treatmentSupporterMobileNumber = ""
    var treatmentSupporterEndDate = ""
    var treatmentSupporterResult = ""

    var foodMonth = ""
    var foodDate = ""
    var foodHeight = ""
    var foodSignatureImage = ""
    var foodItemImage = ""
    var foodIdentityImage = ""


    var dietChartDate = ""
    var dietChartEvaluation = ""
    var dietChartSuggestion = ""
    var dietChartServiceProvider = ""
    var homeVisitDate = ""
    var homeVisitWeight = ""
    var homeVisitSignature = ""
    var homeVisitRemark = ""

    var assistanceDBTDate = ""
    var assistanceDBTTotalAmount = ""
    var assistanceDBTDetails = ""
    var assistanceDBTRemark = ""
    var assistanceExtraGroceryPDS = ""
    var assistanceExtraGroceryPDSDetails = ""
    var assistanceExtraGroceryPDSRemark = ""
    var assistanceMultiVitaminDate = ""
    var assistanceMultiVitaminTotalNumber = ""
    var assistanceMultiVitaminDetails = ""
    var assistanceMultiVitaminRemark = ""
    var assistanceOtherHelp = ""
    var assistanceHelpDetails = ""
    var assistanceHelpRemark = ""
    var assistanceProjectCoordinatorSignature = ""
    var assistanceProjectManagerSignature = ""


    internal var isButtonEnable = MutableLiveData<Boolean>(true)
    fun registerWithFiles(
        view: View,
        hashMap: RequestBody,  callBack: BaseResponseDC<Any>.() -> Unit
    ) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<Any>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.createFormWithFiles(hashMap)

                override fun success(response: Response<BaseResponseDC<Any>>) {
                    if (response.isSuccessful) {
//                        isProductLoad = true
//                        isProductLoadMember = true
//                        showSnackBar(view.resources.getString(R.string.forms_added_successfully))
                        callBack(response.body()!!)

//                        view.findNavController()
//                            .navigate(R.id.action_nbpa_to_nbpaList)
                    } else {
                        showSnackBar(response.body()?.message.orEmpty())
                    }

                    isButtonEnable.value = true
                }

                override fun error(message: String) {
                    super.error(message)
                    var error = JSONObject(message).getString("errors")
                    if(error == "The nakshay i d has already been taken."){
                        showSnackBar(view.resources.getString(R.string.nakshay_id_taken))
                    } else {
                        showSnackBar(error)
                    }
                    isButtonEnable.value = true
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }




    fun registerWithFilesFoodItems(
        view: View,
        hashMap: RequestBody,  callBack: BaseResponseDC<Any>.() -> Unit
    ) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<Any>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.registerWithFilesFoodItems(hashMap)

                override fun success(response: Response<BaseResponseDC<Any>>) {
                    if (response.isSuccessful) {
//                        isProductLoad = true
//                        isProductLoadMember = true
//                        showSnackBar(view.resources.getString(R.string.forms_added_successfully))
                        callBack(response.body()!!)

//                        view.findNavController()
//                            .navigate(R.id.action_nbpa_to_nbpaList)
                    } else {
                        showSnackBar(response.body()?.message.orEmpty())
                    }

                    isButtonEnable.value = true
                }

                override fun error(message: String) {
                    super.error(message)
                    showSnackBar(view.resources.getString(R.string.something_went_wrong))
                    isButtonEnable.value = true
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }



    fun formListDetail(
        view: View,
        _id: String,  callBack: ItemFormListDetail.() -> Unit
    ) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemFormListDetail>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.formListDetail(_id)

                override fun success(response: Response<ItemFormListDetail>) {
                    if (response.isSuccessful) {
//                        isProductLoad = true
//                        isProductLoadMember = true
//                        showSnackBar(view.resources.getString(R.string.forms_added_successfully))
                        callBack(response.body()!!)

//                        view.findNavController()
//                            .navigate(R.id.action_nbpa_to_nbpaList)
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

    val itemsCustomerOrders: ArrayList<ItemLiveScheme> = ArrayList()

//    val adapter by lazy { NBPAListAdapter(this) }


//    val customerOrders =
//        object : GenericAdapter<ItemAllSchemesBinding, ItemLiveScheme>() {
//            override fun onCreateView(
//                inflater: LayoutInflater,
//                parent: ViewGroup,
//                viewType: Int
//            ) = ItemAllSchemesBinding.inflate(inflater, parent, false)
//
//            override fun onBindHolder(
//                binding: ItemAllSchemesBinding,
//                dataClass: ItemLiveScheme,
//                position: Int
//            ) {
//                binding.apply {
//
//
//
////                    val date =
////                        dataClass.updatedtime.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd-MMM-yyyy")
////                    btDate.text = date
////
//                    textTitle.text = dataClass.name
//                    textDesc.text = dataClass.address
//                    dataClass.foodIdentityImage?.url?.glideImagePortrait(root.context, ivIcon)
////                    textMobile.text = dataClass.customerMobile
////                    when (dataClass.status) {
////                        "pending" -> {
////                            btStatus.text = "Pending"
////                            btStatus.backgroundTintList =
////                                ColorStateList.valueOf(
////                                    ContextCompat.getColor(
////                                        binding.root.context,
////                                        R.color._E87103
////                                    )
////                                )
////                            btStatus.visibility = View.VISIBLE
////                        }
////                        "inprogress" -> {
////                            btStatus.text = "In Progress"
////                            btStatus.backgroundTintList =
////                                ColorStateList.valueOf(
////                                    ContextCompat.getColor(
////                                        binding.root.context,
////                                        R.color._F7879A
////                                    )
////                                )
////                            btStatus.visibility = View.VISIBLE
////                        }
////                        "dispatched" -> {
////                            btStatus.text = "Dispatched"
////                            btStatus.backgroundTintList =
////                                ColorStateList.valueOf(
////                                    ContextCompat.getColor(
////                                        binding.root.context,
////                                        R.color._2eb82e
////                                    )
////                                )
////                            btStatus.visibility = View.VISIBLE
////                        }
////                        "complete" -> {
////                            btStatus.text = "Complete"
////                            btStatus.backgroundTintList =
////                                ColorStateList.valueOf(
////                                    ContextCompat.getColor(
////                                        binding.root.context,
////                                        R.color._2A3740
////                                    )
////                                )
////                            btStatus.visibility = View.VISIBLE
////                        }
////                        else -> {
////                            btStatus.visibility = View.GONE
////                        }
//                }
//
//                binding.apply {
//                    root.singleClick {
//                        root.findNavController()
//                            .navigate(R.id.action_members_to_nbpaDetail, Bundle().apply {
//                                putParcelable("key", dataClass)
//                            })
//                    }
//                }
//
//            }
//        }


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


//    private var itemLiveSchemesResult = MutableLiveData<List<ItemLiveScheme>>()
//    val itemLiveSchemes : LiveData<List<ItemLiveScheme>> get() = itemLiveSchemesResult
//
////    internal var itemLiveSchemesResult = MutableLiveData<Boolean>(false)
//
//    fun liveScheme(jsonObject: JSONObject) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.schemeHistoryList(requestBody = jsonObject.getJsonRequestBody())
//
//                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
//                    if (response.isSuccessful) {
//                        val typeToken = object : TypeToken<List<ItemLiveScheme>>() {}.type
//                        val changeValue =
//                            Gson().fromJson<List<ItemLiveScheme>>(
//                                Gson().toJson(response.body()!!.data),
//                                typeToken
//                            )
//                        itemLiveSchemesResult.value = changeValue
//
////                        mainThread {
////                            if (itemsCustomerOrders.size == 0) {
////                                changeValue.forEach { mapData ->
////                                    Log.e("TAG", "schemeDataInnerFalseA: " + mapData.id)
////                                    itemsCustomerOrders.add(mapData)
////                                }
////                            } else {
////
////
////
////                                changeValue.forEach { mapData ->
////                                    var aaa = getData(mapData)
////                                    Log.e( "TAG",  "aaa: " + mapData.id + " :::: "+ aaa)
////                                    if (aaa == true) {
////                                        itemsCustomerOrders.add(mapData)
////                                    }
////
//////                                    getData
////
//////                                    itemsCustomerOrders.forEach { schemeDataInner ->
//////                                        if (schemeDataInner.id != mapData.id) {
//////                                            Log.e( "TAG",  "schemeDataInnerFalseB: " + mapData.id + " :::: "+ schemeDataInner.id)
//////                                            itemsCustomerOrders.add(mapData)
//////                                        }
//////                                    }
////
//////                                    itemsCustomerOrders.forEach { schemeDataInner ->
//////                                        if (schemeDataInner.id != mapData.id) {
//////                                            Log.e(
//////                                                "TAG",
//////                                                "schemeDataInnerFalse: " + schemeDataInner.id
//////                                            )
//////                                            itemsCustomerOrders.add(schemeDataInner)
//////                                        }
//////
//////                                        if (schemeDataInner.id == mapData.id) {
//////                                            Log.e(
//////                                                "TAG",
//////                                                "schemeDataInnerTrue: " + schemeDataInner.id
//////                                            )
//////                                        }
//////                                    }
////                                }
////                            }
//////                                itemsCustomerOrders.map { mapData ->
//////                                    changeValue.map { schemeDataInner ->
//////                                        if (schemeDataInner.id != mapData.id) {
//////                                            Log.e("TAG", "schemeDataInner: " + schemeDataInner.id)
//////                                            itemsCustomerOrders.add(schemeDataInner)
//////                                        }
//////                                    }
//////                                }
////////                            }
////
////                            Log.e("TAG", "onViewCreatedXXX: " + itemsCustomerOrders.size)
////                            itemLiveSchemesResult.value = true
////                        }
//
//
//                    }
//                }
//
//                override fun error(message: String) {
//                    super.error(message)
////                    showSnackBar(message)
//                }
//
//                override fun loading() {
//                    super.loading()
//                }
//            }
//        )
//    }


//    private var itemLiveSchemesResultSecond = MutableLiveData<BaseResponseDC<Any>>()
//    val itemLiveSchemesSecond: LiveData<BaseResponseDC<Any>> get() = itemLiveSchemesResultSecond
//    fun liveSchemeSecond(jsonObject: JSONObject) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.schemeHistoryList(requestBody = jsonObject.getJsonRequestBody())
//
//                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
//                    if (response.isSuccessful) {
//                        itemLiveSchemesResultSecond.value = response.body() as BaseResponseDC<Any>
//                    }
//                }
//
//                override fun error(message: String) {
//                    super.error(message)
////                    showSnackBar(message)
//                }
//
//                override fun loading() {
//                    super.loading()
//                }
//            }
//        )
//    }


//    var applyLink = MutableLiveData<Int>(-1)
//    fun applyLink(jsonObject: JSONObject, position: Int) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.applyLink(requestBody = jsonObject.getJsonRequestBody())
//
//                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
//                    if (response.isSuccessful) {
//                        applyLink.value = position
//                    } else {
//                        applyLink.value = -1
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

//
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
    /**/

    fun getData(mapData: ItemLiveScheme): Boolean {
        itemsCustomerOrders.forEach { schemeDataInner ->
            if (schemeDataInner.id != mapData.id) {
                Log.e("TAG", "schemeDataInnerFalseB: " + mapData.id + " :::: " + schemeDataInner.id)
                return true
            }
        }
        return false
    }


    fun callApiTranslate(_lang: String, _words: String): String {
        return repository.callApiTranslate(_lang, _words)
    }















    var filterNameBoolean = false
    var filterMobileBoolean = false
    var filterAadhaarBoolean = false
    var filterNikshayIdBoolean = false
    var filterDistributorNumberBoolean = false
    var filterStartDateBoolean = false
    var filterEndDateBoolean = false

    var filterName = ""
    var filterMobile= ""
    var filterAadhaar = ""
    var filterNikshayId = ""
    var filterDistributorNumber = ""
    var filterStartDate = ""
    var filterEndDate = ""


    val itemsProduct: ArrayList<ItemNBPAForm> = ArrayList()


    private var itemProducstResult = MutableLiveData<ItemNBPAFormRoot>()
    val itemProducts: LiveData<ItemNBPAFormRoot> get() = itemProducstResult
    fun getProducts(emptyMap: JSONObject, pageNumber: Int) =
        viewModelScope.launch {
            if (pageNumber == 0 || pageNumber == 1) {
                repository.callApi(
                    callHandler = object : CallHandler<Response<ItemNBPAFormRoot>> {
                        override suspend fun sendRequest(apiInterface: ApiInterface) =
                            apiInterface.getPopularMoviesListXX(emptyMap.getJsonRequestBody())

                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<ItemNBPAFormRoot>) {
                            if (response.isSuccessful) {
                                mainThread {
                                    try {
                                        Log.e("TAG", "successAA: ${response.body().toString()}")
                                        val mMineUserEntity = response.body()!!
                                        itemProducstResult.value = mMineUserEntity
                                    } catch (e: Exception) {
                                    }
                                }

                            }
                        }

                        override fun error(message: String) {
                            Log.e("TAG", "messageAA: ${message.toString()}")
                        }

                        override fun loading() {
                            super.loading()
                        }
                    }
                )
            } else {
                repository.callApiWithoutLoader (
                    callHandler = object : CallHandler<Response<ItemNBPAFormRoot>> {
                        override suspend fun sendRequest(apiInterface: ApiInterface) =
                            apiInterface.getPopularMoviesListXX(emptyMap.getJsonRequestBody())

                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<ItemNBPAFormRoot>) {
                            if (response.isSuccessful) {
                                mainThread {
                                    try {
                                        Log.e("TAG", "successAA: ${response.body().toString()}")
                                        var mMineUserEntity = response.body()!!
                                        itemProducstResult.value = mMineUserEntity
                                    } catch (e: Exception) {
                                    }
                                }
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




    val viewForm3Adapter = object : GenericAdapter<ItemForm3Binding, SchemeDetail>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemForm3Binding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemForm3Binding,
            model: SchemeDetail,
            position: Int
        ) {
            binding.apply {
                signaturePad.visibility = View.GONE
                searchLayout.visibility = View.GONE
                editTextHeight.isEnabled = false

                btnImagePassportsize.visibility = View.GONE
                btnIdentityImage.visibility = View.GONE

                editTextMonth.setText(""+model.foodMonth)
                editTextDate.setText(""+model.foodDate)
                editTextHeight.setText(""+model.foodHeight)

                ivSignature.loadImage(type = 1, url = { model.foodSignatureImage.url })
                ivImagePassportsizeImage.loadImage(type = 1, url = { model.foodItemImage.url })
                ivImageIdentityImage.loadImage(type = 1, url = { model.foodIdentityImage.url })

                lateinit var viewer: StfalconImageViewer<String>

                ivSignature.singleClick {
                    Log.e("TAG", "ivSignaturesingleClick")
                    viewer = StfalconImageViewer.Builder<String>(binding.root.context, arrayListOf(model.foodSignatureImage.url)) { view, image ->
                        Picasso.get().load(image).into(view)
                    }.withImageChangeListener {
                        viewer.updateTransitionImage(ivSignature)
                    }
                        .withBackgroundColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color._D9000000
                            )
                        )
                        .show()
                }
                ivImagePassportsizeImage.singleClick {
                    Log.e("TAG", "ivImagePassportsizeImagesingleClick")
//                        model.foodItemImage.url.let {
//                            arrayListOf(it).imageZoom(ivImagePassportsizeImage, 2)
//                        }
                    viewer = StfalconImageViewer.Builder<String>(binding.root.context, arrayListOf(model.foodItemImage.url)) { view, image ->
                        Picasso.get().load(image).into(view)
                    }.withImageChangeListener {
                        viewer.updateTransitionImage(ivImagePassportsizeImage)
                    }
                        .withBackgroundColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color._D9000000
                            )
                        )
                        .show()
                }
                ivImageIdentityImage.singleClick {
                    viewer = StfalconImageViewer.Builder<String>(binding.root.context, arrayListOf(model.foodIdentityImage.url)) { view, image ->
                        Picasso.get().load(image).into(view)
                    }.withImageChangeListener {
                        viewer.updateTransitionImage(ivImageIdentityImage)
                    }
                        .withBackgroundColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color._D9000000
                            )
                        )
                        .show()
                }
            }
        }
    }



    val viewForm4Adapter = object : GenericAdapter<ItemForm4Binding, SchemeDetail>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemForm4Binding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemForm4Binding,
            model: SchemeDetail,
            position: Int
        ) {
            binding.apply {
                signaturePad.visibility = View.GONE
                searchLayout.visibility = View.GONE

                editTextDietChartEvaluation.isEnabled = false
                editTextSuggestion.isEnabled = false
                editTextServiceProvider.isEnabled = false
                editTextHeight.isEnabled = false
                editTextComment.isEnabled = false


                if (model.dietChartDate.isNullOrEmpty()){
                    editTextDietDate.setText("")
                } else {
                    editTextDietDate.setText(""+model.dietChartDate)
                }
                if (model.dietChartEvaluation.isNullOrEmpty()){
                    editTextDietChartEvaluation.setText("")
                } else {
                    editTextDietChartEvaluation.setText(""+model.dietChartEvaluation)
                }
                if (model.dietChartSuggestion.isNullOrEmpty()){
                    editTextSuggestion.setText("")
                } else {
                    editTextSuggestion.setText(""+model.dietChartSuggestion)
                }
                if (model.dietChartServiceProvider.isNullOrEmpty()){
                    editTextServiceProvider.setText("")
                } else {
                    editTextServiceProvider.setText(""+model.dietChartServiceProvider)
                }
                if (model.homeVisitDate.isNullOrEmpty()){
                    editTextHomeDate.setText("")
                } else {
                    editTextHomeDate.setText(""+model.homeVisitDate)
                }
                if (model.homeVisitWeight.isNullOrEmpty()){
                    editTextHeight.setText("")
                } else {
                    editTextHeight.setText(""+model.homeVisitWeight)
                }

//                editTextDietDate.setText(model.dietChartDate.getNotNullData())
//                editTextDietChartEvaluation.setText(model.dietChartEvaluation.getNotNullData())
//                editTextSuggestion.setText(model.dietChartSuggestion.getNotNullData())
//                editTextServiceProvider.setText(model.dietChartServiceProvider.getNotNullData())
//                editTextHomeDate.setText(model.homeVisitDate.getNotNullData())
//                editTextHeight.setText(model.homeVisitWeight.getNotNullData())

                ivSignature.loadImage(type = 1, url = { model.homeVisitSignature.url})
                lateinit var viewer: StfalconImageViewer<String>
                ivSignature.singleClick {
                    Log.e("TAG", "ivSignaturesingleClick")
                    viewer = StfalconImageViewer.Builder<String>(binding.root.context, arrayListOf(model.homeVisitSignature.url)) { view, image ->
                        Picasso.get().load(image).into(view)
                    }.withImageChangeListener {
                        viewer.updateTransitionImage(ivSignature)
                    }
                        .withBackgroundColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color._D9000000
                            )
                        )
                        .show()
                }

                if (model.homeVisitRemark.isNullOrEmpty()){
                    editTextComment.setText("")
                } else {
                    editTextComment.setText(""+model.homeVisitRemark)
                }
            }
        }
    }



    val viewForm5Adapter = object : GenericAdapter<ItemForm5Binding, SchemeDetail>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemForm5Binding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemForm5Binding,
            model: SchemeDetail,
            position: Int
        ) {
            binding.apply {
                signatureProjectCoordinatorPad.visibility = View.GONE
                searchLayoutProjectCoordinator.visibility = View.GONE

                signatureProjectManagerPad.visibility = View.GONE
                searchLayoutProjectManager.visibility = View.GONE

                editTextTotalAmount.isEnabled = false
                editTextDetailsHealth.isEnabled = false
                editTextCommentHealth.isEnabled = false
                editTextAdditionalRationReceivedFromPds.isEnabled = false
                editTextDetailsPDS.isEnabled = false
                editTextCommentPDS.isEnabled = false
                editTextTotalnumberobtained.isEnabled = false
                editTextDetailsVitamin.isEnabled = false
                editTextCommentVitamin.isEnabled = false
                editTextotherReceivedHelp.isEnabled = false
                editTextDetailsHelp.isEnabled = false
                editTextCommentHelp.isEnabled = false

                if (model.dbtDate.isNullOrEmpty()){
                    editTextHealthDate.setText("")
                } else {
                    editTextHealthDate.setText(""+model.dbtDate)
                }
                if (model.dbtTotalAmount.isNullOrEmpty()){
                    editTextTotalAmount.setText("")
                } else {
                    editTextTotalAmount.setText(""+model.dbtTotalAmount)
                }
                if (model.dbtDetails.isNullOrEmpty()){
                    editTextDetailsHealth.setText("")
                } else {
                    editTextDetailsHealth.setText(""+model.dbtDetails)
                }
                if (model.dbtRemark.isNullOrEmpty()){
                    editTextCommentHealth.setText("")
                } else {
                    editTextCommentHealth.setText(""+model.dbtRemark)
                }
                if (model.extraGroceryPDS.isNullOrEmpty()){
                    editTextAdditionalRationReceivedFromPds.setText("")
                } else {
                    editTextAdditionalRationReceivedFromPds.setText(""+model.extraGroceryPDS)
                }
                if (model.extraGroceryPDSDetails.isNullOrEmpty()){
                    editTextDetailsPDS.setText("")
                } else {
                    editTextDetailsPDS.setText(""+model.extraGroceryPDSDetails)
                }
                if (model.extraGroceryPDSRemark.isNullOrEmpty()){
                    editTextCommentPDS.setText("")
                } else {
                    editTextCommentPDS.setText(""+model.extraGroceryPDSRemark)
                }
                if (model.multiVitaminDate.isNullOrEmpty()){
                    editTextmultiVitaminDate.setText("")
                } else {
                    editTextmultiVitaminDate.setText(""+model.multiVitaminDate)
                }
                if (model.multiVitaminTotalNumber.toString().isNullOrEmpty()){
                    editTextTotalnumberobtained.setText("")
                } else {
                    editTextTotalnumberobtained.setText(""+model.multiVitaminTotalNumber)
                }
                if (model.multiVitaminDetails.toString().isNullOrEmpty()){
                    editTextDetailsVitamin.setText("")
                } else {
                    editTextDetailsVitamin.setText(""+model.multiVitaminDetails)
                }
                if (model.multiVitaminRemark.toString().isNullOrEmpty()){
                    editTextCommentVitamin.setText("")
                } else {
                    editTextCommentVitamin.setText(""+model.multiVitaminRemark)
                }
                if (model.otherHelp.toString().isNullOrEmpty()){
                    editTextotherReceivedHelp.setText("")
                } else {
                    editTextotherReceivedHelp.setText(""+model.otherHelp)
                }
                if (model.helpDetails.toString().isNullOrEmpty()){
                    editTextDetailsHelp.setText("")
                } else {
                    editTextDetailsHelp.setText(""+model.helpDetails)
                }
                if (model.helpRemark.toString().isNullOrEmpty()){
                    editTextCommentHelp.setText("")
                } else {
                    editTextCommentHelp.setText(""+model.helpRemark)
                }





//                editTextHealthDate.setText(model.dbtDate.getNotNullData())
//                editTextTotalAmount.setText(model.dbtTotalAmount.getNotNullData())
//                editTextDetailsHealth.setText(model.dbtDetails.getNotNullData())
//                editTextCommentHealth.setText(model.dbtRemark.getNotNullData())
//                editTextAdditionalRationReceivedFromPds.setText(model.extraGroceryPDS.getNotNullData())
//                editTextDetailsPDS.setText(model.extraGroceryPDSDetails.getNotNullData())
//                editTextCommentPDS.setText(model.extraGroceryPDSRemark.getNotNullData())
//                editTextmultiVitaminDate.setText(model.multiVitaminDate.getNotNullData())
//                editTextTotalnumberobtained.setText(model.multiVitaminTotalNumber.getNotNullData())
//                editTextDetailsVitamin.setText(model.multiVitaminDetails.getNotNullData())
//                editTextCommentVitamin.setText(model.multiVitaminRemark.getNotNullData())
//                editTextotherReceivedHelp.setText(model.otherHelp.getNotNullData())
//                editTextDetailsHelp.setText(model.helpDetails.getNotNullData())
//                editTextCommentHelp.setText(model.helpRemark.getNotNullData())

                lateinit var viewer: StfalconImageViewer<String>
                ivSignatureProjectCoordinator.loadImage(type = 1, url = { model.projectCoordinatorSignature.url})
                ivSignatureProjectCoordinator.singleClick {
                    Log.e("TAG", "ivSignaturesingleClick")
                    viewer = StfalconImageViewer.Builder<String>(binding.root.context, arrayListOf(model.projectCoordinatorSignature.url)) { view, image ->
                        Picasso.get().load(image).into(view)
                    }.withImageChangeListener {
                        viewer.updateTransitionImage(ivSignatureProjectCoordinator)
                    }
                        .withBackgroundColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color._D9000000
                            )
                        )
                        .show()
                }

                ivSignatureProjectManager.loadImage(type = 1, url = { model.projectManagerSignature.url})
                ivSignatureProjectManager.singleClick {
                    Log.e("TAG", "ivSignaturesingleClick")
                    viewer = StfalconImageViewer.Builder<String>(binding.root.context, arrayListOf(model.projectManagerSignature.url)) { view, image ->
                        Picasso.get().load(image).into(view)
                    }.withImageChangeListener {
                        viewer.updateTransitionImage(ivSignatureProjectManager)
                    }
                        .withBackgroundColor(
                            ContextCompat.getColor(
                                binding.root.context,
                                R.color._D9000000
                            )
                        )
                        .show()
                }
            }
        }
    }
}