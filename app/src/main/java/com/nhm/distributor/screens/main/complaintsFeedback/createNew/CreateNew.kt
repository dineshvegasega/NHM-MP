package com.nhm.distributor.screens.main.complaintsFeedback.createNew

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.nhm.distributor.R
import com.nhm.distributor.databinding.CreateNewBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.getCameraPath
import com.nhm.distributor.utils.getMediaFilePathFor
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.showOptions
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class CreateNew : Fragment() {
    private val viewModel: CreateNewVM by viewModels()
    private var _binding: CreateNewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateNewBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.complaintsSlashfeedback)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            btCancel.singleClick {
               findNavController().navigateUp()
            }

            editTextSelectYourChoice.singleClick {
                requireActivity().showDropDownDialog(type = 16){
                    binding.editTextSelectYourChoice.setText(name)
                    when(position){
                        0-> {
                            viewModel.type = "complaint"
                            binding.textSubjectOfComplaintTxt.text = getString(R.string.subject_of_complaint)
                            binding.textTypeTxt.visibility = View.VISIBLE
                            binding.editTextSelectComplaintType.visibility = View.VISIBLE
                        }
                        1-> {
                            viewModel.type = "feedback"
                            binding.textSubjectOfComplaintTxt.text = getString(R.string.subject_of_feedback)
                            binding.textTypeTxt.visibility = View.GONE
                            binding.editTextSelectComplaintType.visibility = View.GONE
                        }
                    }
                }
            }

            if(networkFailed) {
                viewModel.complaintType(view)
            } else {
                requireContext().callNetworkDialog()
            }

            editTextSelectComplaintType.singleClick {
                if(viewModel.itemComplaintType.size > 0){
                    var index = 0
                    val list = arrayOfNulls<String>(viewModel.itemComplaintType.size)
                    for (value in viewModel.itemComplaintType) {
                        list[index] = value.name
                        index++
                    }
                    requireActivity().showDropDownDialog(type = 17, list){
                        binding.editTextSelectComplaintType.setText(name)
                        viewModel.complaintTypeId =  viewModel.itemComplaintType[position].id
                    }
                } else {
                    showSnackBar(getString(R.string.not_complaint_type))
                }
            }

            btUploadMedia.singleClick {
                imagePosition = 1
                isFree = true
                callMediaPermissions()
            }

            btSubmit.singleClick {
                if(editTextSubjectOfComplaint.text.toString().isEmpty()){
                    if (viewModel.type == "complaint"){
                        showSnackBar(getString(R.string.subject_of_complaint))
                    } else if (viewModel.type == "feedback"){
                        showSnackBar(getString(R.string.subject_of_feedback))
                    }
                } else if (viewModel.type == "complaint" && viewModel.complaintTypeId == 0){
                    showSnackBar(getString(R.string.select_complaint_type))
                } else if (editTextYourName.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.your_full_name))
                } else if (editTextYourMobileNumber.text.toString().isEmpty() || editTextYourMobileNumber.text.toString().length != 10){
                    showSnackBar(getString(R.string.your_mobile_number))
                } else if (editTextTypeHere.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.description))
//                } else if (viewModel.uploadMediaImage == null){
//                    showSnackBar(getString(R.string.upload_media))
                } else {
//                    Log.e("TAG", "typeXX "+viewModel.type)
                    val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(user_role, USER_TYPE)
                    requestBody.addFormDataPart(type, viewModel.type)
                    requestBody.addFormDataPart(subject, editTextSubjectOfComplaint.text.toString())
                    if (viewModel.type == "complaint" && viewModel.complaintTypeId != 0){
                        requestBody.addFormDataPart(complaint_type, ""+viewModel.complaintTypeId)
                    }
                    requestBody.addFormDataPart(name, editTextYourName.text.toString())
                    requestBody.addFormDataPart(mobile_number, editTextYourMobileNumber.text.toString())
                    requestBody.addFormDataPart(message, editTextTypeHere.text.toString())
                    if(viewModel.uploadMediaImage != null){
                        requestBody.addFormDataPart(
                            media,
                            File(viewModel.uploadMediaImage!!).name,
                            File(viewModel.uploadMediaImage!!).asRequestBody("image/*".toMediaTypeOrNull())
                        )
                    }
                    requestBody.addFormDataPart(status, "pending")
                    readData(LOGIN_DATA) { loginUser ->
                        if (loginUser != null) {
                           val user = Gson().fromJson(loginUser, Login::class.java)
                            requestBody.addFormDataPart(user_id, ""+user?.id)
                            requestBody.addFormDataPart(state_id, ""+user.residential_state?.id)
                            requestBody.addFormDataPart(district_id, ""+user.residential_district?.id)
                            requestBody.addFormDataPart(municipality_id, ""+user.residential_municipality_panchayat?.id)
                            if(user?.residential_state?.id != null){
                                if(networkFailed) {
                                    viewModel.newFeedback(view = requireView(), requestBody.build())
                                } else {
                                    requireContext().callNetworkDialog()
                                }
                            } else {
                                showSnackBar(resources.getString(R.string.need_to_add_complete_profile))
                            }
                        }
                    }
                }
            }
        }
    }




    var imagePosition = 0
    private var pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        lifecycleScope.launch {
            if (uri != null) {
                when (imagePosition) {
                    1 -> {
                        val compressedImageFile = Compressor.compress(requireContext(), File(requireContext().getMediaFilePathFor(uri)))
                        viewModel.uploadMediaImage = compressedImageFile.path
                        binding.textViewUploadMedia.setText(File(viewModel.uploadMediaImage!!).name)
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
                        viewModel.uploadMediaImage = compressedImageFile.path
                        binding.textViewUploadMedia.setText(compressedImageFile.name)
                    }
                }
            }
        }
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


    var isFree = false
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) {
                    if(isFree){
                        requireActivity().showOptions {
                            when(this){
                                1 -> forCamera()
                                2 -> forGallery()
                            }
                        }
                    }
                    isFree = false
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
        requireActivity().runOnUiThread(){
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}