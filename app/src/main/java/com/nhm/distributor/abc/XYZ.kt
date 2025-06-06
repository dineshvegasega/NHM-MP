package com.nhm.distributor.abc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mindorks.example.fusedlocation.utils.PermissionUtils
import com.nhm.distributor.R
import com.nhm.distributor.utils.callPermissionDialog
import kotlin.toString

class XYZ : AppCompatActivity() {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        callMediaPermissions()
    }




    private fun callMediaPermissions() {
        val permissionList = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        activityResultLauncher.launch(permissionList)
    }

    @SuppressLint("MissingPermission")
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            if(!permissions.entries.toString().contains("false")){
                setUpLocationListener()
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
    private fun setUpLocationListener() {
//        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(1000).setFastestInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

//        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//            Log.e("TAG", "addOnSuccessListenerBBB " + location.toString())
//        }


        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S || Build.VERSION.SDK_INT == Build.VERSION_CODES.S_V2){
            Log.e("TAG", "addOnSuccessListenerBBB " )

            var isLocation = false
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        for (location in locationResult.locations) {
                            if (!isLocation) {
                                isLocation = true
                                Log.e("TAG", "addOnSuccessListenerBB " + location.toString())
                            }
                        }
                    }
                },
                Looper.myLooper()
            )
        }






    }

//    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
//    override fun onStart() {
//        super.onStart()
//        when {
//            PermissionUtils.isAccessFineLocationGranted(this) -> {
//                when {
//                    PermissionUtils.isLocationEnabled(this) -> {
//                        setUpLocationListener()
//                    }
//                    else -> {
//                        PermissionUtils.showGPSNotEnabledDialog(this)
//                    }
//                }
//            }
//            else -> {
//                PermissionUtils.requestAccessFineLocationPermission(
//                    this,
//                    LOCATION_PERMISSION_REQUEST_CODE
//                )
//            }
//        }
//    }
//
//    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            LOCATION_PERMISSION_REQUEST_CODE -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    when {
//                        PermissionUtils.isLocationEnabled(this) -> {
//                            setUpLocationListener()
//                        }
//                        else -> {
//                            PermissionUtils.showGPSNotEnabledDialog(this)
//                        }
//                    }
//                } else {
//                    Toast.makeText(
//                        this,
//                        "getString(R.string.location_permission_not_granted)",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//        }
//    }
}