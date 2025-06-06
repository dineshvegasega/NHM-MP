package com.nhm.distributor.screens.main.profiles

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ProfessionalDetailsBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.screens.interfaces.CallBackListener
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.locale
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.callPermissionDialog
import com.nhm.distributor.utils.getCameraPath
import com.nhm.distributor.utils.getMediaFilePathFor
import com.nhm.distributor.utils.isNetworkAvailable
import com.nhm.distributor.utils.loadImage
import com.nhm.distributor.utils.mainThread
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.showOptions
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.Calendar


@AndroidEntryPoint
class ProfessionalDetails : Fragment(), CallBackListener {
    private val viewModel: ProfilesVM by activityViewModels()
    private var _binding: ProfessionalDetailsBinding? = null
    private val binding get() = _binding!!


    companion object {
        var callBackListener: CallBackListener? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfessionalDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    var scrollPoistion = 0


    private fun callMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            )
        } else {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            if (!permissions.entries.toString().contains("false")) {
                requireActivity().showOptions {
                    when(this){
                        1 -> forCamera()
                        2 -> forGallery()
                    }
                }
            } else {
                requireActivity().callPermissionDialog{
                    someActivityResultLauncher.launch(this)
                }
            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
    }



    var imagePosition = 0
    private var pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            lifecycleScope.launch {
                if (uri != null) {
                    when (imagePosition) {
                        1 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
                            viewModel.data.shopImage = compressedImageFile.path
                            binding.ivImageShopImage.loadImage(type = 1, url = { viewModel.data.shopImage!! })
                        }

                        2 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
                            viewModel.data.ImageUploadCOV = compressedImageFile.path
                            binding.ivImageCOV.loadImage(type = 1, url = { viewModel.data.ImageUploadCOV!! })
                        }

                        3 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
                            viewModel.data.UploadSurveyReceipt = compressedImageFile.path
                            binding.ivImageSurveyReceipt.loadImage(type = 1, url = { viewModel.data.UploadSurveyReceipt!! })
                        }

                        4 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
                            viewModel.data.ImageUploadLOR = compressedImageFile.path
                            binding.ivImageLOR.loadImage(type = 1, url = { viewModel.data.ImageUploadLOR!! })
                        }

                        5 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
                            viewModel.data.UploadChallan = compressedImageFile.path
                            binding.ivImageUploadChallan.loadImage(type = 1, url = { viewModel.data.UploadChallan!! })
                        }

                        6 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
                            viewModel.data.UploadApprovalLetter = compressedImageFile.path
                            binding.ivImageApprovalLetter.loadImage(type = 1, url = { viewModel.data.UploadApprovalLetter!! })
                        }
                    }
                }
            }
        }


    var uriReal: Uri? = null
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        lifecycleScope.launch {
            if (uri == true) {
                when (imagePosition) {
                    1 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
                        viewModel.data.shopImage = compressedImageFile.path
                        binding.ivImageShopImage.loadImage(type = 1, url = { viewModel.data.shopImage!! })
                    }

                    2 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
                        viewModel.data.ImageUploadCOV = compressedImageFile.path
                        binding.ivImageCOV.loadImage(type = 1, url = { viewModel.data.ImageUploadCOV!! })
                    }

                    3 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
                        viewModel.data.UploadSurveyReceipt = compressedImageFile.path
                        binding.ivImageSurveyReceipt.loadImage(type = 1, url = { viewModel.data.UploadSurveyReceipt!! })
                    }

                    4 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
                        viewModel.data.ImageUploadLOR = compressedImageFile.path
                        binding.ivImageLOR.loadImage(type = 1, url = { viewModel.data.ImageUploadLOR!! })
                    }

                    5 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
                        viewModel.data.UploadChallan = compressedImageFile.path
                        binding.ivImageUploadChallan.loadImage(type = 1, url = { viewModel.data.UploadChallan!! })
                    }

                    6 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
                        viewModel.data.UploadApprovalLetter = compressedImageFile.path
                        binding.ivImageApprovalLetter.loadImage(type = 1, url = { viewModel.data.UploadApprovalLetter!! })
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.e("TAG", "onCreate")
        callBackListener = this
    }

    var docs: String? = ""
    var stringCOV = ""
    var stringSurveyReceipt = ""
    var stringLOR = ""
    var stringChallan = ""
    var stringApprovalLetter = ""

    var scheme: String? = ""
    var stringPm_swanidhi_schemeSingle = ""
    var stringOtherSchemeName = ""

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBackListener = this


        binding.apply {
            fieldsEdit()
            readData(LOGIN_DATA) { loginUser ->
                if (loginUser != null) {
                    val data = Gson().fromJson(loginUser, Login::class.java)
//                     Log.e("TAG", "dataZZ "+data.toString())

                    if (data.vending_documents != "null") {
                        if (data.vending_documents?.contains(getString(R.string.COVText)) == true) {
                            cbRememberCOV.isChecked = true
                            stringCOV = getString(R.string.COVText) + " "
                        }
                        if (data.vending_documents?.contains(getString(R.string.LORText)) == true) {
                            cbRememberSurveyReceipt.isChecked = true
                            stringSurveyReceipt = getString(R.string.Survery_ReceiptText) + " "
                        }
                        if (data.vending_documents?.contains(getString(R.string.Survery_ReceiptText)) == true) {
                            cbRememberLOR.isChecked = true
                            stringLOR = getString(R.string.LORText) + " "
                        }
                        if (data.vending_documents?.contains(getString(R.string.ChallanText)) == true) {
                            cbRememberChallan.isChecked = true
                            stringChallan = getString(R.string.ChallanText) + " "
                        }
                        if (data.vending_documents?.contains(getString(R.string.Approval_LetterText)) == true) {
                            cbRememberApprovalLetter.isChecked = true
                            stringApprovalLetter = getString(R.string.Approval_LetterText) + " "
                        }
                    }

                    docs =
                        stringCOV + stringSurveyReceipt + stringLOR + stringChallan + stringApprovalLetter
                    viewModel.data.vending_documents = docs


                    cbRememberCOV.singleClick {
                        if (cbRememberCOV.isChecked) {
                            stringCOV = getString(R.string.COVText) + " "
                        } else {
                            stringCOV = ""
                        }
                    }

                    cbRememberSurveyReceipt.singleClick {
                        if (cbRememberSurveyReceipt.isChecked) {
                            stringSurveyReceipt = getString(R.string.Survery_ReceiptText) + " "
                        } else {
                            stringSurveyReceipt = ""
                        }
                    }

                    cbRememberLOR.singleClick {
                        if (cbRememberLOR.isChecked) {
                            stringLOR = getString(R.string.LORText) + " "
                        } else {
                            stringLOR = ""
                        }
                    }

                    cbRememberChallan.singleClick {
                        if (cbRememberChallan.isChecked) {
                            stringChallan = getString(R.string.ChallanText) + " "
                        } else {
                            stringChallan = ""
                        }
                    }

                    cbRememberApprovalLetter.singleClick {
                        if (cbRememberApprovalLetter.isChecked) {
                            stringApprovalLetter = getString(R.string.Approval_LetterText) + " "
                        } else {
                            stringApprovalLetter = ""
                        }
                    }

                    if (data.availed_scheme != "null" && data.availed_scheme != null) {
                        viewModel.data.governmentScheme = true
                        ivRdGovernmentYes.isChecked = true
                        inclideGovernment.root.visibility = View.VISIBLE
                        if (data.availed_scheme?.contains(getString(R.string.pm_swanidhi_schemeSingle)) == true) {
                            inclideGovernment.cbRememberPMSwanidhiScheme.isChecked = true
                            stringPm_swanidhi_schemeSingle =
                                getString(R.string.pm_swanidhi_schemeSingle) + " "
                        }

                        val xx = data.availed_scheme?.split(" ")
                        xx?.let {
                            for (item in xx.iterator()) {
                                if (item != getString(R.string.pm_swanidhi_schemeSingle)) {
                                    inclideGovernment.cbRememberOthersPleaseName.isChecked = true
                                    stringOtherSchemeName += item + " "
                                    inclideGovernment.editTextSchemeName.setText(
                                        stringOtherSchemeName
                                    )
                                }
                            }
                        }

                    } else {
                        viewModel.data.governmentScheme = false
                        ivRdGovernmentYes.isChecked = false
                        inclideGovernment.root.visibility = View.GONE
                    }
                    scheme = stringPm_swanidhi_schemeSingle + stringOtherSchemeName
                    viewModel.data.schemeName = scheme


                    inclideGovernment.cbRememberPMSwanidhiScheme.singleClick {
                        if (inclideGovernment.cbRememberPMSwanidhiScheme.isChecked) {
                            stringPm_swanidhi_schemeSingle =
                                getString(R.string.pm_swanidhi_schemeSingle) + " "
                        } else {
                            stringPm_swanidhi_schemeSingle = ""
                        }
                    }


                    inclideGovernment.cbRememberOthersPleaseName.singleClick {
                        if (inclideGovernment.cbRememberOthersPleaseName.isChecked) {
                            stringOtherSchemeName =
                                inclideGovernment.editTextSchemeName.text.toString()
                        } else {
                            stringOtherSchemeName = ""
                        }
                    }

                    binding.scroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                        scrollPoistion = scrollY
                    })

                    ivRdGovernmentYes.singleClick {
                        viewModel.data.governmentScheme = true
                        inclideGovernment.layoutGovernmentScheme.visibility = View.VISIBLE
                        setScrollPosition(2, true)
                    }

                    ivRdGovernmentNo.singleClick {
                        viewModel.data.governmentScheme = false
                        inclideGovernment.layoutGovernmentScheme.visibility = View.GONE
                        setScrollPosition(2, false)
                    }

                    if (requireContext().isNetworkAvailable()) {
                        viewModel.marketplace(view)
                        viewModel.vending(view)
                        viewModel.localOrganisation(requireView(), JSONObject().apply {
                            put(state_id, data.vending_state?.id)
                            put(district_id, data.vending_district?.id)
                        })
                        viewModel.stateCurrent(view)
                    } else {
                        requireContext().callNetworkDialog()
                    }

                    viewModel.marketPlaceTrue.observe(viewLifecycleOwner, Observer {
                        if (it == true) {
                            for (item in viewModel.itemMarketplace) {
                                if (item.marketplace_id == data.type_of_marketplace) {
                                    binding.editTextTypeofMarketPlace.setText("" + item.name)
                                }
                            }
                        }
                    })
                    viewModel.marketplaceId = data.type_of_marketplace.toInt()
                    viewModel.data.type_of_marketplace = "" + viewModel.marketplaceId

                    if (data.type_of_marketplace.toInt() == 7) {
                        editTextTypeofMarketPlaceEnter.visibility = View.VISIBLE
                        viewModel.data.marketpalce_others = "" + data.marketpalce_others
                        editTextTypeofMarketPlaceEnter.setText("${data.marketpalce_others}")
                    } else {
                        editTextTypeofMarketPlaceEnter.visibility = View.GONE
                        viewModel.data.marketpalce_others = "null"
                        editTextTypeofMarketPlaceEnter.setText("")
                    }


                    viewModel.vendingTrue.observe(viewLifecycleOwner, Observer {
                        if (it == true) {
                            for (item in viewModel.itemVending) {
                                if (item.vending_id == data.type_of_vending) {
                                    binding.editTextTypeofVending.setText("" + item.name)
                                }
                            }
                        }
                    })
                    viewModel.vendingId = data.type_of_vending.toInt()
                    viewModel.data.type_of_vending = "" + viewModel.vendingId
                    if (data.type_of_vending.toInt() == 11) {
                        editTextTypeofVendingEnter.visibility = View.VISIBLE
                        viewModel.data.vending_others = "" + data.vending_others
                        editTextTypeofVendingEnter.setText("${data.vending_others}")
                    } else {
                        editTextTypeofVendingEnter.visibility = View.GONE
                        viewModel.data.vending_others = "null"
                        editTextTypeofVendingEnter.setText("")
                    }


                    data.total_years_of_business?.let {
                        editTextTotalYearsofVending.setText("${data.total_years_of_business}")
                    }

                    data.vending_time_from?.let {
                        editTextVendingTimeOpen.setText("${data.vending_time_from}")
                    }

                    data.vending_time_to?.let {
                        editTextVendingTimeClose.setText("${data.vending_time_to}")
                    }

                    viewModel.data.total_years_of_business = "" + data.total_years_of_business
                    viewModel.data.open = "" + data.vending_time_from
                    viewModel.data.close = "" + data.vending_time_to

                    data.vending_time_from?.let {
                        val datetime = Calendar.getInstance()
                        datetime[Calendar.HOUR_OF_DAY] =
                            data.vending_time_from.split(":")[0].toInt()
                        datetime[Calendar.MINUTE] = data.vending_time_from.split(":")[1].toInt()
                        val strHrsToShow =
                            if (datetime.get(Calendar.HOUR) === 0) "12" else Integer.toString(
                                datetime.get(Calendar.HOUR)
                            )
                        var am_pm = ""
                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";
                        binding.editTextVendingTimeOpen.setText(
                            strHrsToShow + ":" + (if (data.vending_time_from.split(":")[1].toString().length == 1) "0" + datetime.get(
                                Calendar.MINUTE
                            ) else datetime.get(Calendar.MINUTE)) + " " + am_pm
                        )
                    }



                    data.vending_time_to?.let {
                        val datetime = Calendar.getInstance()
                        datetime[Calendar.HOUR_OF_DAY] = data.vending_time_to.split(":")[0].toInt()
                        datetime[Calendar.MINUTE] = data.vending_time_to.split(":")[1].toInt()
                        val strHrsToShow =
                            if (datetime.get(Calendar.HOUR) === 0) "12" else Integer.toString(
                                datetime.get(Calendar.HOUR)
                            )
                        var am_pm = ""
                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";
                        binding.editTextVendingTimeClose.setText(
                            strHrsToShow + ":" + (if (data.vending_time_to.split(":")[1].toString().length == 1) "0" + datetime.get(
                                Calendar.MINUTE
                            ) else datetime.get(Calendar.MINUTE)) + " " + am_pm
                        )
                    }

                    if (IS_LANGUAGE_ALL) {
                        runBlocking {
                            mainThread {
                                data.vending_state?.let {
                                    if (MainActivity.context.get()!!
                                            .getString(R.string.englishVal) == "" + locale
                                    ) {
                                        editTextVendingSelectState.setText("${data.vending_state?.name}")
                                    } else {
                                        viewModel.show()
                                        val nameChanged: String =
                                            viewModel.callApiTranslate(
                                                "" + locale,
                                                data.vending_state.name
                                            )
                                        editTextVendingSelectState.setText("${nameChanged}")
                                        viewModel.hide()
                                    }
                                }

                                data.vending_district?.let {
                                    if (MainActivity.context.get()!!
                                            .getString(R.string.englishVal) == "" + locale
                                    ) {
                                        editTextVendingSelectDistrict.setText("${data.vending_district?.name}")
                                    } else {
                                        viewModel.show()
                                        val nameChanged: String =
                                            viewModel.callApiTranslate(
                                                "" + locale,
                                                data.vending_district.name
                                            )
                                        editTextVendingSelectDistrict.setText("${nameChanged}")
                                        viewModel.hide()
                                    }
                                }

                                data.vending_municipality_panchayat?.let {
                                    if (MainActivity.context.get()!!
                                            .getString(R.string.englishVal) == "" + locale
                                    ) {
                                        editTextVendingMunicipalityPanchayat.setText("${data.vending_municipality_panchayat?.name}")
                                    } else {
                                        viewModel.show()
                                        val nameChanged: String =
                                            viewModel.callApiTranslate(
                                                "" + locale,
                                                data.vending_municipality_panchayat.name
                                            )
                                        editTextVendingMunicipalityPanchayat.setText("${nameChanged}")
                                        viewModel.hide()
                                    }
                                }



                                if (data.local_organisation != null) {
                                    if (MainActivity.context.get()!!
                                            .getString(R.string.englishVal) == "" + locale
                                    ) {
                                        editTextLocalOrganisation.setText("${data.local_organisation?.name}")
                                        editTextLocalOrganisation.visibility = View.VISIBLE
                                        ivRdLocalOrgnaizationYes.isChecked = true
                                        viewModel.data.localOrganisation =
                                            "" + data.local_organisation?.id
                                    } else {
                                        viewModel.show()
                                        val nameChanged: String =
                                            viewModel.callApiTranslate(
                                                "" + locale,
                                                data.local_organisation.name
                                            )
                                        editTextLocalOrganisation.setText(nameChanged)
                                        editTextLocalOrganisation.visibility = View.VISIBLE
                                        ivRdLocalOrgnaizationYes.isChecked = true
                                        viewModel.data.localOrganisation =
                                            "" + data.local_organisation?.id
                                        viewModel.hide()
                                    }
                                } else {
                                    editTextLocalOrganisation.visibility = View.GONE
                                    ivRdLocalOrgnaizationYes.isChecked = false
                                    viewModel.data.localOrganisation = "-1"
                                }
                            }
                        }
                    } else {
                        data.vending_state?.let {
                            editTextVendingSelectState.setText("${data.vending_state?.name}")
                        }

                        data.vending_district?.let {
                            editTextVendingSelectDistrict.setText("${data.vending_district?.name}")
                        }

                        data.vending_municipality_panchayat?.let {
                            editTextVendingMunicipalityPanchayat.setText("${data.vending_municipality_panchayat?.name}")
                        }


                        if (data.local_organisation != null) {
                            editTextLocalOrganisation.setText("${data.local_organisation?.name}")
                            editTextLocalOrganisation.visibility = View.VISIBLE
                            ivRdLocalOrgnaizationYes.isChecked = true
                            viewModel.data.localOrganisation = "" + data.local_organisation?.id
                        } else {
                            editTextLocalOrganisation.visibility = View.GONE
                            ivRdLocalOrgnaizationYes.isChecked = false
                            viewModel.data.localOrganisation = "-1"
                        }
                    }


                    if (data.vending_pincode != null) {
                        editTextVendingSelectPincode.setText("" + data.vending_pincode?.pincode!!.toInt())
                    } else {
                        editTextVendingSelectPincode.setText("")
                    }

                    data.vending_address?.let {
                        if (data.vending_address != "null") {
                            editTextVendingAddress.setText("${data.vending_address}")
                        }
                    }

                    viewModel.data.vending_state = "" + data.vending_state?.id
                    viewModel.data.vending_district = "" + data.vending_district?.id
                    viewModel.data.vending_municipality_panchayat =
                        "" + data.vending_municipality_panchayat?.id
                    if (data.vending_pincode != null) {
                        viewModel.data.vending_pincode = data.vending_pincode?.pincode
                    } else {
                        viewModel.data.vending_pincode = "0"
                    }
                    viewModel.data.vending_address = "" + data.vending_address

                    data.vending_state?.let {
                        viewModel.stateIdVending = data.vending_state.id
                    }
                    data.vending_district?.let {
                        viewModel.districtIdVending = data.vending_district.id
                    }
                    data.vending_municipality_panchayat?.let {
                        viewModel.panchayatIdVending = data.vending_municipality_panchayat.id
                    }

                    if (data.vending_pincode != null) {
                        viewModel.pincodeIdVending = data.vending_pincode.pincode
                    } else {
                        viewModel.pincodeIdVending = ""
                    }





                    data.shop_image?.let {
                        viewModel.data.shopImage = data.shop_image?.url
                        ivImageShopImage.loadImage(type = 1, url = { data.shop_image.url })
                    }
                    data.cov_image?.let {
                        viewModel.data.ImageUploadCOV = data.cov_image?.url
                        ivImageCOV.loadImage(type = 1, url = { data.cov_image.url })
                    }
                    data.survey_receipt_image?.let {
                        viewModel.data.UploadSurveyReceipt = data.survey_receipt_image?.url
                        ivImageSurveyReceipt.loadImage(type = 1, url = { data.survey_receipt_image.url })
                    }
                    data.lor_image?.let {
                        viewModel.data.ImageUploadLOR = data.lor_image?.url
                        ivImageLOR.loadImage(type = 1, url = { data.lor_image.url })
                    }
                    data.challan_image?.let {
                        viewModel.data.UploadChallan = data.challan_image?.url
                        ivImageUploadChallan.loadImage(type = 1, url = { data.challan_image.url })
                    }
                    data.approval_letter_image?.let {
                        viewModel.data.UploadApprovalLetter = data.approval_letter_image?.url
                        ivImageApprovalLetter.loadImage(type = 1, url = { data.approval_letter_image.url })
                    }
                }
            }



            editTextTypeofMarketPlace.singleClick {
                var index = 0
                val list = arrayOfNulls<String>(viewModel.itemMarketplace.size)
                for (value in viewModel.itemMarketplace) {
                    list[index] = value.name
                    index++
                }
                requireActivity().showDropDownDialog(type = 10, arrayList = list){
                    binding.editTextTypeofMarketPlace.setText(name)
                    viewModel.marketplaceId = viewModel.itemMarketplace[position].marketplace_id
                    viewModel.data.type_of_marketplace = "" + viewModel.marketplaceId
                    if (viewModel.marketplaceId == 7) {
                        binding.editTextTypeofMarketPlaceEnter.visibility = View.VISIBLE
                        viewModel.data.marketpalce_others =
                            "" + binding.editTextTypeofMarketPlaceEnter.text.toString()
                    } else {
                        binding.editTextTypeofMarketPlaceEnter.visibility = View.GONE
                        viewModel.data.marketpalce_others = ""
                        binding.editTextTypeofMarketPlaceEnter.setText("")
                    }
                }
            }

            editTextTypeofVending.singleClick {
                var index = 0
                val list = arrayOfNulls<String>(viewModel.itemVending.size)
                for (value in viewModel.itemVending) {
                    list[index] = value.name
                    index++
                }
                requireActivity().showDropDownDialog(type = 11, arrayList = list){
                    binding.editTextTypeofVending.setText(name)
                    viewModel.vendingId = viewModel.itemVending[position].vending_id
                    viewModel.data.type_of_vending = "" + viewModel.vendingId
                    if (viewModel.vendingId == 11) {
                        binding.editTextTypeofVendingEnter.visibility = View.VISIBLE
                        viewModel.data.vending_others =
                            "" + binding.editTextTypeofVendingEnter.text.toString()
                    } else {
                        binding.editTextTypeofVendingEnter.visibility = View.GONE
                        viewModel.data.vending_others = ""
                        binding.editTextTypeofVendingEnter.setText("")
                    }
                }
            }

            editTextTotalYearsofVending.singleClick {
                requireActivity().showDropDownDialog(type = 12){
                    binding.editTextTotalYearsofVending.setText(name)
                    viewModel.data.total_years_of_business = name
                }
            }


            editTextVendingTimeOpen.singleClick {
                requireActivity().showDropDownDialog(type = 14){
                    binding.editTextVendingTimeOpen.setText(name)
                    viewModel.data.open = title
                }
            }


            editTextVendingTimeClose.singleClick {
                requireActivity().showDropDownDialog(type = 14){
                    binding.editTextVendingTimeClose.setText(name)
                    viewModel.data.close = title
                }
            }



            editTextVendingSelectState.singleClick {
                var index = 0
                val list = arrayOfNulls<String>(viewModel.itemStateVending.size)
                for (value in viewModel.itemStateVending) {
                    list[index] = value.name
                    index++
                }
                requireActivity().showDropDownDialog(type = 6, arrayList = list){
                    binding.editTextVendingSelectState.setText(name)
                    viewModel.stateIdVending = viewModel.itemStateVending[position].id
                    if (networkFailed) {
                        view?.let { viewModel.districtCurrent(it, viewModel.stateIdVending) }
                        if (!IS_LANGUAGE_ALL) {
                            view?.let { viewModel.panchayatCurrent(it, viewModel.stateIdVending) }
                        }
                    } else {
                        requireContext().callNetworkDialog()
                    }

                    if (viewModel.stateIdVending != 0 && viewModel.districtIdVending != 0) {
                        if (networkFailed) {
                            view?.let {
                                viewModel.localOrganisation(it, JSONObject().apply {
                                    put(state_id, viewModel.stateIdVending)
                                    put(district_id, viewModel.districtIdVending)
                                })
                            }
                        } else {
                            requireContext().callNetworkDialog()
                        }
                    }

                    viewModel.data.vending_state = "" + viewModel.stateIdVending
                    binding.editTextVendingSelectDistrict.setText("")
                    binding.editTextVendingMunicipalityPanchayat.setText("")
                    viewModel.districtIdVending = 0
                    viewModel.panchayatIdVending = 0
                }
            }

            editTextVendingSelectDistrict.singleClick {
                if (!(viewModel.stateIdVending > 0)) {
                    showSnackBar(getString(R.string.select_state_))
                } else {
                    if (viewModel.itemDistrictVending.size > 0) {
                        var index = 0
                        val list = arrayOfNulls<String>(viewModel.itemDistrictVending.size)
                        for (value in viewModel.itemDistrictVending) {
                            list[index] = value.name
                            index++
                        }
                        requireActivity().showDropDownDialog(type = 7, arrayList = list){
                            binding.editTextVendingSelectDistrict.setText(name)
                            viewModel.districtIdVending = viewModel.itemDistrictVending[position].id
                            if (networkFailed) {
                                view?.let { viewModel.pincodeCurrent(it, viewModel.districtIdVending) }
                            } else {
                                requireContext().callNetworkDialog()
                            }

                            viewModel.data.vending_district = "" + viewModel.districtIdVending
                            if (viewModel.stateIdVending != 0 && viewModel.districtIdVending != 0) {
                                if (networkFailed) {
                                    view?.let {
                                        viewModel.localOrganisation(it, JSONObject().apply {
                                            put(state_id, viewModel.stateIdVending)
                                            put(district_id, viewModel.districtIdVending)
                                        })
                                    }
                                } else {
                                    requireContext().callNetworkDialog()
                                }
                            }

                            binding.editTextVendingSelectPincode.setText("")
                            viewModel.pincodeId = ""
                        }
                    } else {
                        showSnackBar(getString(R.string.not_district))
                    }
                }
            }

            editTextVendingMunicipalityPanchayat.singleClick {
                if (!(viewModel.stateIdVending > 0)) {
                    showSnackBar(getString(R.string.select_state_))
                } else {
                    if (viewModel.itemPanchayatVending.size > 0) {
                        var index = 0
                        val list = arrayOfNulls<String>(viewModel.itemPanchayatVending.size)
                        for (value in viewModel.itemPanchayatVending) {
                            list[index] = value.name
                            index++
                        }
                        requireActivity().showDropDownDialog(type = 8, arrayList = list){
                            binding.editTextVendingMunicipalityPanchayat.setText(name)
                            viewModel.panchayatIdVending = viewModel.itemPanchayatVending[position].id
                            viewModel.data.vending_municipality_panchayat = "" + viewModel.panchayatIdVending
                        }
                    } else {
                        showSnackBar(getString(R.string.not_municipality_panchayat))
                    }
                }
            }

            editTextVendingSelectPincode.singleClick {
                if (!(viewModel.districtIdVending > 0)) {
                    showSnackBar(getString(R.string.select_district_))
                } else {
                    if (viewModel.itemPincodeVending.size > 0) {

                        var index = 0
                        val list = arrayOfNulls<String>(viewModel.itemPincodeVending.size)
                        for (value in viewModel.itemPincodeVending) {
                            list[index] = value.pincode
                            index++
                        }
                        requireActivity().showDropDownDialog(type = 9, arrayList = list){
                            binding.editTextVendingSelectPincode.setText(name)
                            viewModel.pincodeIdVending = name
                            viewModel.data.vending_pincode = "" + viewModel.pincodeIdVending
                        }
                    } else {
                        showSnackBar(getString(R.string.not_pincode))
                    }
                }
            }


            ivRdLocalOrgnaizationYes.singleClick {
                editTextLocalOrganisation.visibility = View.VISIBLE
                setScrollPosition(1, true)
            }

            ivRdLocalOrgnaizationNo.singleClick {
                editTextLocalOrganisation.visibility = View.GONE
                setScrollPosition(1, false)
                viewModel.data.localOrganisation = "-1"
            }


            editTextLocalOrganisation.singleClick {
                if (viewModel.itemLocalOrganizationVending.size > 0) {
                    var index = 0
                    val list = arrayOfNulls<String>(viewModel.itemLocalOrganizationVending.size)
                    for (value in viewModel.itemLocalOrganizationVending) {
                        list[index] = value.local_organisation_name
                        index++
                    }
                    requireActivity().showDropDownDialog(type = 13, arrayList = list){
                        binding.editTextLocalOrganisation.setText(name)
                        viewModel.localOrganizationIdVending =
                            viewModel.itemLocalOrganizationVending[position].id
                        viewModel.data.localOrganisation = "" + viewModel.localOrganizationIdVending
                    }
                } else {
                    showSnackBar(getString(R.string.not_Organisation))
                }
            }




            btnImageShopImage.singleClick {
                imagePosition = 1
                callMediaPermissions()
            }
            btnImageCOV.singleClick {
                imagePosition = 2
                callMediaPermissions()
            }
            btnImageSurveyReceipt.singleClick {
                imagePosition = 3
                callMediaPermissions()
            }
            btnImageLOR.singleClick {
                imagePosition = 4
                callMediaPermissions()
            }
            btnImageUploadChallan.singleClick {
                imagePosition = 5
                callMediaPermissions()
            }
            btnImageApprovalLetter.singleClick {
                imagePosition = 6
                callMediaPermissions()
            }

            binding.scroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                scrollPoistion = scrollY
            })




            btnAddtoCart.singleClick {
                viewModel.data.marketpalce_others =
                    "" + editTextTypeofMarketPlaceEnter.text.toString()
                viewModel.data.vending_others = "" + editTextTypeofVendingEnter.text.toString()

                viewModel.data.vending_address = "" + editTextVendingAddress.text.toString()

                docs =
                    stringCOV + stringSurveyReceipt + stringLOR + stringChallan + stringApprovalLetter
                viewModel.data.vending_documents = docs

                scheme = stringPm_swanidhi_schemeSingle + stringOtherSchemeName
                viewModel.data.schemeName = scheme
            }

        }
    }


    private fun fieldsEdit() {
        binding.apply {
            viewModel.isEditable.observe(viewLifecycleOwner, Observer {
//                Log.e("TAG", "isEditable "+it)
                editTextTypeofMarketPlace.isEnabled = it
                editTextTypeofMarketPlaceEnter.isEnabled = it
                editTextTypeofVending.isEnabled = it
                editTextTypeofVendingEnter.isEnabled = it
                editTextTotalYearsofVending.isEnabled = it
                editTextVendingTimeOpen.isEnabled = it
                editTextVendingTimeClose.isEnabled = it
                editTextVendingSelectState.isEnabled = it
                editTextVendingSelectDistrict.isEnabled = it
                editTextVendingMunicipalityPanchayat.isEnabled = it
                editTextVendingSelectPincode.isEnabled = it
                editTextVendingAddress.isEnabled = it
                ivRdLocalOrgnaizationYes.isEnabled = it
                ivRdLocalOrgnaizationNo.isEnabled = it
                editTextLocalOrganisation.isEnabled = it

                btnImageShopImage.isEnabled = it
                btnImageCOV.isEnabled = it
                btnImageSurveyReceipt.isEnabled = it
                btnImageLOR.isEnabled = it
                btnImageUploadChallan.isEnabled = it
                btnImageApprovalLetter.isEnabled = it

                cbRememberCOV.isEnabled = it
                cbRememberSurveyReceipt.isEnabled = it
                cbRememberLOR.isEnabled = it
                cbRememberChallan.isEnabled = it
                cbRememberApprovalLetter.isEnabled = it

                ivRdGovernmentYes.isEnabled = it
                ivRdGovernmentNo.isEnabled = it
                inclideGovernment.cbRememberPMSwanidhiScheme.isEnabled = it
                inclideGovernment.cbRememberOthersPleaseName.isEnabled = it
                inclideGovernment.editTextSchemeName.isEnabled = it
            })
        }
    }


    private fun setScrollPosition(type: Int, ifTrue: Boolean) {
        when (type) {
            1 -> {
                ObjectAnimator.ofInt(binding.scroll, "scrollY", scrollPoistion).setDuration(100)
                    .start()
            }

            2 -> {
//                Handler(Looper.getMainLooper()).postDelayed(Runnable {
//                    val itemHeight =
//                        binding.inclideGovernment.layoutGovernmentScheme.height
//                    binding.scroll.smoothScrollTo(0,scrollPoistion * itemHeight)
//                }, 50)
            }
        }
    }





    private fun forCamera() {
        requireActivity().getCameraPath {
            uriReal = this
            captureMedia.launch(uriReal)
        }
    }

    private fun forGallery() {
        requireActivity().runOnUiThread() {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }


    override fun onCallBack(pos: Int) {
        if (pos == 3) {
            update()
        }
    }



    private fun update() {
//        Log.e("TAG", "dataZZupdate "+viewModel.marketplaceId)
        binding.apply {
            if (viewModel.marketplaceId == 0) {
                showSnackBar(getString(R.string.type_of_market_place))
            } else if (viewModel.marketplaceId == 7 && editTextTypeofMarketPlaceEnter.text.toString()
                    .isEmpty()
            ) {
                showSnackBar(getString(R.string.enter_market_place))
            } else if (viewModel.vendingId == 0) {
                showSnackBar(getString(R.string.type_of_vending))
            } else if (viewModel.vendingId == 11 && editTextTypeofVendingEnter.text.toString()
                    .isEmpty()
            ) {
                showSnackBar(getString(R.string.enter_vending))
            } else if (editTextTotalYearsofVending.text.toString().isEmpty()) {
                showSnackBar(getString(R.string.total_years_of_vending))
            } else if (editTextVendingTimeOpen.text.toString().isEmpty()) {
                showSnackBar(getString(R.string.open_time))
            } else if (editTextVendingTimeClose.text.toString().isEmpty()) {
                showSnackBar(getString(R.string.close_time))
            } else if (!(viewModel.stateIdVending > 0)) {
                showSnackBar(getString(R.string.select_state))
            } else if (!(viewModel.districtIdVending > 0)) {
                showSnackBar(getString(R.string.select_district))
            } else if (!(viewModel.panchayatIdVending > 0)) {
                showSnackBar(getString(R.string.municipality_panchayat))
            } else if (editTextVendingAddress.text.toString().isEmpty()) {
                showSnackBar(getString(R.string.address_mention_village))
            } else if (binding.ivRdLocalOrgnaizationYes.isChecked == true && editTextLocalOrganisation.text.toString()
                    .isEmpty()
            ) {
                showSnackBar(getString(R.string.localOrganisation))
            } else if (viewModel.data.shopImage == null) {
                showSnackBar(getString(R.string.shop_imageStar))
            } else {
                val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(user_role, USER_TYPE)

                if (viewModel.data.vendor_first_name != null) {
                    requestBody.addFormDataPart(
                        vendor_first_name,
                        viewModel.data.vendor_first_name!!
                    )
                }
                if (viewModel.data.vendor_last_name != null) {
                    requestBody.addFormDataPart(
                        vendor_last_name,
                        viewModel.data.vendor_last_name!!
                    )
                }
                if (viewModel.data.parent_first_name != null) {
                    requestBody.addFormDataPart(
                        parent_first_name,
                        viewModel.data.parent_first_name!!
                    )
                }
                if (viewModel.data.parent_last_name != null) {
                    requestBody.addFormDataPart(
                        parent_last_name,
                        viewModel.data.parent_last_name!!
                    )
                }
                if (viewModel.data.gender != null) {
                    requestBody.addFormDataPart(gender, viewModel.data.gender!!)
                }
                if (viewModel.data.date_of_birth != null) {
                    requestBody.addFormDataPart(date_of_birth, viewModel.data.date_of_birth!!)
                }
                if (viewModel.data.social_category != null) {
                    requestBody.addFormDataPart(social_category, viewModel.data.social_category!!)
                }
                if (viewModel.data.education_qualification != null) {
                    requestBody.addFormDataPart(
                        education_qualification,
                        viewModel.data.education_qualification!!
                    )
                }
                if (viewModel.data.marital_status != null) {
                    requestBody.addFormDataPart(marital_status, viewModel.data.marital_status!!)
                }
                if (viewModel.data.spouse_name != null) {
                    requestBody.addFormDataPart(spouse_name, viewModel.data.spouse_name!!)
                }
                if (viewModel.data.current_state != null) {
                    requestBody.addFormDataPart(residential_state, viewModel.data.current_state!!)
                }
                if (viewModel.data.current_district != null) {
                    requestBody.addFormDataPart(
                        residential_district,
                        viewModel.data.current_district!!
                    )
                }
                if (viewModel.data.municipality_panchayat_current != null) {
                    requestBody.addFormDataPart(
                        residential_municipality_panchayat,
                        viewModel.data.municipality_panchayat_current!!
                    )
                }
                if (viewModel.data.current_pincode != null) {
                    requestBody.addFormDataPart(
                        residential_pincode,
                        viewModel.data.current_pincode!!
                    )
                }
                if (viewModel.data.current_address != null) {
                    requestBody.addFormDataPart(
                        residential_address,
                        viewModel.data.current_address!!
                    )
                }
                if (viewModel.data.passportSizeImage != null && (!viewModel.data.passportSizeImage!!.startsWith(
                        "http"
                    ))
                ) {
                    requestBody.addFormDataPart(
                        profile_image_name,
                        File(viewModel.data.passportSizeImage!!).name,
                        File(viewModel.data.passportSizeImage!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.identificationImage != null && (!viewModel.data.identificationImage!!.startsWith(
                        "http"
                    ))
                ) {
                    requestBody.addFormDataPart(
                        identity_image_name,
                        File(viewModel.data.identificationImage!!).name,
                        File(viewModel.data.identificationImage!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }


                if (viewModel.data.type_of_marketplace != null) {
                    requestBody.addFormDataPart(
                        type_of_marketplace,
                        viewModel.data.type_of_marketplace!!
                    )
                }
                // if(viewModel.data.marketpalce_others  != null){
                viewModel.data.marketpalce_others =
                    "" + editTextTypeofMarketPlaceEnter.text.toString()
                requestBody.addFormDataPart(
                    marketpalce_others,
                    viewModel.data.marketpalce_others!!
                )
                // }
//                Log.e("TAG", "type_of_vendingAA "+viewModel.data.type_of_vending!!)
                if (viewModel.data.type_of_vending != null) {
                    requestBody.addFormDataPart(type_of_vending, viewModel.data.type_of_vending!!)
                }

//                if(viewModel.data.vending_others  != null){
                viewModel.data.vending_others = "" + editTextTypeofVendingEnter.text.toString()
                requestBody.addFormDataPart(vending_others, viewModel.data.vending_others!!)
//                }
                if (viewModel.data.total_years_of_business != null) {
                    requestBody.addFormDataPart(
                        total_years_of_business,
                        viewModel.data.total_years_of_business!!
                    )
                }
                if (viewModel.data.open != null) {
                    requestBody.addFormDataPart(vending_time_from, viewModel.data.open!!)
                }
                if (viewModel.data.close != null) {
                    requestBody.addFormDataPart(vending_time_to, viewModel.data.close!!)
                }

                if (viewModel.data.vending_state != null) {
                    requestBody.addFormDataPart(vending_state, viewModel.data.vending_state!!)
                }
                if (viewModel.data.vending_district != null) {
                    requestBody.addFormDataPart(
                        vending_district,
                        viewModel.data.vending_district!!
                    )
                }
                if (viewModel.data.vending_municipality_panchayat != null) {
                    requestBody.addFormDataPart(
                        vending_municipality_panchayat,
                        viewModel.data.vending_municipality_panchayat!!
                    )
                }
                if (viewModel.data.vending_pincode != null) {
                    requestBody.addFormDataPart(vending_pincode, viewModel.data.vending_pincode!!)
                }

                viewModel.data.vending_address = "" + editTextVendingAddress.text.toString()
                if (viewModel.data.vending_address != null) {
                    requestBody.addFormDataPart(vending_address, viewModel.data.vending_address!!)
                }

                requestBody.addFormDataPart(
                    local_organisation,
                    "" + viewModel.data.localOrganisation
                )



                docs =
                    stringCOV + stringSurveyReceipt + stringLOR + stringChallan + stringApprovalLetter
                viewModel.data.vending_documents = docs
                if (!docs.toString().isEmpty()) {
                    requestBody.addFormDataPart(vending_documents, docs.toString())
                } else {
                    requestBody.addFormDataPart(vending_documents, "null")
                }

                if (inclideGovernment.cbRememberOthersPleaseName.isChecked) {
                    stringOtherSchemeName = inclideGovernment.editTextSchemeName.text.toString()
                } else {
                    stringOtherSchemeName = ""
                }
                scheme = stringPm_swanidhi_schemeSingle + stringOtherSchemeName
                viewModel.data.schemeName = scheme

                if (viewModel.data.governmentScheme == true) {
                    if (!viewModel.data.schemeName!!.isEmpty()) {
                        requestBody.addFormDataPart(availed_scheme, viewModel.data.schemeName!!)
                    } else {
                        requestBody.addFormDataPart(availed_scheme, "null")
                    }
                } else {
                    requestBody.addFormDataPart(availed_scheme, "null")
                }





                if (viewModel.data.shopImage != null && (!viewModel.data.shopImage!!.startsWith("http"))) {
                    requestBody.addFormDataPart(
                        shop_image,
                        File(viewModel.data.shopImage!!).name,
                        File(viewModel.data.shopImage!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.ImageUploadCOV != null && (!viewModel.data.ImageUploadCOV!!.startsWith(
                        "http"
                    ))
                ) {
                    requestBody.addFormDataPart(
                        cov_image,
                        File(viewModel.data.ImageUploadCOV!!).name,
                        File(viewModel.data.ImageUploadCOV!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.ImageUploadLOR != null && (!viewModel.data.ImageUploadLOR!!.startsWith(
                        "http"
                    ))
                ) {
                    requestBody.addFormDataPart(
                        lor_image,
                        File(viewModel.data.ImageUploadLOR!!).name,
                        File(viewModel.data.ImageUploadLOR!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.UploadSurveyReceipt != null && (!viewModel.data.UploadSurveyReceipt!!.startsWith(
                        "http"
                    ))
                ) {
                    requestBody.addFormDataPart(
                        survey_receipt_image,
                        File(viewModel.data.UploadSurveyReceipt!!).name,
                        File(viewModel.data.UploadSurveyReceipt!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.UploadChallan != null && (!viewModel.data.UploadChallan!!.startsWith(
                        "http"
                    ))
                ) {
                    requestBody.addFormDataPart(
                        challan_image,
                        File(viewModel.data.UploadChallan!!).name,
                        File(viewModel.data.UploadChallan!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.UploadApprovalLetter != null && (!viewModel.data.UploadApprovalLetter!!.startsWith(
                        "http"
                    ))
                ) {
                    requestBody.addFormDataPart(
                        approval_letter_image,
                        File(viewModel.data.UploadApprovalLetter!!).name,
                        File(viewModel.data.UploadApprovalLetter!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

//                Log.e("TAG", "viewModel.dataAll22 "+viewModel.data.toString())

                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val data = Gson().fromJson(loginUser, Login::class.java)
                        if (networkFailed) {
                            viewModel.profileUpdate(
                                view = requireView(),
                                "" + data.id,
                                requestBody.build()
                            )
                        } else {
                            requireContext().callNetworkDialog()
                        }
                    }
                }
            }
        }

    }

}
