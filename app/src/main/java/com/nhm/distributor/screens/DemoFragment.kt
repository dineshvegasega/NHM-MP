package com.nhm.distributor.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.nhm.distributor.BuildConfig
import com.nhm.distributor.R
import com.nhm.distributor.databinding.Form3Binding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.screens.main.NBPA.NBPAViewModel
import com.nhm.distributor.screens.onboarding.register.Register.Companion.imagePath
import com.nhm.distributor.utils.callPermissionDialog
import com.nhm.distributor.utils.callPermissionDialogGPS
import com.nhm.distributor.utils.getAddress
import com.nhm.distributor.utils.getCameraPathFoodItem
import com.nhm.distributor.utils.getImageName
import com.nhm.distributor.utils.getMediaFilePathFor
import com.nhm.distributor.utils.isLocationEnabled
import com.nhm.distributor.utils.loadImage
import com.nhm.distributor.utils.loadImageForms
import com.nhm.distributor.utils.mainThread
import com.nhm.distributor.utils.showOptions
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.get

@AndroidEntryPoint
class DemoFragment : Fragment() {

    private lateinit var viewModel: NBPAViewModel
    private var _binding: Form3Binding? = null
    private val binding get() = _binding!!

    private var uriReal: Uri ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = Form3Binding.inflate(inflater, container, false)
        return binding.root
    }
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>


    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private var currentPhotoPath: String = ""

    var imagePosition = 0
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Log.e("TAG", "onViewCreated")

        binding.apply {



            permissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->



                val imagesGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] == true
                val videoGranted = permissions[Manifest.permission.READ_MEDIA_VIDEO] == true
                val videoGrantedREAD_MEDIA_VISUAL_USER_SELECTED = permissions[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] == true
                Log.e("TAG", "imagesGranted "+imagesGranted)
                Log.e("TAG", "videoGranted "+videoGranted)


//                val imagePermission = ContextCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.READ_MEDIA_IMAGES
//                )
//                val videoPermission = ContextCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.READ_MEDIA_VIDEO
//                )
//
//                val granted = imagePermission == PackageManager.PERMISSION_GRANTED && videoPermission == PackageManager.PERMISSION_GRANTED
//                Toast.makeText(requireContext(), "Media access granted ${granted}", Toast.LENGTH_SHORT).show()

                if (imagesGranted || videoGranted || videoGrantedREAD_MEDIA_VISUAL_USER_SELECTED) {
                    // At least one permission granted
                    Toast.makeText(requireContext(), "Media access granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Permissions denied", Toast.LENGTH_SHORT).show()
                }
            }



//var imageFile = File("/storage/emulated/0/Android/data/com.nhm.distributor/cache/photo11.jpg");
//var d = BitmapDrawable(getResources(), imageFile.getAbsolutePath());
//            ivImagePassportsizeImage.setImageDrawable(d);
            cameraPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                    if (granted) {
                        launchCameraForImage()
                    } else {
                        Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
                    }
                }

            cameraLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        handleCameraResult()
                    }
                }

            galleryLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                        handleGalleryResult(result.data!!)
                    }
                }


            ivImagePassportsizeImage.singleClick {
                imagePosition = 1

                callMediaPermissionsWithLocation()
//                captureImage()

//                capturePhotoForRecordUpload();

//                requireActivity().getCameraPathFoodItemCreate {
//                        uriReal = this
//                        Log.e("TAG", "ccompressedImageFile " + uriReal)
//                        captureMediaA.launch(uriReal)
//                }
            }

            ivImageIdentityImage.singleClick {
                imagePosition = 1
//                callMediaPermissionsWithLocation()
                captureImage()
            }



            layoutMainCapture.visibility = View.INVISIBLE
            layoutNestedScrollView.visibility = View.VISIBLE
            layoutMainCapture.singleClick {
                Log.e("TAG", "layoutMainCapture "+viewModel.foodSignatureImage)
                callMediaPermissions()
            }


        }


    }

    fun capturePhotoForRecordUpload() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
//            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
////            val storageDir = File(Environment.getExternalStorageDirectory().getPath() + "/MyApp")
//            if (!storageDir.exists()) storageDir.mkdirs()
//            if (!storageDir.exists()) storageDir.mkdir()
//            var file = File(
//                storageDir.path + "/sample.jpg"
//            )
//            var imageUri = Uri.fromFile(file);

//            val file = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "photo11.jpg")
            val file = File(requireActivity().externalCacheDir , "photo11.jpg")
                    val uri = FileProvider.getUriForFile(requireContext(), requireActivity().packageName + ".provider", file)
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)


            takePictureIntent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                file
            )

            resultLauncher.launch(takePictureIntent)

//            startActivityForResult(takePictureIntent, 7)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            Log.e("TAG", "AAAAAAAAASSSS "+data?.data?.path)
        }
    }







    private fun callMediaPermissionsWithLocation() {
        try {

//                val permissionsToRequest = mutableListOf<String>()
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
//                    permissionsToRequest += Manifest.permission.READ_MEDIA_IMAGES
//                    permissionsToRequest += Manifest.permission.READ_MEDIA_VIDEO
//                    permissionsToRequest += Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
//                } else {
//                    permissionsToRequest += Manifest.permission.READ_EXTERNAL_STORAGE
//                }
//
//                permissionLauncher.launch(permissionsToRequest.toTypedArray())





            if (isLocationEnabled(requireActivity()) == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    Log.e("TAG", "AAAAAAAAAAA")
                    activityResultLauncherWithLocation.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
//                            Manifest.permission.READ_MEDIA_IMAGES,
//                            Manifest.permission.READ_MEDIA_VIDEO,
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
//                            Manifest.permission.READ_MEDIA_VIDEO,
//                            Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else {
                    Log.e("TAG", "CCCCCCCCC")
                    activityResultLauncherWithLocation.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_COARSE_LOCATION
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
//                                imagePath = compressedImageFile.path
//                                binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
//                                dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                    viewModel.foodItemImage = this
//                                    Log.e("TAG", "viewModel.foodItemImageAAAAAAA " + this)
//                                    binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
//                                }



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




    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
//        Log.e("TAG", "addOnSuccessListenerBB " + uri.toString())
        try {
            lifecycleScope.launch @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {

                Log.e("TAG", "addOnSuccessListenerBBCC " + uri.toString())

//                if (::uriReal.isInitialized) {
                    val compressedImageFile = Compressor.compress(
                        requireContext(),
                        File(requireContext().getMediaFilePathFor(uriReal!!))
                    )

                    Log.e("TAG", "addOnSuccessListenerBBCCcompressedImageFile " + compressedImageFile)
//                }



//                if (uri == true) {
////                    Log.e("TAG", "addOnSuccessListenerBBCCDD " + uri.toString())
//                    when (imagePosition) {
//                        1 -> {
////                            Log.e("TAG", "addOnSuccessListenerBBCCDDFF " + uri.toString())
//                            val compressedImageFile = Compressor.compress(
//                                requireContext(),
//                                File(requireContext().getMediaFilePathFor(uriReal!!))
//                            )
//                            mainThread {
////                            binding.progressBarFoodItem.visibility = View.VISIBLE
//                                binding.ivImagePassportsizeImage.loadImageForms(type = 1, url = { "" })
//                            }
//                            var isLocation = false
//                            val locationRequest = LocationRequest().setInterval(1000).setFastestInterval(1000)
//                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                            fusedLocationClient.requestLocationUpdates(
//                                locationRequest,
//                                object : LocationCallback() {
//                                    override fun onLocationResult(locationResult: LocationResult) {
//                                        super.onLocationResult(locationResult)
//                                        for (location in locationResult.locations) {
//                                            if (!isLocation) {
//                                                isLocation = true
//                                                Log.e("TAG", "addOnSuccessListenerBB " + location.toString())
//                                                imagePath = compressedImageFile.path
//                                                var latLong = LatLng(location!!.latitude, location.longitude)
//
//                                                readData(LOGIN_DATA) { loginUser ->
//                                                    if (loginUser != null) {
//                                                        val data = Gson().fromJson(loginUser, Login::class.java)
//                                                        binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
//                                                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//                                                        val currentDate = sdf.format(Date())
//
//                                                        binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name
//
//                                                        binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
//                                                        binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude
//
////                                    readData(GEO_LOCATION) { geoLocation ->
////                                        if (geoLocation == "true") {
////                                            readData(GEO_LAT_LONG) { latLong ->
////                                                if (latLong!!.isNotEmpty()) {
////                                                    val latLong = latLong!!.split(",")
////                                                    mainThread {
////                                                        binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(LatLng(latLong[0].toDouble(), latLong[1].toDouble()))
////                                                        dispatchTakePictureIntent(binding.layoutMainCapture) {
////                                                            viewModel.foodItemImage = this
////                                                            Log.e("TAG", "viewModel.foodItemImage " + this)
////                                                            binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
////                                                        }
////                                                    }
////                                                }
////                                            }
////                                        } else {
//                                                        mainThread {
//                                                            binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
//                                                            dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                                                viewModel.foodItemImage = this
//                                                                Log.e("TAG", "viewModel.foodItemImageEEEEEEEE " + this)
//                                                                binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
////                                                                binding.progressBarFoodItem.visibility = View.GONE
//                                                            }
//                                                        }
////                                        }
////                                    }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                },
//                                Looper.myLooper()
//                            )
//                        }
//
//                        2 -> {
//                            val compressedImageFile = Compressor.compress(
//                                requireContext(),
//                                File(requireContext().getMediaFilePathFor(uriReal!!))
//                            )
//                            mainThread {
////                            binding.progressBarIdentity.visibility = View.VISIBLE
//                                binding.ivImageIdentityImage.loadImageForms(type = 1, url = { "" })
//                            }
//                            var isLocation = false
//                            val locationRequest = LocationRequest().setInterval(1000).setFastestInterval(1000)
//                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                            fusedLocationClient.requestLocationUpdates(
//                                locationRequest,
//                                object : LocationCallback() {
//                                    override fun onLocationResult(locationResult: LocationResult) {
//                                        super.onLocationResult(locationResult)
//                                        for (location in locationResult.locations) {
//                                            if (!isLocation) {
//                                                isLocation = true
//                                                Log.e("TAG", "addOnSuccessListenerBB " + location.toString())
//                                                imagePath = compressedImageFile.path
//                                                var latLong = LatLng(location!!.latitude, location.longitude)
//
//                                                readData(LOGIN_DATA) { loginUser ->
//                                                    if (loginUser != null) {
//                                                        val data = Gson().fromJson(loginUser, Login::class.java)
//                                                        binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
//                                                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//                                                        val currentDate = sdf.format(Date())
//
//                                                        binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name
//
//                                                        binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
//                                                        binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude
//
////                                    readData(GEO_LOCATION) { geoLocation ->
////                                        if (geoLocation == "true") {
////                                            readData(GEO_LAT_LONG) { latLong ->
////                                                if (latLong!!.isNotEmpty()) {
////                                                    val latLong = latLong!!.split(",")
////                                                    mainThread {
////                                                        binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(LatLng(latLong[0].toDouble(), latLong[1].toDouble()))
////                                                        dispatchTakePictureIntent(binding.layoutMainCapture) {
////                                                            viewModel.foodIdentityImage = this
////                                                            Log.e("TAG", "viewModel.foodIdentityImage " + this)
////                                                            binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
////                                                        }
////                                                    }
////                                                }
////                                            }
////                                        } else {
//                                                        mainThread {
//                                                            binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
//                                                            dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                                                viewModel.foodIdentityImage = this
//                                                                Log.e("TAG", "viewModel.foodIdentityImageGGGGGGGG " + this)
//                                                                binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
////                                                                binding.progressBarIdentity.visibility = View.GONE
//                                                            }
//                                                        }
////                                        }
////                                    }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                },
//                                Looper.myLooper()
//                            )
//                        }
//                    }
//                }
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



    private val activityResultLauncherWithLocation =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            try {
//                if (permissions.get(Manifest.permission.CAMERA) != null && permissions.get(Manifest.permission.CAMERA) == true) {
//                    // Camera permission granted
//                    Log.e("TAG", "permissionsAA11 ")
//                } else {
//                    Log.e("TAG", "permissionsAA11FF ")
//                }
//                if (permissions.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null && permissions.get(
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    ) == true
//                ) {
//                    Log.e("TAG", "permissionsAA22 ")
//                } else {
//                    Log.e("TAG", "permissionsAA22FF ")
//                }


//                permissions.forEach {
//                    var  permissionName = it.key
//                    var  isGranted = it.value
//                    Log.e("TAG", "permissionsAA "+permissionName +" "+isGranted)
//
////                    if (!permissions.entries.toString().contains("false")) {
////                        requireActivity().showOptions {
////                            when (this) {
////                                1 -> forCamera()
////                                2 -> forGallery()
////                            }
////                        }
////                    } else {
////                        requireActivity().callPermissionDialog {
////                            someActivityResultLauncherWithLocation.launch(this)
////                        }
////                    }
//
//                }



//                Log.e("TAG", "permissionsAA "+permissions.entries)

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


    @Throws(IOException::class)
    private fun createImageFile2(): File? {
        // Create an image file name
        val timeStamp=
            SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName="JPEG_" + timeStamp + "_"
        val storageDir=requireActivity()!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.e("TAG","createImageFile " + storageDir)
        return File.createTempFile(mFileName,".jpg",storageDir)
    }



    var photoFile : File? = null
    var fileimage : File? = null
    var imageUri : Uri ? = null
    private fun forCamera() {
        try {
//            takeImage()

//            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            requireActivity().runOnUiThread() {
                try {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    photoFile = createImageFile2()

//            var photoFile: File?=null
//
//                photoFile=createImageFile2()
//
////            val photoURI=FileProvider.getUriForFile(
////                requireActivity()!!,BuildConfig.APPLICATION_ID.toString() + ".provider",
////                photoFile!!
////            )
//
//                photoFile?.also { file ->
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        requireContext(),
//                        BuildConfig.APPLICATION_ID.toString() + ".provider",
//                        file
//                    )
//                    imageUri = photoURI
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                }
//                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)


//            fileimage=photoFile
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI)
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    resultLauncher.launch(takePictureIntent)


//                    val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo.jpg")
//                    val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
//                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
//                    resultLauncher.launch(takePictureIntent)


                } catch (ex: IOException) {
                    ex.printStackTrace()
                    // Error occurred while creating the File
                    Log.e("TAG","errrr " + ex.message)
                }
            }



//            imageUri = FileProvider.getUriForFile(
//                requireContext(),
//                "${requireContext().packageName}.provider",
//                File(requireContext().cacheDir, "temp_image.jpg")
//            )


//            val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
//                if (result) {
//                    Log.e("TAG", "photoFileAA "+imageUri)
//                } else {
//                    // Image capture failed or was cancelled
//                }
//            }

// 3. Launch the camera intent
// This typically happens in response to a user action, e.g., button click
//            captureMediaA.launch(imageUri)


//            Handler(Looper.getMainLooper()).postDelayed({
////                requireActivity().runOnUiThread() {
////                    requireActivity().getCameraPath {
////                        uriReal = this
////                        captureMediaA.launch(uriReal)
////                    }
////                Log.e("TAG", "photoFileAA "+uriReal)
//
////
////
//////                    val file = File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg")
//////
//////
//////                    //Uri of camera image
//////                    val uri = FileProvider.getUriForFile(
//////                        requireContext(),
//////                        requireActivity().applicationContext.packageName + ".provider",
//////                        file
//////                    )
//////
//////                    uriReal = uri
////
////
//////                    var photoFile: File?=null
//////                    try {
//////                        photoFile=createImageFile()
//////                        if (!photoFile!!.exists()) photoFile.createNewFile()
//////                        Log.e("TAG","photoFilephotoFile " + photoFile)
//////
//////                    } catch (ex: IOException) {
//////                        ex.printStackTrace()
//////                        // Error occurred while creating the File
//////                        Log.e("TAG","errrrrrr " + ex.message)
//////                    }
//////
//////                    viewModel.uriRealM = FileProvider.getUriForFile(
//////                        requireContext(),requireActivity().applicationContext.packageName + ".provider",
//////                        photoFile!!
//////                    )
////
//////                    uriReal = photoURI
//////                    captureMediaA.launch(viewModel.uriRealM)
////                }
//
////                val photoFile = createImageFile()
//
//
////                Log.e("TAG", "photoFileAA "+photoFile)
//
////                if (photoFile != null) {
////                    val photoUri: Uri = FileProvider.getUriForFile(
////                        requireContext(), "${requireActivity().packageName}.provider", photoFile
////                    )
////                    uriReal = photoUri
////                    captureMediaA.launch(uriReal)
////                }
//
//
//            }, 200)



//            mainThread {
//                withContext(Dispatchers.Default) {
//                        val userDataDeferred = async {
//                            requireActivity().getCameraPath {
//                            uriReal = this
//                            captureMediaA.launch(uriReal)
//                        }.await()
//                       val userData = userDataDeferred.await()
//                    }
//                }
//            }


//            mainThread {
//                uriReal = createImageUri()
//                captureMedia.launch(uriReal)
//            }



//            uriReal = requireContext().createTempPictureUri()
//            captureMediaA.launch(uriReal)


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




//    private fun openCameraIntent() {
//        val pictureIntent = Intent(
//            MediaStore.ACTION_IMAGE_CAPTURE
//        )
//        if (pictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
//            //Create a file to store the image
//            var photoFile: File? = null
//            try {
//                photoFile = createImageFile()
//            } catch (ex: IOException) {
//                // Error occurred while creating the File
//            }
//            if (photoFile != null) {
//                val photoURI =
//                    FileProvider.getUriForFile(requireContext(), BuildConfig.APPLICATION_ID.toString() + ".provider", photoFile)
//                pictureIntent.putExtra(
//                    MediaStore.EXTRA_OUTPUT,
//                    photoURI
//                )
//
//                resultLauncher.launch(pictureIntent)
//            }
//        }
//    }





    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
//                val bitmap = result.data?.extras?.parcelable<Bitmap>("data")
//
//                Log.e("TAG", "addOnSuccessListenerBBCCcompressedImageFile " +bitmap)

                val file = File(requireActivity().externalCacheDir , "photo11.jpg")
//                val uri = FileProvider.getUriForFile(requireContext(), requireActivity().packageName + ".provider", file)

//                val imageUri = result.data!!.data?.authority

                Log.e("TAG", "addOnSuccessListenerBBCCcompressedImageFile " +file)





                //    private void openCameraIntent() {
                //        Intent pictureIntent = new Intent(
                //                MediaStore.ACTION_IMAGE_CAPTURE);
                //        if(pictureIntent.resolveActivity(getPackageManager()) != null){
                //            //Create a file to store the image
                //            File photoFile = null;
                //            try {
                //                photoFile = createImageFile();
                //            } catch (IOException ex) {
                //                // Error occurred while creating the File
                //            ...
                //            }
                //            if (photoFile != null) {
                //                Uri photoURI = FileProvider.getUriForFile(this,                                                                                                    "com.example.android.provider", photoFile);
                //                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                //                        photoURI);
                //                startActivityForResult(pictureIntent,
                //                        REQUEST_CAPTURE_IMAGE);
                //            }
                //        }
                //    }
                //    Uri uri = data.getData();
                //    ContentResolver cr = this.getContentResolver();
                //    String mime = cr.getType(uri);
//                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                val file = File(path, getImageName())
//                try {
////                    var bytes = ByteArrayOutputStream();
//                    bitmap!!.compress(CompressFormat.PNG, 100, FileOutputStream(file));
////                    val ostream: FileOutputStream = FileOutputStream(file)
////                    ostream.write(bytes.toByteArray());
////                    // remember close file output
////                    ostream.close();
//                } catch ( e : Exception) {
//                    e!!.printStackTrace();
//                }


                //    private void openCameraIntent() {
                //        Intent pictureIntent = new Intent(
                //                MediaStore.ACTION_IMAGE_CAPTURE);
                //        if(pictureIntent.resolveActivity(getPackageManager()) != null){
                //            //Create a file to store the image
                //            File photoFile = null;
                //            try {
                //                photoFile = createImageFile();
                //            } catch (IOException ex) {
                //                // Error occurred while creating the File
                //            ...
                //            }
                //            if (photoFile != null) {
                //                Uri photoURI = FileProvider.getUriForFile(this,                                                                                                    "com.example.android.provider", photoFile);
                //                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                //                        photoURI);
                //                startActivityForResult(pictureIntent,
                //                        REQUEST_CAPTURE_IMAGE);
                //            }
                //        }
                //    }
                //    Uri uri = data.getData();
                //    ContentResolver cr = this.getContentResolver();
                //    String mime = cr.getType(uri);


//                val contentUri: Uri? = result.data?.data




                //    private void openCameraIntent() {
                //        Intent pictureIntent = new Intent(
                //                MediaStore.ACTION_IMAGE_CAPTURE);
                //        if(pictureIntent.resolveActivity(getPackageManager()) != null){
                //            //Create a file to store the image
                //            File photoFile = null;
                //            try {
                //                photoFile = createImageFile();
                //            } catch (IOException ex) {
                //                // Error occurred while creating the File
                //            ...
                //            }
                //            if (photoFile != null) {
                //                Uri photoURI = FileProvider.getUriForFile(this,                                                                                                    "com.example.android.provider", photoFile);
                //                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                //                        photoURI);
                //                startActivityForResult(pictureIntent,
                //                        REQUEST_CAPTURE_IMAGE);
                //            }
                //        }
                //    }
//                var uri: Uri? = result.data?.extras.toString().toUri()
//                val filePathColumn = arrayOf<String?>(MediaStore.Images.Media.DATA)
//
//                val cursor: Cursor = requireActivity().getContentResolver().query(
//                    uri!!,
//                    filePathColumn, null, null, null
//                )!!
//                cursor.moveToFirst()



//
//                val cursor: Cursor? = requireActivity().getContentResolver().query(
//                    uri,
//                    filePathColumn, null, null, null
//                )
//                cursor.moveToFirst()

//                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
//                val picturePath = cursor.getString(columnIndex)
//
//
//
//                var cr: ContentResolver = requireActivity().getContentResolver()
//                var mime: String? = cr.getType(uri!!)
//                Log.e("TAG", "viewModel.foodItemImageAAAAAAA " + mime)


//                var ContentResolver cr = this.getContentResolver();
//
//                val proj = arrayOf<String?>(MediaStore.Images.Media.DATA)
//                val cursor: Cursor = managedQuery(contentUri, proj, null, null, null)
//                val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                cursor.moveToFirst()
//                val tmppath = cursor.getString(column_index)

//                requireActivity().getBitmapToPath(bitmap!!){
//                        Log.e("TAG", "addOnSuccessListenerBBCCcompressedImageFile " +this)
//                    }


//                binding.ivIcon22.setImageBitmap(bitmap)


//                dispatchTakePictureIntent(binding.ivIcon22) {
//                    viewModel.foodItemImage = this
//                    Log.e("TAG", "viewModel.foodItemImageAAAAAAA " + this)
//                    binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
//
//                }

//                Log.e("TAG", "addOnSuccessListenerBBCCcompressedImageFile " +imageFilePath)
//            val photoURI=FileProvider.getUriForFile(
//                requireActivity()!!,BuildConfig.APPLICATION_ID.toString() + ".provider",
//                photoFile!!
//            )
//                Log.e("TAG", "addOnSuccessListenerBBCCcompressedImageFile " +photoURI)

//                val bitmap = result.data?.extras?.get("data") as Bitmap
//                bitmap?.let {
//
//                    val selectedImage=result?.data
//                    try {
//                        var fileimage=File(getRealPathFromUri(selectedImage as Uri?))
//                    } catch (e: java.lang.Exception) {
//                        e.printStackTrace()
//                    }
//
////                    var uri = savebitmap(bitmap)
//
////                    var uri = getUri(requireContext(), bitmap)
////                    requireActivity().getBitmapToPath(bitmap){
////                        Log.e("TAG", "addOnSuccessListenerBBCCcompressedImageFile " +this)
////                    }
//                }
            } else {
                Log.e("TAG", "addOnSuccessListenerBBCCcompressedImageFileBBB ")
            }
        }



    private var latestTmpUri: Uri? = null
    private fun takeImage() {
        lifecycleScope.launch {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }
    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".jpg", requireActivity().cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(requireActivity().applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }


    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    Log.e("TAG", "addOnSuccessListenerBBCCcompressedImageFile " + uri)
                }
            }
        }


    val captureMediaA = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        Log.e("TAG", "uriuri " + uri)
        Log.e("TAG", "addOnSuccessListenerBBCCcompressedImageFile " + uriReal)

        requireActivity().getCameraPathFoodItem {
            var uriReal = this
            Log.e("TAG", "addOnSuccessListenerBBCCcompressedImageFileBB " + uriReal)
            lifecycleScope.launch {
                val compressedImageFile = Compressor.compress(
                    requireContext(),
                    File(requireContext().getMediaFilePathFor(uriReal!!))
                )
                binding.ivImagePassportsizeImage.loadImage(
                    type = 1,
                    url = { compressedImageFile.path })
            }
        }
    }


    fun createImageUri(): Uri {
        val photoFile = File.createTempFile(
            "IMG_",
            ".jpg",
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )
    }

    private fun forGallery() {
        try {
            requireActivity().runOnUiThread() {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
                        dispatchTakePictureIntent(binding.layoutMainCapture){
//                            viewModel.foodSignatureImage = this
                            Log.e("TAG", "viewModel.foodSignature "+this)
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





    private fun captureImage() {
        val cameraPermission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(requireContext(), cameraPermission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(cameraPermission)
        } else {
            launchCameraForImage()
        }
    }

    /**
     * Launches the camera intent to capture an image and saves it to a file.
     * Uses FileProvider for secure URI access.
     */

    var photoUriAAA : Uri ?= null
    private fun launchCameraForImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile = createImageFile()
            if (photoFile != null) {
                val photoUri: Uri = FileProvider.getUriForFile(
                    requireContext(), "${requireActivity().packageName}.provider", photoFile
                )


                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                photoUriAAA = photoUri
                cameraLauncher.launch(takePictureIntent)
            }
        }
    }

    /**
     * Creates a temporary file for storing the captured image.
     * Returns the created file or null in case of an error.
     *
     * @return File object representing the created image file.
     */
//    private fun createImageFile(): File? {
//        return try {
//            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
//            val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//            File.createTempFile("JPEG_$timeStamp", ".jpg", storageDir).apply {
//                currentPhotoPath = absolutePath
//            }
//        } catch (ex: IOException) {
//            Toast.makeText(requireContext(), "Error creating file", Toast.LENGTH_SHORT).show()
//            null
//        }
//    }


    var imageFilePath: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp =
            SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir: File? =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        imageFilePath = image.getAbsolutePath()
        return image
    }


//    @Throws(IOException::class)
//    private fun createImageFile(): File? {
//        // Create an image file name
//        val timeStamp=
//            SimpleDateFormat("yyyyMMddHHmmss").format(Date())
//        val mFileName="JPEG_" + timeStamp + "_"
//        val storageDir=requireActivity()!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        Log.e("TAG","createImageFile " + storageDir)
//        return File.createTempFile(mFileName,".jpg",storageDir)
//    }



    /**
     * Opens the system media picker to select an image from the gallery.
     * Uses ACTION_OPEN_DOCUMENT for better compatibility with Scoped Storage.
     */
    private fun openMediaPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    /**
     * Handles the result of a camera capture.
     * Decodes the image file and confirms successful capture.
     */
    private fun handleCameraResult() {
        Toast.makeText(requireContext(), "Image captured successfully!" +photoUriAAA, Toast.LENGTH_SHORT).show()

//        val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
//        if (bitmap != null) {
//            Toast.makeText(requireContext(), "Image captured successfully!", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show()
//        }
    }

    /**
     * Handles the result of selecting an image from the gallery.
     * Retrieves the image URI and confirms selection.
     *
     * @param data The intent containing the selected image data.
     */
    private fun handleGalleryResult(data: Intent) {
        val imageUri: Uri? = data.data
        Toast.makeText(requireContext(), "Image selected successfully!", Toast.LENGTH_SHORT).show()
    }

    companion object {
        /**
         * Constant value for camera permission request.
         */
        private const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }


    fun getRealPathFromUri(contentUri: Uri?): String? {
        var cursor: Cursor?=null
        return try {
            var proj=arrayOf<kotlin.String>(MediaStore.Images.Media.DATA)
            //val proj: Array<String>= arrayOf<kotlin.String>(MediaStore.Images.Media.DATA)
            cursor=requireActivity()!!.contentResolver.query(contentUri!!,proj,null,null,null)
            assert(cursor != null)
            val column_index=cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }


    override fun onResume() {
        super.onResume()
        Log.e("TAG", "onResume")
    }


    override fun onPause() {
        super.onPause()
        Log.e("TAG", "onPause")
    }



}