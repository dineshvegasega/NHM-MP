package com.nhm.distributor.screens.main.NBPA.addForms

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.nhm.distributor.R
import com.nhm.distributor.databinding.Form3Binding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.screens.interfaces.CallBackListener
import com.nhm.distributor.screens.main.NBPA.NBPAViewModel
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.isProductLoad
import com.nhm.distributor.screens.onboarding.register.Register.Companion.imagePath
import com.nhm.distributor.utils.callPermissionDialog
import com.nhm.distributor.utils.callPermissionDialogGPS
import com.nhm.distributor.utils.getAddress
import com.nhm.distributor.utils.getCameraPath
import com.nhm.distributor.utils.getImageName
import com.nhm.distributor.utils.getMediaFilePathFor
import com.nhm.distributor.utils.getMonthFromHindi
import com.nhm.distributor.utils.isLocationEnabled
import com.nhm.distributor.utils.loadImage
import com.nhm.distributor.utils.loadImageForms
import com.nhm.distributor.utils.mainThread
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.showOptions
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class NBPA_Form3 : Fragment() , CallBackListener {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: Form3Binding? = null
    private val binding get() = _binding!!
    var isSignature = false

    companion object {
        var callBackListener: CallBackListener? = null
        var tabPosition = 0
        var formFill3 = false
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = Form3Binding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
        callBackListener = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        binding.apply {

            ivMenu.singleClick {
                NBPA.callBackListener!!.onCallBack(1001)
            }

            signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
                override fun onStartSigning() {
                    //Event triggered when the pad is touched
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                    isSignature = true
                }

                override fun onSigned() {
                    //Event triggered when the pad is signed
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }

                override fun onClear() {
                    //Event triggered when the pad is cleared
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
//                    isSignature = false
                }
            })

            btComplete.singleClick {
                if (isSignature){
                    ivSignature.setImageBitmap(signaturePad.signatureBitmap)
                    ivSignature.visibility = View.VISIBLE
                    signaturePad.visibility = View.GONE
                    btTryAgain.visibility = View.VISIBLE
                    btComplete.visibility = View.GONE
                    btClear.visibility = View.INVISIBLE
                    callMediaPermissions()
                } else {
                    showSnackBar(getString(R.string.add_signature))
                }

            }

            btTryAgain.singleClick {
                signaturePad.clear()
                ivSignature.visibility = View.GONE
                signaturePad.visibility = View.VISIBLE
                btTryAgain.visibility = View.GONE
                btComplete.visibility = View.VISIBLE
                btClear.visibility = View.VISIBLE
                viewModel.foodSignatureImage = ""
                formFill3 = false
                isSignature = false
            }

            btClear.singleClick {
                signaturePad.clear()
                viewModel.foodSignatureImage = ""
                formFill3 = false
                isSignature = false
            }

            editTextMonth.singleClick {
                requireActivity().showDropDownDialog(type = 21){
                    Log.e("TAG", "getMonthFromHindi "+title)
                    editTextMonth.setText(title.getMonthFromHindi())
                    viewModel.foodMonth = title.getMonthFromHindi()
                }
            }

            editTextDate.singleClick {
                requireActivity().showDropDownDialog(type = 22){
                    editTextDate.setText(title)
                    viewModel.foodDate = title
                }
            }



            ivImagePassportsizeImage.singleClick {
                imagePosition = 1
//                if (isLocationEnabled(requireActivity()) == true) {
//                    callMediaPermissionsWithLocation()
//                } else {
//                    requireActivity().callPermissionDialogGPS {
//                        someActivityResultLauncherWithLocationGPS.launch(this)
//                    }
//                }
                callMediaPermissionsWithLocation()
            }

            ivImageIdentityImage.singleClick {
                imagePosition = 2
//                if (isLocationEnabled(requireActivity()) == true) {
//                    callMediaPermissionsWithLocation()
//                } else {
//                    requireActivity().callPermissionDialogGPS {
//                        someActivityResultLauncherWithLocationGPS.launch(this)
//                    }
//                }
                callMediaPermissionsWithLocation()
            }


            btnImagePassportsize.singleClick {
                imagePosition = 1
//                if (isLocationEnabled(requireActivity()) == true) {
//                    callMediaPermissionsWithLocation()
//                } else {
//                    requireActivity().callPermissionDialogGPS {
//                        someActivityResultLauncherWithLocationGPS.launch(this)
//                    }
//                }
                callMediaPermissionsWithLocation()
            }

            btnIdentityImage.singleClick {
                imagePosition = 2
//                if (isLocationEnabled(requireActivity()) == true) {
//                    callMediaPermissionsWithLocation()
//                } else {
//                    requireActivity().callPermissionDialogGPS {
//                        someActivityResultLauncherWithLocationGPS.launch(this)
//                    }
//                }
                callMediaPermissionsWithLocation()
            }





            btSignIn.singleClick {
                isProductLoad = true
                getData(true)
            }

        }


    }




    private fun callMediaPermissionsWithLocation() {
        try {
            if (isLocationEnabled(requireActivity()) == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    Log.e("TAG", "AAAAAAAAAAA")
                    activityResultLauncherWithLocation.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.READ_MEDIA_VIDEO,
                            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Log.e("TAG", "BBBBBBBBB")
                    activityResultLauncherWithLocation.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_MEDIA_IMAGES,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else {
                    Log.e("TAG", "CCCCCCCCC")
                    activityResultLauncherWithLocation.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            } else {
                requireActivity().callPermissionDialogGPS {
                    someActivityResultLauncherWithLocationGPS.launch(this)
                }
            }
        } catch (e : Exception){

        }

    }


    private val activityResultLauncherWithLocation =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            try {
                if (!permissions.entries.toString().contains("false")) {
                    requireActivity().showOptions {
                        when (this) {
                            1 -> forCamera()
                            2 -> forGallery()
                        }
                    }
                } else {
                    requireActivity().callPermissionDialog {
                        someActivityResultLauncherWithLocation.launch(this)
                    }
                }
            } catch (e : Exception){

            }
        }


    var someActivityResultLauncherWithLocation = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            callMediaPermissionsWithLocation()
        } catch (e : Exception){

        }
    }

    var someActivityResultLauncherWithLocationGPS = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            Log.e("TAG", "result.resultCode "+result.resultCode)
            callMediaPermissionsWithLocation()
        } catch (e : Exception){

        }
    }


    private fun forCamera() {
        try {
            requireActivity().getCameraPath {
                uriReal = this
                captureMedia.launch(uriReal)
            }
//
//            Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
//                putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_BACK);
//                putExtra("android.intent.extra.USE_FRONT_CAMERA", false)
//            }.also { takePictureIntent ->
//                takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
//                    // Camera app is available: launch it
//                    takePictureLauncher.launch(takePictureIntent)
//                } ?: run {
//                    // No camera app available: inform the user
//                    Toast.makeText(requireActivity(), "No camera app available", Toast.LENGTH_SHORT).show()
//                }
//            }
        } catch (e : Exception){

        }
    }

    private fun forGallery() {
        try {
            requireActivity().runOnUiThread() {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        } catch (e : Exception){

        }
    }

//    @SuppressLint("MissingPermission", "SuspiciousIndentation")
//    private val takePictureLauncher: ActivityResultLauncher<Intent> =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//
//            lifecycleScope.launch{
//                            val compressedImageFile = Compressor.compress(
//                                requireContext(),
//                                File(requireContext().getMediaFilePathFor(result.data?.data!!))
//                            )
//                imagePath = compressedImageFile.path
//                binding.ivImagePassportsizeImage.loadImage(type = 1, url = { imagePath })
//            }
//
//
//
//            Toast.makeText(requireActivity(), "Photo taken "+result.toString(), Toast.LENGTH_SHORT).show()
//        }

    var imagePosition = 0
    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    private var pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            try {
                lifecycleScope.launch @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
                    if (uri != null) {
                        when (imagePosition) {
                            1 -> {
                                val compressedImageFile = Compressor.compress(
                                    requireContext(),
                                    File(requireContext().getMediaFilePathFor(uri))
                                )
                                mainThread {
//                                binding.progressBarFoodItem.visibility = View.VISIBLE
                                    binding.ivImagePassportsizeImage.loadImageForms(type = 1, url = { "" })
                                }
                                var isLocation = false
                                val locationRequest = LocationRequest().setInterval(1000).setFastestInterval(1000)
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                fusedLocationClient.requestLocationUpdates(
                                    locationRequest,
                                    object : LocationCallback() {
                                        override fun onLocationResult(locationResult: LocationResult) {
                                            super.onLocationResult(locationResult)
                                            for (location in locationResult.locations) {
                                                if (!isLocation) {
                                                    isLocation = true
                                                    imagePath = compressedImageFile.path
                                                    var latLong = LatLng(location!!.latitude, location.longitude)
                                                    Log.e("TAG", "addOnSuccessListener111111 " + latLong.toString())
                                                    readData(LOGIN_DATA) { loginUser ->
                                                        if (loginUser != null) {
                                                            val data = Gson().fromJson(loginUser, Login::class.java)
                                                            binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                                            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                                            val currentDate = sdf.format(Date())

                                                            binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name

                                                            binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                                            binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude

//                                        readData(GEO_LOCATION) { geoLocation ->
//                                            if (geoLocation == "true") {
//                                                readData(GEO_LAT_LONG) { latLong ->
//                                                    if (latLong!!.isNotEmpty()) {
//                                                        val latLong = latLong!!.split(",")
//                                                        mainThread {
//                                                            binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(LatLng(latLong[0].toDouble(), latLong[1].toDouble()))
//                                                            dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                                                viewModel.foodItemImage = this
//                                                                Log.e("TAG", "viewModel.foodItemImage " + this)
//                                                                binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            } else {
                                                            mainThread {
                                                                binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                                                dispatchTakePictureIntent(binding.layoutMainCapture) {
                                                                    viewModel.foodItemImage = this
                                                                    Log.e("TAG", "viewModel.foodItemImageAAAAAAA " + this)
                                                                    binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })

                                                                }
//                                                                binding.progressBarFoodItem.visibility = View.GONE
                                                            }
//                                            }
//                                        }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    Looper.myLooper()
                                )
                            }

                            2 -> {
                                val compressedImageFile = Compressor.compress(
                                    requireContext(),
                                    File(requireContext().getMediaFilePathFor(uri))
                                )
                                mainThread {
//                                binding.progressBarIdentity.visibility = View.VISIBLE
                                    binding.ivImageIdentityImage.loadImageForms(type = 1, url = { "" })
                                }
                                var isLocation = false
                                val locationRequest = LocationRequest().setInterval(1000).setFastestInterval(1000)
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                fusedLocationClient.requestLocationUpdates(
                                    locationRequest,
                                    object : LocationCallback() {
                                        override fun onLocationResult(locationResult: LocationResult) {
                                            super.onLocationResult(locationResult)
                                            for (location in locationResult.locations) {
                                                if (!isLocation) {
                                                    isLocation = true
                                                    imagePath = compressedImageFile.path
                                                    var latLong = LatLng(location!!.latitude, location.longitude)
                                                    Log.e("TAG", "addOnSuccessListener333333 " + latLong.toString())
                                                    readData(LOGIN_DATA) { loginUser ->
                                                        if (loginUser != null) {
                                                            val data = Gson().fromJson(loginUser, Login::class.java)
                                                            binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                                            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                                            val currentDate = sdf.format(Date())

                                                            binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name

                                                            binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                                            binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude

//                                        readData(GEO_LOCATION) { geoLocation ->
//                                            if (geoLocation == "true") {
//                                                readData(GEO_LAT_LONG) { latLong ->
//                                                    if (latLong!!.isNotEmpty()) {
//                                                        val latLong = latLong!!.split(",")
//                                                        mainThread {
//                                                            binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(LatLng(latLong[0].toDouble(), latLong[1].toDouble()))
//                                                            dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                                                viewModel.foodIdentityImage = this
//                                                                Log.e("TAG", "viewModel.foodIdentityImage " + this)
//                                                                binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            } else {
                                                            mainThread {
                                                                binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                                                dispatchTakePictureIntent(binding.layoutMainCapture) {
                                                                    viewModel.foodIdentityImage = this
                                                                    Log.e("TAG", "viewModel.foodIdentityImageCCCCCCCC " + this)
                                                                    binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
//                                                                    binding.progressBarIdentity.visibility = View.GONE
                                                                }
                                                            }
//                                            }
//                                        }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    Looper.myLooper()
                                )
                            }
                        }
                    }
                }
            } catch (e : Exception){

            }
        }


    var uriReal: Uri? = null
    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        try {
            lifecycleScope.launch @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
                if (uri == true) {
                    when (imagePosition) {
                        1 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uriReal!!))
                            )
                            mainThread {
//                            binding.progressBarFoodItem.visibility = View.VISIBLE
                                binding.ivImagePassportsizeImage.loadImageForms(type = 1, url = { "" })
                            }
                            var isLocation = false
                            val locationRequest = LocationRequest().setInterval(1000).setFastestInterval(1000)
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                object : LocationCallback() {
                                    override fun onLocationResult(locationResult: LocationResult) {
                                        super.onLocationResult(locationResult)
                                        for (location in locationResult.locations) {
                                            if (!isLocation) {
                                                isLocation = true
                                                Log.e("TAG", "addOnSuccessListenerBB " + location.toString())
                                                imagePath = compressedImageFile.path
                                                var latLong = LatLng(location!!.latitude, location.longitude)

                                                readData(LOGIN_DATA) { loginUser ->
                                                    if (loginUser != null) {
                                                        val data = Gson().fromJson(loginUser, Login::class.java)
                                                        binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                                        val currentDate = sdf.format(Date())

                                                        binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name

                                                        binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                                        binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude

//                                    readData(GEO_LOCATION) { geoLocation ->
//                                        if (geoLocation == "true") {
//                                            readData(GEO_LAT_LONG) { latLong ->
//                                                if (latLong!!.isNotEmpty()) {
//                                                    val latLong = latLong!!.split(",")
//                                                    mainThread {
//                                                        binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(LatLng(latLong[0].toDouble(), latLong[1].toDouble()))
//                                                        dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                                            viewModel.foodItemImage = this
//                                                            Log.e("TAG", "viewModel.foodItemImage " + this)
//                                                            binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        } else {
                                                        mainThread {
                                                            binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                                            dispatchTakePictureIntent(binding.layoutMainCapture) {
                                                                viewModel.foodItemImage = this
                                                                Log.e("TAG", "viewModel.foodItemImageEEEEEEEE " + this)
                                                                binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
//                                                                binding.progressBarFoodItem.visibility = View.GONE
                                                            }
                                                        }
//                                        }
//                                    }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                Looper.myLooper()
                            )
                        }

                        2 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uriReal!!))
                            )
                            mainThread {
//                            binding.progressBarIdentity.visibility = View.VISIBLE
                                binding.ivImageIdentityImage.loadImageForms(type = 1, url = { "" })
                            }
                            var isLocation = false
                            val locationRequest = LocationRequest().setInterval(1000).setFastestInterval(1000)
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            fusedLocationClient.requestLocationUpdates(
                                locationRequest,
                                object : LocationCallback() {
                                    override fun onLocationResult(locationResult: LocationResult) {
                                        super.onLocationResult(locationResult)
                                        for (location in locationResult.locations) {
                                            if (!isLocation) {
                                                isLocation = true
                                                Log.e("TAG", "addOnSuccessListenerBB " + location.toString())
                                                imagePath = compressedImageFile.path
                                                var latLong = LatLng(location!!.latitude, location.longitude)

                                                readData(LOGIN_DATA) { loginUser ->
                                                    if (loginUser != null) {
                                                        val data = Gson().fromJson(loginUser, Login::class.java)
                                                        binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                                        val currentDate = sdf.format(Date())

                                                        binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name

                                                        binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                                        binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude

//                                    readData(GEO_LOCATION) { geoLocation ->
//                                        if (geoLocation == "true") {
//                                            readData(GEO_LAT_LONG) { latLong ->
//                                                if (latLong!!.isNotEmpty()) {
//                                                    val latLong = latLong!!.split(",")
//                                                    mainThread {
//                                                        binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(LatLng(latLong[0].toDouble(), latLong[1].toDouble()))
//                                                        dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                                            viewModel.foodIdentityImage = this
//                                                            Log.e("TAG", "viewModel.foodIdentityImage " + this)
//                                                            binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        } else {
                                                        mainThread {
                                                            binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                                            dispatchTakePictureIntent(binding.layoutMainCapture) {
                                                                viewModel.foodIdentityImage = this
                                                                Log.e("TAG", "viewModel.foodIdentityImageGGGGGGGG " + this)
                                                                binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
//                                                                binding.progressBarIdentity.visibility = View.GONE
                                                            }
                                                        }
//                                        }
//                                    }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                Looper.myLooper()
                            )
                        }
                    }
                }
            }
        } catch (e : Exception){

        }
    }







    private fun callMediaPermissions() {
        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
                activityResultLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                )
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                activityResultLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES)
                )
            } else{
                activityResultLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                )
            }
        } catch (e : Exception){

        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            try {
                if(!permissions.entries.toString().contains("false")){
                    mainThread {
                        dispatchTakePictureIntent(binding.ivSignature){
                            viewModel.foodSignatureImage = this
                            Log.e("TAG", "viewModel.foodSignature "+viewModel.foodSignatureImage)
                        }
                    }
                } else {
                    requireActivity().callPermissionDialog{
                        someActivityResultLauncher.launch(this)
                    }
                }
            } catch (e : Exception){

            }

        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            callMediaPermissions()
        } catch (e : Exception){

        }
    }



    private fun dispatchTakePictureIntent(imageView: View, callBack: String.() -> Unit) {
        val bitmap: Bitmap = getBitmapFromView(imageView)
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        // val filename = System.currentTimeMillis().toString() + "." + "png" // change png/pdf
        val path = requireActivity().externalCacheDir ?: Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path, getImageName())
        try {
            if (!path.exists()) path.mkdirs()
            if (!file.exists()) file.createNewFile()
            val ostream: FileOutputStream = FileOutputStream(file)
            bitmap.compress(CompressFormat.PNG, 10, ostream)
            ostream.close()
            val data = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file)
            }else{
                val imagePath: File = File(file.absolutePath)
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", imagePath)
            }
//            Log.e("TAG", "viewModel.foodSignature "+viewModel.foodSignature)
            callBack(file.toString())

        } catch (e: IOException) {
            Log.w("ExternalStorage", "Error writing $file", e)
        }
    }

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    override fun onCallBack(pos: Int) {
        Log.e("TAG", "onCallBackNo3 "+pos)
        getData(false)
    }




    private fun getData(isButton: Boolean) {
        var bool: Boolean = false
        if (viewModel.editDataNew != null){
            val list = viewModel.editDataNew!!.schemeDetail
            val isArray = list.filter { it.foodMonth == viewModel.foodMonth }
            bool = if (isArray.size == 0) false else true
            Log.e("TAG", "getDatabool "+isArray.size)
        }


        binding.apply {
            if (editTextMonth.text.toString() == "") {
                showSnackBar(getString(R.string.select_month))
                formFill3 = false
            } else if (editTextDate.text.toString() == "") {
                showSnackBar(getString(R.string.select_date))
                formFill3 = false
            } else if (editTextHeight.text.toString() == "") {
                showSnackBar(getString(R.string.enterWeight))
                formFill3 = false
            } else if (viewModel.foodSignatureImage == "" || isSignature == false) {
                showSnackBar(getString(R.string.add_signature))
                formFill3 = false
            } else if (viewModel.foodItemImage == "") {
                showSnackBar(getString(R.string.food_item_image))
                formFill3 = false
            } else if (viewModel.foodIdentityImage == "") {
                showSnackBar(getString(R.string.identity_imageStar))
                formFill3 = false
            }  else {
//                if (viewModel.start == "no"){
//                    viewModel.foodHeight = editTextHeight.text.toString()
//                    if (isButton){
//                        formFill3 = true
//                        NBPA.callBackListener!!.onCallBack(1003)
//                    } else {
//                    }
//                }
//                if (viewModel.start == "yes"){
                    if (bool == true) {
                        showSnackBar(requireView().resources.getString(R.string.food_already_taken))
                        formFill3 = false
                    } else {
                        viewModel.foodHeight = editTextHeight.text.toString()
                        formFill3 = true
                        if (isButton){
                            NBPA.callBackListener!!.onCallBack(1003)
                        }
                    }
//                }
            }
        }
    }

//    override fun onStop() {
//        super.onStop()
//        isProductLoad = true
//        isProductLoadMember = true
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        isProductLoad = false
//        isProductLoadMember = false
//    }

}