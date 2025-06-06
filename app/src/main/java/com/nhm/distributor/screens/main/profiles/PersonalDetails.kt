package com.nhm.distributor.screens.main.profiles

import android.Manifest
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.nhm.distributor.R
import com.nhm.distributor.databinding.PersonalDetailsBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.interfaces.CallBackListener
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
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
import java.io.File

@AndroidEntryPoint
class PersonalDetails : Fragment() , CallBackListener {
    private val viewModel: ProfilesVM by activityViewModels()
    private var _binding: PersonalDetailsBinding? = null
    private val binding get() = _binding!!


    companion object{
        var callBackListener: CallBackListener? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PersonalDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }



    private fun callMediaPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            )
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES)
            )
        } else{
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            if(!permissions.entries.toString().contains("false")){
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
    private var pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        lifecycleScope.launch {
            if (uri != null) {
                when (imagePosition) {
                    1 -> {
                        val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uri)))
                        viewModel.data.passportSizeImage = compressedImageFile.path
                        binding.ivImagePassportsizeImage.loadImage(type = 1, url = { viewModel.data.passportSizeImage!! })
                    }
                    2 -> {
                        val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uri)))
                        viewModel.data.identificationImage = compressedImageFile.path
                        binding.ivImageIdentityImage.loadImage(type = 1, url = { viewModel.data.identificationImage!! })
                    }
                }
            }
        }
    }


    var uriReal : Uri?= null
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        lifecycleScope.launch {
            if (uri == true) {
                when (imagePosition) {
                    1 -> {
                        val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uriReal!!)))
                        viewModel.data.passportSizeImage = compressedImageFile.path
                        binding.ivImagePassportsizeImage.loadImage(type = 1, url = { viewModel.data.passportSizeImage!! })
                    }
                    2 -> {
                        val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uriReal!!)))
                        viewModel.data.identificationImage = compressedImageFile.path
                        binding.ivImageIdentityImage.loadImage(type = 1, url = { viewModel.data.identificationImage!! })
                    }
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBackListener = this

        binding.apply {

            fieldsEdit()

            readData(LOGIN_DATA) { loginUser ->
                if (loginUser != null) {
                    val data = Gson().fromJson(loginUser, Login::class.java)
                    editTextFN.setText(data.vendor_first_name)
                    editTextLN.setText(data.vendor_last_name)
                    editTextFatherFN.setText(data.parent_first_name)
                    editTextFatherLN.setText(data.parent_last_name)
                    viewModel.data.gender = data.gender
                    viewModel.data.date_of_birth = data.date_of_birth
                    viewModel.data.marital_status = data.marital_status
                    viewModel.data.spouse_name = data.spouse_name
                    viewModel.data.social_category = data.social_category
                    viewModel.data.education_qualification = data.education_qualification

                    data.profile_image_name?.let {
                        ivImagePassportsizeImage.loadImage(type = 1, url = { data.profile_image_name.url })
                        viewModel.data.passportSizeImage = data.profile_image_name.url
                    }

                    data.identity_image_name?.let {
                        ivImageIdentityImage.loadImage(type = 1, url = { data.identity_image_name.url })
                        viewModel.data.identificationImage = data.identity_image_name.url
                    }


                    val listGender = resources.getStringArray(R.array.gender_array)
                    data.gender?.let{
                        when(it){
                            "Male" -> {
                               editTextGender.setText(listGender[0])
                            }
                            "Female" -> {
                                editTextGender.setText(listGender[1])
                            }
                            "Other" -> {
                                editTextGender.setText(listGender[2])
                            }
                        }
                    }

                    editTextDateofBirth.setText(data.date_of_birth)

                    val listMaritalStatus = resources.getStringArray(R.array.maritalStatus_array)
                    data.marital_status?.let{
                        when(it){
                            "Single" -> {
                                editTextMaritalStatus.setText(listMaritalStatus[0])
                                textSpouseNameTxt.visibility = View.GONE
                                editTextSpouseName.visibility = View.GONE
                            }
                            "Married" -> {
                                editTextMaritalStatus.setText(listMaritalStatus[1])
                                editTextSpouseName.setText("${data.spouse_name}")
                                textSpouseNameTxt.visibility = View.VISIBLE
                                editTextSpouseName.visibility = View.VISIBLE
                            }
                            "Widowed" -> {
                                editTextMaritalStatus.setText(listMaritalStatus[2])
                                textSpouseNameTxt.visibility = View.GONE
                                editTextSpouseName.visibility = View.GONE
                            }
                            "Divorced" -> {
                                editTextMaritalStatus.setText(listMaritalStatus[3])
                                textSpouseNameTxt.visibility = View.GONE
                                editTextSpouseName.visibility = View.GONE
                            }
                            "Separated" -> {
                                editTextMaritalStatus.setText(listMaritalStatus[4])
                                textSpouseNameTxt.visibility = View.GONE
                                editTextSpouseName.visibility = View.GONE
                            }
                        }
                    }

                    editTextSocialCategory.setText(data.social_category)

                    val listEducation = resources.getStringArray(R.array.socialEducation_array)
                    data.education_qualification?.let{
                        when(it){
                            "No Education" -> {
                                editTextEducationQualifacation.setText(listEducation[0])
                            }
                            "Primary Education(1st To 5th)" -> {
                                editTextEducationQualifacation.setText(listEducation[1])
                            }
                            "Middle Education(6th To 9th)" -> {
                                editTextEducationQualifacation.setText(listEducation[2])
                            }
                            "Higher Education(10th To 12th)" -> {
                                editTextEducationQualifacation.setText(listEducation[3])
                            }
                            "Graduation" -> {
                                editTextEducationQualifacation.setText(listEducation[4])
                            }
                            "Post Graduation" -> {
                                editTextEducationQualifacation.setText(listEducation[5])
                            }
                        }
                    }

                    if (IS_LANGUAGE_ALL){
                        runBlocking {
                            mainThread {
                                data.residential_state?.let {
                                    if (MainActivity.context.get()!!
                                            .getString(R.string.englishVal) == "" + locale
                                    ) {
                                        editTextSelectState.setText("${data.residential_state.name}")
                                    } else {
                                        viewModel.show()
                                        val nameChanged: String =
                                            viewModel.callApiTranslate("" + locale, data.residential_state.name)
                                        editTextSelectState.setText("${nameChanged}")
                                        viewModel.hide()
                                    }
                                }

                                data.residential_district?.let {
                                    if (MainActivity.context.get()!!
                                            .getString(R.string.englishVal) == "" + locale
                                    ) {
                                        editTextSelectDistrict.setText("${data.residential_district.name}")
                                    } else {
                                        viewModel.show()
                                        val nameChanged: String =
                                            viewModel.callApiTranslate("" + locale, data.residential_district.name)
                                        editTextSelectDistrict.setText("${nameChanged}")
                                        viewModel.hide()
                                    }
                                }

                                data.residential_municipality_panchayat?.let {
                                    if (MainActivity.context.get()!!
                                            .getString(R.string.englishVal) == "" + locale
                                    ) {
                                        editTextMunicipalityPanchayat.setText("${data.residential_municipality_panchayat.name}")
                                    } else {
                                        viewModel.show()
                                        val nameChanged: String =
                                            viewModel.callApiTranslate("" + locale, data.residential_municipality_panchayat.name)
                                        editTextMunicipalityPanchayat.setText("${nameChanged}")
                                        viewModel.hide()
                                    }

                                }
                            }
                        }
                    } else {
                        data.residential_state?.let {
                            editTextSelectState.setText("${data.residential_state.name}")
                        }
                        data.residential_district?.let {
                            editTextSelectDistrict.setText("${data.residential_district.name}")
                        }
                        data.residential_municipality_panchayat?.let {
                            editTextMunicipalityPanchayat.setText("${data.residential_municipality_panchayat.name}")
                        }
                    }






                    if(data.residential_pincode != null){
                        editTextSelectPincode.setText(""+data.residential_pincode.pincode.toInt())
                    } else {
                        editTextSelectPincode.setText("")
                    }

                    data.residential_address?.let {
                        editTextAddress.setText("${data.residential_address}")
                    }

                    data.residential_state?.let {
                        viewModel.data.current_state = ""+data.residential_state.id
                    }
                    data.residential_district?.let {
                        viewModel.data.current_district = ""+data.residential_district.id
                    }
                    data.residential_municipality_panchayat?.let {
                        viewModel.data.municipality_panchayat_current = ""+data.residential_municipality_panchayat.id
                    }

                    if(data.residential_pincode != null){
                        viewModel.data.current_pincode = ""+data.residential_pincode.pincode
                    } else {
                        viewModel.data.current_pincode = "0"
                    }
                    viewModel.data.current_address = ""+data.residential_address


                    data.residential_state?.let {
                        viewModel.stateId = data.residential_state.id
                    }
                    data.residential_district?.let {
                        viewModel.districtId = data.residential_district.id
                    }
                    data.residential_municipality_panchayat?.let {
                        viewModel.panchayatId = data.residential_municipality_panchayat.id
                    }

                    if(data.residential_pincode != null){
                        viewModel.pincodeId = data.residential_pincode.pincode
                    } else {
                        viewModel.pincodeId = ""
                    }
                }
            }


            btnImagePassportsize.singleClick {
                imagePosition = 1
                callMediaPermissions()
            }
            btnIdentityImage.singleClick {
                imagePosition = 2
                callMediaPermissions()
            }


            if(requireContext().isNetworkAvailable()) {
                viewModel.state(view)
            } else {
                requireContext().callNetworkDialog()
            }

            editTextSelectState.singleClick {
                var index = 0
                val list = arrayOfNulls<String>(viewModel.itemState.size)
                for (value in viewModel.itemState) {
                    list[index] = value.name
                    index++
                }
                requireActivity().showDropDownDialog(type = 6, arrayList = list){
                    binding.editTextSelectState.setText(name)
                    viewModel.stateId =  viewModel.itemState[position].id
                    if(networkFailed) {
                        view?.let { viewModel.district(it, viewModel.stateId) }
                        if (!IS_LANGUAGE_ALL){
                            view?.let { viewModel.panchayat(it, viewModel.stateId) }
                        }
                    } else {
                        requireContext().callNetworkDialog()
                    }
                    viewModel.data.current_state = ""+viewModel.stateId
                    binding.editTextSelectDistrict.setText("")
                    binding.editTextMunicipalityPanchayat.setText("")
                    viewModel.districtId = 0
                    viewModel.panchayatId = 0
                }
            }

            editTextSelectDistrict.singleClick {
                if (!(viewModel.stateId > 0)){
                    showSnackBar(getString(R.string.select_state_))
                }else{
                    if(viewModel.itemDistrict.size > 0){
                        var index = 0
                        val list = arrayOfNulls<String>(viewModel.itemDistrict.size)
                        for (value in viewModel.itemDistrict) {
                            list[index] = value.name
                            index++
                        }
                        requireActivity().showDropDownDialog(type = 7, arrayList = list){
                            binding.editTextSelectDistrict.setText(name)
                            viewModel.districtId = viewModel.itemDistrict[position].id
                            if (networkFailed) {
                                view?.let { viewModel.pincode(it, viewModel.districtId) }
                            } else {
                                requireContext().callNetworkDialog()
                            }
                            viewModel.data.current_district = ""+viewModel.districtId
                            binding.editTextSelectPincode.setText("")
                            viewModel.pincodeId = ""
                        }
                    } else {
                        showSnackBar(getString(R.string.not_district))
                    }
                }
            }

            editTextMunicipalityPanchayat.singleClick {
                if (!(viewModel.stateId> 0)){
                    showSnackBar(getString(R.string.select_state_))
                }else{
                    if(viewModel.itemPanchayat.size > 0){
                        var index = 0
                        val list = arrayOfNulls<String>(viewModel.itemPanchayat.size)
                        for (value in viewModel.itemPanchayat) {
                            list[index] = value.name
                            index++
                        }
                        requireActivity().showDropDownDialog(type = 8, arrayList = list){
                            binding.editTextMunicipalityPanchayat.setText(name)
                            viewModel.panchayatId = viewModel.itemPanchayat[position].id
                            viewModel.data.municipality_panchayat_current = ""+viewModel.panchayatId
                        }
                    } else {
                        showSnackBar(getString(R.string.not_municipality_panchayat))
                    }
                }
            }

            editTextSelectPincode.singleClick {
                if (!(viewModel.districtId > 0)){
                    showSnackBar(getString(R.string.select_district_))
                } else {
                    if(viewModel.itemPincode.size > 0){
                        var index = 0
                        val list = arrayOfNulls<String>(viewModel.itemPincode.size)
                        for (value in viewModel.itemPincode) {
                            list[index] = value.pincode
                            index++
                        }
                        requireActivity().showDropDownDialog(type = 9, arrayList = list){
                            binding.editTextSelectPincode.setText(name)
                            viewModel.pincodeId = binding.editTextSelectPincode.text.toString()
                            viewModel.data.current_pincode = ""+viewModel.pincodeId
                        }
                    } else {
                        showSnackBar(getString(R.string.not_pincode))
                    }
                }
            }



            editTextGender.singleClick {
                requireActivity().showDropDownDialog(type = 1){
                    binding.editTextGender.setText(name)
                    when (position) {
                        0 -> viewModel.data.gender = "Male"
                        1 -> viewModel.data.gender = "Female"
                        2 -> viewModel.data.gender = "Other"
                    }
                }
            }

            editTextDateofBirth.singleClick {
                requireActivity().showDropDownDialog(type = 2){
                    binding.editTextDateofBirth.setText(name)
                    viewModel.data.date_of_birth = if(name != "") name else null
                }
            }

            editTextSocialCategory.singleClick {
                requireActivity().showDropDownDialog(type = 3){
                    binding.editTextSocialCategory.setText(name)
                }
            }

            editTextEducationQualifacation.singleClick {
                requireActivity().showDropDownDialog(type = 4){
                    binding.editTextEducationQualifacation.setText(name)
                    when (position) {
                        0 -> viewModel.data.education_qualification = "No Education"
                        1 -> viewModel.data.education_qualification = "Primary Education(1st To 5th)"
                        2 -> viewModel.data.education_qualification = "Middle Education(6th To 9th)"
                        3 -> viewModel.data.education_qualification = "Higher Education(10th To 12th)"
                        4 -> viewModel.data.education_qualification = "Graduation"
                        5 -> viewModel.data.education_qualification = "Post Graduation"
                    }
                }
            }

            editTextMaritalStatus.singleClick {
                requireActivity().showDropDownDialog(type = 5){
                    binding.editTextMaritalStatus.setText(name)
                    if (name == getString(R.string.married)) {
                        binding.textSpouseNameTxt.visibility = View.VISIBLE
                        binding.editTextSpouseName.visibility = View.VISIBLE
                    } else {
                        binding.textSpouseNameTxt.visibility = View.GONE
                        binding.editTextSpouseName.visibility = View.GONE
                        viewModel.data.spouse_name = null
                    }
                    when (position) {
                        0 -> viewModel.data.marital_status = "Single"
                        1 -> viewModel.data.marital_status = "Married"
                        2 -> viewModel.data.marital_status = "Widowed"
                        3 -> viewModel.data.marital_status = "Divorced"
                        4 -> viewModel.data.marital_status = "Separated"
                    }
                }
            }

        }


    }


    private fun fieldsEdit() {
        binding.apply {
            viewModel.isEditable.observe(viewLifecycleOwner, Observer {
                editTextFN.isEnabled = it
                editTextLN.isEnabled = it
                editTextFatherFN.isEnabled = it
                editTextFatherLN.isEnabled = it
                editTextGender.isEnabled = it
                editTextDateofBirth.isEnabled = it
                editTextMaritalStatus.isEnabled = it
                editTextSpouseName.isEnabled = it
                editTextSocialCategory.isEnabled = it
                editTextEducationQualifacation.isEnabled = it
                editTextSelectState.isEnabled = it
                editTextSelectDistrict.isEnabled = it
                editTextMunicipalityPanchayat.isEnabled = it
                editTextSelectPincode.isEnabled = it
                editTextAddress.isEnabled = it
                btnImagePassportsize.isEnabled = it
                btnIdentityImage.isEnabled = it
            })
        }
    }







    private fun forCamera() {
        requireActivity().getCameraPath {
            uriReal = this
            captureMedia.launch(uriReal)
        }
    }

    private fun forGallery() {
        requireActivity().runOnUiThread(){
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }



    override fun onCallBack(pos: Int) {
        if(pos == 1){
            update()
        }
    }





    private fun update() {
        binding.apply {
            if(editTextFN.text.toString().isEmpty()){
                showSnackBar(getString(R.string.first_name))
            } else if (editTextLN.text.toString().isEmpty()){
                showSnackBar(getString(R.string.last_name))
            } else if (editTextFatherFN.text.toString().isEmpty()){
                showSnackBar(getString(R.string.father_s_first_name))
            } else if (editTextFatherLN.text.toString().isEmpty()){
                showSnackBar(getString(R.string.father_s_last_name))
            } else if (editTextGender.text.toString().isEmpty()){
                showSnackBar(getString(R.string.gender))
            } else if (editTextDateofBirth.text.toString().isEmpty()){
                showSnackBar(getString(R.string.date_of_birth))
            } else if (editTextMaritalStatus.text.toString().isEmpty()){
                showSnackBar(getString(R.string.marital_status))
            } else if (editTextMaritalStatus.text.toString() == getString(R.string.married) && editTextSpouseName.text.toString().isEmpty()){
                showSnackBar(getString(R.string.spouse_s_name))
            } else if (editTextSocialCategory.text.toString().isEmpty()){
                showSnackBar(getString(R.string.social_category))
            } else if (editTextEducationQualifacation.text.toString().isEmpty()){
                showSnackBar(getString(R.string.education_qualifacation))
            } else if (!(viewModel.stateId > 0)){
                showSnackBar(getString(R.string.select_state))
            } else if (!(viewModel.districtId > 0)){
                showSnackBar(getString(R.string.select_district))
            } else if (!(viewModel.panchayatId > 0)){
                showSnackBar(getString(R.string.municipality_panchayat))
            } else if (editTextAddress.text.toString().isEmpty()){
                showSnackBar(getString(R.string.address_mention_village))
            } else if(viewModel.data.passportSizeImage == null){
                showSnackBar(getString(R.string.passport_size_imageStar))
            } else if(viewModel.data.identificationImage == null){
                showSnackBar(getString(R.string.identity_imageStar))
            } else {
                viewModel.data.vendor_first_name = editTextFN.text.toString()
                viewModel.data.vendor_last_name = editTextLN.text.toString()
                viewModel.data.parent_first_name = editTextFatherFN.text.toString()
                viewModel.data.parent_last_name = editTextFatherLN.text.toString()

                viewModel.data.spouse_name = editTextSpouseName.text.toString()

                if (viewModel.data.marital_status == "Married"){
                    viewModel.data.spouse_name = editTextSpouseName.text.toString()
                } else {
                    viewModel.data.spouse_name = null
                }

                viewModel.data.social_category = editTextSocialCategory.text.toString()
                viewModel.data.current_address = editTextAddress.text.toString()

                val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(user_role, USER_TYPE)
                if(viewModel.data.vendor_first_name  != null){
                    requestBody.addFormDataPart(vendor_first_name, viewModel.data.vendor_first_name!!)
                }
                if(viewModel.data.vendor_last_name  != null){
                    requestBody.addFormDataPart(vendor_last_name, viewModel.data.vendor_last_name!!)
                }
                if(viewModel.data.parent_first_name  != null){
                    requestBody.addFormDataPart(parent_first_name, viewModel.data.parent_first_name!!)
                }
                if(viewModel.data.parent_last_name  != null){
                    requestBody.addFormDataPart(parent_last_name, viewModel.data.parent_last_name!!)
                }
                if(viewModel.data.gender  != null){
                    requestBody.addFormDataPart(gender, viewModel.data.gender!!)
                }
                if(viewModel.data.date_of_birth  != null){
                    requestBody.addFormDataPart(date_of_birth, viewModel.data.date_of_birth!!)
                }
                if(viewModel.data.social_category  != null){
                    requestBody.addFormDataPart(social_category, viewModel.data.social_category!!)
                }
                if(viewModel.data.education_qualification  != null){
                    requestBody.addFormDataPart(education_qualification, viewModel.data.education_qualification!!)
                }
                if(viewModel.data.marital_status  != null){
                    requestBody.addFormDataPart(marital_status, viewModel.data.marital_status!!)
                }
                if(viewModel.data.spouse_name  != null){
                    requestBody.addFormDataPart(spouse_name, viewModel.data.spouse_name!!)
                }
                if(viewModel.data.current_state  != null){
                    requestBody.addFormDataPart(residential_state, viewModel.data.current_state!!)
                }
                if(viewModel.data.current_district  != null){
                    requestBody.addFormDataPart(residential_district, viewModel.data.current_district!!)
                }
                if(viewModel.data.municipality_panchayat_current  != null){
                    requestBody.addFormDataPart(residential_municipality_panchayat, viewModel.data.municipality_panchayat_current!!)
                }
                if(viewModel.data.current_pincode  != null){
                    requestBody.addFormDataPart(residential_pincode, viewModel.data.current_pincode!!)
                }
                if(viewModel.data.current_address  != null){
                    requestBody.addFormDataPart(residential_address, viewModel.data.current_address!!)
                }
                if(viewModel.data.passportSizeImage != null && (!viewModel.data.passportSizeImage!!.startsWith("http"))){
                    requestBody.addFormDataPart(
                        profile_image_name,
                        File(viewModel.data.passportSizeImage!!).name,
                        File(viewModel.data.passportSizeImage!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if(viewModel.data.identificationImage != null && (!viewModel.data.identificationImage!!.startsWith("http"))){
                    requestBody.addFormDataPart(
                        identity_image_name,
                        File(viewModel.data.identificationImage!!).name,
                        File(viewModel.data.identificationImage!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

//                Handler(Looper.getMainLooper()).postDelayed({
//                    Profiles.callBackListener!!.onCallBack(2)
//                }, 100)
            }
        }
    }



}
