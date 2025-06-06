package com.nhm.distributor.screens.main.setLocation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.nhm.distributor.R
import com.nhm.distributor.databinding.SetLocationBinding
import com.nhm.distributor.datastore.DataStoreKeys.GEO_LAT_LONG
import com.nhm.distributor.datastore.DataStoreKeys.GEO_LOCATION
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.datastore.DataStoreUtil.saveData
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class SetLocation : Fragment() {

    private val viewModel: SetLocationVM by viewModels()
    private var _binding: SetLocationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SetLocationBinding.inflate(inflater)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(3)

        binding.apply {
            readData(GEO_LOCATION) { geoLocation ->
                if (geoLocation == null) {
                    Log.e("TAG", "groLocationAA "+geoLocation)
                    switchGeoLocation.isChecked = false
                    setVisible(false)
                    saveData(GEO_LOCATION, ""+false)
                    saveData(GEO_LAT_LONG, "")
                } else {
                    Log.e("TAG", "groLocationBB "+geoLocation)
                    if (geoLocation == "true"){
                        switchGeoLocation.isChecked = true
                        setVisible(true)
                        readData(GEO_LAT_LONG) { latLong ->
                            if (latLong!!.isNotEmpty()) {
                                val latLong = latLong!!.split(",")
                                editTextLatitude.setText(""+latLong[0])
                                editTextLongitude.setText(""+latLong[1])
                            }
                        }
                    } else {
                        switchGeoLocation.isChecked = false
                        setVisible(false)
                        saveData(GEO_LAT_LONG, "")
                    }
                }
            }

            var isSave = false
            switchGeoLocation.setOnClickListener {
                if (switchGeoLocation.isChecked) {
                    isSave = true
                    setVisible(true)
                } else {
                    saveData(GEO_LOCATION, ""+false)
                    saveData(GEO_LAT_LONG, "")
                    isSave = false
                    setVisible(false)
                }
            }


            btSignIn.setOnClickListener {
                try{
                    val lat = editTextLatitude.text.toString().toDouble()
                    val long = editTextLongitude.text.toString().toDouble()
                    saveData(GEO_LAT_LONG, ""+lat+","+long)
                    saveData(GEO_LOCATION, ""+isSave)
                    showSnackBar(getString(R.string.location_saved_successfully))
                }catch (e:Exception){
                    e.printStackTrace()
                    showSnackBar(getString(R.string.please_enter_valid_latitude_longitude))
                }
            }
        }
    }

    private fun setVisible(bool: Boolean) {
        binding.apply {
            if (bool) {
                editTextLatitude.visibility = View.VISIBLE
                editTextLongitude.visibility = View.VISIBLE
                btSignIn.visibility = View.VISIBLE
            } else {
                editTextLatitude.visibility = View.INVISIBLE
                editTextLongitude.visibility = View.INVISIBLE
                btSignIn.visibility = View.INVISIBLE
            }
        }
    }


    override fun onDestroyView() {
        MainActivity.mainActivity.get()?.callFragment(4)
        _binding = null
        super.onDestroyView()
    }

}