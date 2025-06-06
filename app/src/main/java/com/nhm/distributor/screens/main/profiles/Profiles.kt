package com.nhm.distributor.screens.main.profiles

import android.Manifest
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ProfilesBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.utils.callPermissionDialog
import com.nhm.distributor.utils.getCameraPath
import com.nhm.distributor.utils.getMediaFilePathFor
import com.nhm.distributor.utils.loadImage
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.showOptions
import com.nhm.distributor.utils.singleClick
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class Profiles : Fragment() {
    private val viewModel: ProfilesVM by activityViewModels()
    private var _binding: ProfilesBinding? = null
    private val binding get() = _binding!!

//    companion object{
//        var callBackListener: CallBackListener? = null
//        var tabPosition = 0
//    }

    lateinit var adapter : ProfilePagerAdapter

    var data : Login ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfilesBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
//        callBackListener = this

        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.your_Profile)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            inclideHeaderSearch.textHeaderEditTxt.visibility = View.GONE
            inclideHeaderSearch.textHeaderEditTxt.singleClick {
                inclideHeaderSearch.textHeaderEditTxt.visibility = View.INVISIBLE
                btSave.visibility = View.VISIBLE
                btCancel.visibility = View.VISIBLE
                viewModel.isEditable.value = true
            }

            btSave.singleClick {
//                checkValidationClick()
            }


            btCancel.singleClick {
                inclideHeaderSearch.textHeaderEditTxt.visibility = View.VISIBLE
                btSave.visibility = View.GONE
                btCancel.visibility = View.GONE
                viewModel.isEditable.value = false
            }

            inclideHeaderSearch.btNominee.singleClick {
                view.findNavController().navigate(R.id.action_profiles_to_nomineeDetails)
            }

            editTextGender.singleClick {
                requireActivity().showDropDownDialog(type = 1){
                    binding.editTextGender.setText(name)
                    when (position) {
                        0 -> viewModel.data.gender = getString(R.string.maleGender)
                        1 -> viewModel.data.gender = getString(R.string.femaleGender)
                        2 -> viewModel.data.gender = getString(R.string.otherGender)
                    }
                }
            }

            readData(LOGIN_DATA) { loginUser ->
                if (loginUser != null) {
//                    Log.e("TAG", "loginUser "+loginUser)
                    data = Gson().fromJson(loginUser, Login::class.java)

                    editTextFN.setText(data!!.vendor_first_name)
                    editTextLN.setText(data!!.vendor_last_name)
                    data!!.profile_image_name?.let {
                        ivProfileImage.loadImage(type = 1, url = { data!!.profile_image_name.url })
                    }
                    editMobile.setText(data!!.mobile_no)
                    editTextGender.setText(data!!.gender)
                    val listGender = resources.getStringArray(R.array.gender_array)
                    data?.gender.let{
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
                            "पुरुष" -> {
                                editTextGender.setText(listGender[0])
                            }
                            "महिला" -> {
                                editTextGender.setText(listGender[1])
                            }
                            "अन्य" -> {
                                editTextGender.setText(listGender[2])
                            }
                        }
                    }
                    editTextAadhaarNumber.setText(data!!.aadhar_card)
                    editTextAddress.setText(data!!.residential_address)
                    data!!.aadhar_card_doc?.let {
                        ivImageAadhaarImage.loadImage(type = 1, url = { data!!.aadhar_card_doc.url })
                    }


                    if(data!!.user_role == "member"){
                        textGenderTxt.visibility = View.GONE
                        editTextGender.visibility = View.GONE
                    }
                    if(data!!.user_role == "controller"){
                        textGenderTxt.visibility = View.VISIBLE
                        editTextGender.visibility = View.VISIBLE
                    }


                    viewModel.isEditable.value = false
                    fieldsEdit()
                }
            }
        }
    }




    private fun fieldsEdit() {
        binding.apply {
            viewModel.isEditable.observe(viewLifecycleOwner, Observer {
                editTextFN.isEnabled = it
                editTextLN.isEnabled = it
                editMobile.isEnabled = it
                editTextGender.isEnabled = it
                editTextAadhaarNumber.isEnabled = it
                editTextAddress.isEnabled = it

                ivProfileImage.isEnabled = true
                btnProfileImage.isEnabled = true
                ivImageAadhaarImage.isEnabled = true
                btnImageAadhaarImage.isEnabled = true

                if (it == true){
                    btnProfileImage.visibility = View.VISIBLE
                    btnImageAadhaarImage.visibility = View.VISIBLE
                    ivProfileImage.singleClick {
                        imagePosition = 1
                        callMediaPermissions()
                    }

                    ivImageAadhaarImage.singleClick {
                        imagePosition = 2
                        callMediaPermissions()
                    }
                }
                lateinit var viewer: StfalconImageViewer<String>

                if (it == false){
                    data!!.profile_image_name?.let {
                        ivProfileImage.loadImage(type = 1, url = { data!!.profile_image_name.url })
                    }
                    data!!.aadhar_card_doc?.let {
                        ivImageAadhaarImage.loadImage(type = 1, url = { data!!.aadhar_card_doc.url })
                    }
                    btnProfileImage.visibility = View.GONE
                    btnImageAadhaarImage.visibility = View.GONE
                    ivProfileImage.singleClick {
//                        data!!.profile_image_name.url.let {
//                            arrayListOf(it).imageZoom(ivProfileImage, 2)
//                        }
                        viewer = StfalconImageViewer.Builder<String>(requireActivity(), arrayListOf(data!!.profile_image_name.url)) { view, image ->
                            Picasso.get().load(image).into(view)
                        }.withImageChangeListener {
                            viewer.updateTransitionImage(ivProfileImage)
                        }
                            .withBackgroundColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color._D9000000
                                )
                            )
                            .show()
                    }
                    ivImageAadhaarImage.singleClick {
//                        data!!.aadhar_card_doc.url.let {
//                            arrayListOf(it).imageZoom(ivImageAadhaarImage, 2)
//                        }
                        viewer = StfalconImageViewer.Builder<String>(requireActivity(), arrayListOf(data!!.aadhar_card_doc.url)) { view, image ->
                            Picasso.get().load(image).into(view)
                        }.withImageChangeListener {
                            viewer.updateTransitionImage(ivImageAadhaarImage)
                        }
                            .withBackgroundColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color._D9000000
                                )
                            )
                            .show()
                    }
                }
            })
        }
    }






    var imagePosition = 0
    @SuppressLint("MissingPermission")
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
                            viewModel.data.profile_image_name = compressedImageFile.path
//                            binding.textViewPassportSizeImage.setText(File(viewModel.data.profile_image_name!!).name)
                            binding.ivProfileImage.loadImage(type = 1, url = { viewModel.data.profile_image_name!! })

                        }
                        2 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
                            viewModel.data.aadhar_card_doc = compressedImageFile.path
//                            binding.textViewAadhaarImage.setText(File(viewModel.data.aadhar_card_doc!!).name)
                            binding.ivImageAadhaarImage.loadImage(type = 1, url = { viewModel.data.aadhar_card_doc!! })
                        }
                    }
                }
            }
        }


    var uriReal: Uri? = null
    @SuppressLint("MissingPermission")
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        lifecycleScope.launch {
            if (uri == true) {

                when (imagePosition) {
                    1 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
                        viewModel.data.profile_image_name = compressedImageFile.path
//                        binding.textViewPassportSizeImage.setText(File(viewModel.data.profile_image_name!!).name)
                        binding.ivProfileImage.loadImage(type = 1, url = { viewModel.data.profile_image_name!! })
                    }
                    2 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )

                        viewModel.data.aadhar_card_doc = compressedImageFile.path
//                        binding.textViewAadhaarImage.setText(compressedImageFile.name)
                        binding.ivImageAadhaarImage.loadImage(type = 1, url = { viewModel.data.aadhar_card_doc!! })
                    }
                }
            }
        }
    }


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
                    when (this) {
                        1 -> forCamera()
                        2 -> forGallery()
                    }
                }
            } else {
                requireActivity().callPermissionDialog {
                    someActivityResultLauncher.launch(this)
                }
            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
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



}