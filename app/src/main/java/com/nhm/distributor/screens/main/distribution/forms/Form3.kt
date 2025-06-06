package com.nhm.distributor.screens.main.distribution.forms

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.nhm.distributor.databinding.Form3Binding
import com.nhm.distributor.screens.main.distribution.DistributionViewModel
import com.nhm.distributor.screens.onboarding.register.Register.Companion.imagePath
import com.nhm.distributor.utils.callPermissionDialog
import com.nhm.distributor.utils.getCameraPath
import com.nhm.distributor.utils.getMediaFilePathFor
import com.nhm.distributor.utils.showOptions
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class Form3 : Fragment() {
    private val viewModel: DistributionViewModel by viewModels()
    private var _binding: Form3Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = Form3Binding.inflate(inflater, container, false)
        return binding.root
    }

    var imagePosition = 0


    var uriReal: Uri? = null
    @SuppressLint("MissingPermission")
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        lifecycleScope.launch{
            if (uri == true) {

                when (imagePosition) {
                    1 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )

//                            viewModel.data.passportSizeImage = compressedImageFile.path
//                            binding.textViewPassportSizeImage.setText(compressedImageFile.name)
                        imagePath = compressedImageFile.path
                        Log.e("TAG", "addOnSuccessListenerRegisterAA " + imagePath.toString())

                    }

                    2 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
                        imagePath = compressedImageFile.path
                        Log.e("TAG", "addOnSuccessListenerRegisterBB " + imagePath.toString())

                    }
                }
            }
        }
    }






//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//
//    var imagePosition = 0
//    @SuppressLint("MissingPermission")
//    private var pickMedia =
//        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
//            lifecycleScope.launch @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
//                if (uri != null) {
//                    when (imagePosition) {
//                        1 -> {
//                            val compressedImageFile = Compressor.compress(
//                                requireContext(),
//                                File(requireContext().getMediaFilePathFor(uri))
//                            )
//                            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//                            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                                Log.e("TAG", "addOnSuccessListenerRegisterAA " + location.toString())
////                                viewModel.data.passportSizeImage = compressedImageFile.path
////                                binding.textViewPassportSizeImage.setText(File(viewModel.data.passportSizeImage!!).name)
//                                imagePath = compressedImageFile.path
//                                latLong = LatLng(location!!.latitude, location.longitude)
////                                binding.ivImagePassportsizeImage.loadImage(type = 1, url = { viewModel.data.passportSizeImage!! })
//                                binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
//                                binding.textAddressTxt.text = requireActivity().getAddress(latLong)
//                                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//                                val currentDate = sdf.format(Date())
//                                binding.textTimeTxt.text = currentDate
//                                mainThread {
//                                    dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                        viewModel.foodItemImage = this
//                                        Log.e("TAG", "viewModel.foodItemImage " + this)
//                                        binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
//                                    }
//                                }
//                            }
//                        }
//
//                        2 -> {
//                            val compressedImageFile = Compressor.compress(
//                                requireContext(),
//                                File(requireContext().getMediaFilePathFor(uri))
//                            )
//                            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//                            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                                Log.e("TAG", "addOnSuccessListenerRegisterBB " + location.toString())
////                                viewModel.data.identificationImage = compressedImageFile.path
////                                binding.textViewIdentificationImage.setText(File(viewModel.data.identificationImage!!).name)
//                                imagePath = compressedImageFile.path
//                                latLong = LatLng(location!!.latitude, location.longitude)
//
//                                binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
//                                binding.textAddressTxt.text = requireActivity().getAddress(latLong)
//                                val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//                                val currentDate = sdf.format(Date())
//                                binding.textTimeTxt.text = currentDate
//                                mainThread {
//                                    dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                        viewModel.foodItemImage = this
//                                        Log.e("TAG", "viewModel.foodItemImage " + this)
//                                        binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//        }


//    var uriReal: Uri? = null
//    @SuppressLint("MissingPermission")
//    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
//        lifecycleScope.launch{
//            if (uri == true) {
//
//
//
////
////                when (imagePosition) {
////                    1 -> {
////                        val compressedImageFile = Compressor.compress(
////                            requireContext(),
////                            File(requireContext().getMediaFilePathFor(uriReal!!))
////                        )
////                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
////                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
////                            Log.e("TAG", "addOnSuccessListenerRegisterAA " + location.toString())
////////                            viewModel.data.passportSizeImage = compressedImageFile.path
////////                            binding.textViewPassportSizeImage.setText(compressedImageFile.name)
//////                            imagePath = compressedImageFile.path
//////                            latLong = LatLng(location!!.latitude, location.longitude)
//////
//////                            binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
//////                            binding.textAddressTxt.text = requireActivity().getAddress(latLong)
//////                            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//////                            val currentDate = sdf.format(Date())
//////                            binding.textTimeTxt.text = currentDate
//////                            mainThread {
//////                                dispatchTakePictureIntent(binding.layoutMainCapture) {
//////                                    viewModel.foodItemImage = this
//////                                    Log.e("TAG", "viewModel.foodItemImage " + this)
//////                                    binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
//////                                }
//////                            }
////                        }
////                    }
////
////                    2 -> {
////                        val compressedImageFile = Compressor.compress(
////                            requireContext(),
////                            File(requireContext().getMediaFilePathFor(uriReal!!))
////                        )
////                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
////                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
////                            Log.e("TAG", "addOnSuccessListenerRegisterBB " + location.toString())
//////                            viewModel.data.identificationImage = compressedImageFile.path
//////                            binding.textViewIdentificationImage.setText(compressedImageFile.name)
////                            imagePath = compressedImageFile.path
////                            latLong = LatLng(location!!.latitude, location.longitude)
////
////                            binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
////                            binding.textAddressTxt.text = requireActivity().getAddress(latLong)
////                            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
////                            val currentDate = sdf.format(Date())
////                            binding.textTimeTxt.text = currentDate
////                            mainThread {
////                                dispatchTakePictureIntent(binding.layoutMainCapture) {
////                                    viewModel.foodItemImage = this
////                                    Log.e("TAG", "viewModel.foodItemImage " + this)
////                                    binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
////                                }
////                            }
////                        }
////                    }
////                }
//            }
//        }
//    }

//    @SuppressLint("NotifyDataSetChanged")
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
////        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//
//        binding.apply {
//            signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
//                override fun onStartSigning() {
//                    //Event triggered when the pad is touched
////                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
////                        .show()
//                }
//
//                override fun onSigned() {
//                    //Event triggered when the pad is signed
////                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
////                        .show()
//                }
//
//                override fun onClear() {
//                    //Event triggered when the pad is cleared
////                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
////                        .show()
//                }
//            })
//
//            btComplete.singleClick {
//                ivSignature.setImageBitmap(signaturePad.signatureBitmap)
//                ivSignature.visibility = View.VISIBLE
//                signaturePad.visibility = View.GONE
//                btTryAgain.visibility = View.VISIBLE
//                btComplete.visibility = View.GONE
//                btClear.visibility = View.INVISIBLE
//
////                callMediaPermissions()
//            }
//
//            btTryAgain.singleClick {
//                signaturePad.clear()
//                ivSignature.visibility = View.GONE
//                signaturePad.visibility = View.VISIBLE
//                btTryAgain.visibility = View.GONE
//                btComplete.visibility = View.VISIBLE
//                btClear.visibility = View.VISIBLE
//            }
//
//            btClear.singleClick {
//                signaturePad.clear()
//            }
//
//            editTextMonth.singleClick {
//                requireActivity().showDropDownDialog(type = 21){
//                    editTextMonth.setText(title)
//                    viewModel.foodMonth = title
//                }
//            }
//
//            editTextDate.singleClick {
//                requireActivity().showDropDownDialog(type = 22){
//                    editTextDate.setText(title)
//                    viewModel.foodDate = title
//                }
//            }
//
//            btnImagePassportsize.singleClick {
////                imagePosition = 1
//////                callMediaPermissionsWithLocation()
////                requireActivity().runOnUiThread(){
////                    requireActivity().getCameraPath {
////                        uriReal = this
////                        captureMedia.launch(uriReal)
////                    }
////                }
//
//                imagePosition = 1
//                callMediaPermissions()
//            }
//            btnIdentityImage.singleClick {
////                imagePosition = 2
////                callMediaPermissionsWithLocation()
//            }
//
//
//            btSignIn.singleClick {
//                viewModel.foodHeight = editTextHeight.text.toString()
////                viewModel.foodSignature = editTextHeight.text.toString()
////                viewModel.foodItemImage = title
////                viewModel.foodIdentityImage = title
//
//            }
//
//
//        }
//    }




    private fun callMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
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
            Log.e("TAG", "thisthis "+this.path)
//            captureMedia.launch(uriReal)
        }



    }

    private fun forGallery() {
//        requireActivity().runOnUiThread() {
//            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//        }
    }






//
//
//    private fun callMediaPermissionsWithLocation() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
//            activityResultLauncherLocation.launch(
//                arrayOf(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.READ_MEDIA_IMAGES,
//                    Manifest.permission.READ_MEDIA_VIDEO,
//                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//            ))
//        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//            activityResultLauncherLocation.launch(
//                arrayOf(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.READ_MEDIA_IMAGES,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//            ))
//        } else{
//            activityResultLauncherLocation.launch(
//                arrayOf(
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION)
//            )
//        }
//    }
//
//    private val activityResultLauncherLocation =
//        registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions())
//        { permissions ->
//            if(!permissions.entries.toString().contains("false")){
//                requireActivity().showOptions {
//                    when(this){
//                        1 -> forCamera()
//                        2 -> forGallery()
//                    }
//                }
//            } else {
//                requireActivity().callPermissionDialog{
//                    someActivityResultLauncherLocation.launch(this)
//                }
//            }
//        }
//
//
//    var someActivityResultLauncherLocation = registerForActivityResult<Intent, ActivityResult>(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        callMediaPermissionsWithLocation()
//    }
//
//    private fun forCamera() {
//        requireActivity().runOnUiThread(){
//            requireActivity().getCameraPath {
//                uriReal = this
//                captureMedia.launch(uriReal)
//            }
//        }
//    }
//
//    private fun forGallery() {
//        requireActivity().runOnUiThread(){
//            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//        }
//    }
//












//
//    private fun callMediaPermissions() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
//            activityResultLauncher.launch(
//                arrayOf(
//                    Manifest.permission.READ_MEDIA_IMAGES,
//                    Manifest.permission.READ_MEDIA_VIDEO,
//                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
//            )
//        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
//            activityResultLauncher.launch(
//                arrayOf(
//                    Manifest.permission.READ_MEDIA_IMAGES)
//            )
//        } else{
//            activityResultLauncher.launch(
//                arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            )
//        }
//    }
//
//    private val activityResultLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions())
//        { permissions ->
//            if(!permissions.entries.toString().contains("false")){
//                mainThread {
//                    dispatchTakePictureIntent(binding.ivSignature){
//                        viewModel.foodSignature = this
//                        Log.e("TAG", "viewModel.foodSignature "+viewModel.foodSignature)
//                    }
//                }
//            } else {
//                requireActivity().callPermissionDialog{
//                    someActivityResultLauncher.launch(this)
//                }
//            }
//        }
//
//
//    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        callMediaPermissions()
//    }
//
//
//
//    private fun dispatchTakePictureIntent(imageView: View, callBack: String.() -> Unit) {
//        val bitmap: Bitmap = getBitmapFromView(imageView)
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        // val filename = System.currentTimeMillis().toString() + "." + "png" // change png/pdf
//        val file = File(path, getImageName())
//        try {
//            if (!path.exists()) path.mkdirs()
//            if (!file.exists()) file.createNewFile()
//            val ostream: FileOutputStream = FileOutputStream(file)
//            bitmap.compress(CompressFormat.PNG, 10, ostream)
//            ostream.close()
//            val data = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file)
//            }else{
//                val imagePath: File = File(file.absolutePath)
//                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", imagePath)
//            }
////            Log.e("TAG", "viewModel.foodSignature "+viewModel.foodSignature)
//            callBack(file.toString())
//
//        } catch (e: IOException) {
//            Log.w("ExternalStorage", "Error writing $file", e)
//        }
//    }
//
//    fun getBitmapFromView(view: View): Bitmap {
//        val bitmap = Bitmap.createBitmap(
//            view.width, view.height, Bitmap.Config.ARGB_8888
//        )
//
//        val canvas = Canvas(bitmap)
//        view.draw(canvas)
//        return bitmap
//    }



}