package com.nhm.distributor.abc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.nhm.distributor.databinding.ABinding
import com.nhm.distributor.utils.callPermissionDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ABC : AppCompatActivity() {
    private val viewModel: ABCVM by viewModels()
    private var _binding: ABinding? = null
    private val binding get() = _binding!!


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

    private val permissionList = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        _binding = ABinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val policy = ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build()
        StrictMode.setThreadPolicy(policy)
//        val fruits =
//            arrayOf<String?>("Apple", "Banana", "Cherry", "Date", "Grape", "Kiwi", "Mango", "Pear")
//
//        val adapter = ArrayAdapter<String?>(this, android.R.layout.simple_list_item_1, fruits)



        binding.apply {

            autoCompleteTextView.setAdapter(
                PlacesAutoCompleteAdapter(
                    this@ABC,
                    android.R.layout.simple_list_item_1
                )
            )
            autoCompleteTextView.setThreshold(1)

//            autoCompleteTextView.setAdapter<ArrayAdapter<String?>?>(adapter)
            autoCompleteTextView.setTextColor(Color.RED)

//            autoCompleteTextView.setOnFocusChangeListener { _, _ ->
//                autoCompleteTextView.showDropDown()
//            }


            autoCompleteTextView.setOnItemClickListener(object :
                AdapterView.OnItemClickListener {
                public override fun onItemClick(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val description = parent.getItemAtPosition(position) as String?
                    Log.e("TAG", "description:: " + description)
                    autoCompleteTextView.setText(description)

                }
            })
        }




//        activityResultLauncher.launch(permissionList)

//        callMediaPermissions()
//        requestQ()


//        locationRequest = LocationRequest.create().apply {
//
//        }
//
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
//
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//
//                // Normally, you want to save a new location to a database. We are simplifying
//                // things a bit and just saving it as a local variable, as we only need it again
//                // if a Notification is created (when the user navigates away from app).
//                currentLocation = locationResult.lastLocation
//
//            }
//        }
//
//
//        val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
//        removeTask.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Log.d("TAG", "Location Callback removed.")
//                stopSelf()
//            } else {
//                Log.d("TAG", "Failed to remove Location Callback.")
//            }
//        }
//    }

    }


    private fun callMediaPermissions() {
        activityResultLauncher.launch(permissionList)
    }

    @SuppressLint("MissingPermission")
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            if(!permissions.entries.toString().contains("false")){
                openCamera()
            } else {
                callPermissionDialog{
                    someActivityResultLauncher.launch(this)
                }
            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun openCamera() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            Log.e("TAG", "addOnSuccessListenerBB " + location.toString())
        }
    }


    private var pickMedia =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { uri ->
            lifecycleScope.launch {

            }

        }




//    private fun requestQ() {
//        requestMultiplePermissions.launch(
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.CAMERA
//            )
//        )
//    }
//
//
//    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
//            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
//            permissions[Manifest.permission.CAMERA] == true) {
//            Log.e("DEBUG", "${permissions.size} = ${permissions.values}")
//            val longerThan4 = permissions.filter { it.value == false }
//            Log.e("longerThan4", "longerThan4 "+longerThan4.size)
//
//            if(longerThan4.size == 0){
//                Log.e("longerThan4", "CLICK")
//            } else {
//                Log.e("longerThan4", "")
////                requestQ()
//
//            }
//        } else {
//            // Permission was denied. Display an error message.
//            Log.e("DEBUG", "Permission was denied")
//        }
//
////        permissions.entries.forEach {
////            Log.e("DEBUG", "${it.key} = ${it.value}")
////        }
////        if (isGranted.containsValue(false)) {
////            Log.d("PERMISSIONS", "At least one of the permissions was not granted, launching again...");
////            multiplePermissionLauncher.launch(PERMISSIONS);
////        }
////        if (permissions) {
////
////        }
//    }

//    private val activityResultLauncher =
//        registerForActivityResult(RequestMultiplePermissions()) { permissions ->
//            if (!permissions.values.contains(false)) {
////                contactsProvider.addMockContactList()
//                permissionState.value = true
//                Log.d(TAG, "activityResultLauncher")
//            }
//        }
//
//    val activityResultLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted ->
//            Log.e("TAG", "activityResultLauncher")
//            // Handle Permission granted/rejected
//            if (isGranted) {
//                // Permission is granted
//            } else {
//                // Permission is denied
//            }
//        }

}