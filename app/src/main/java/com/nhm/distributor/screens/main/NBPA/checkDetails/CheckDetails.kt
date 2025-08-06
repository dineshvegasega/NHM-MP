package com.nhm.distributor.screens.main.NBPA.checkDetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nhm.distributor.R
import com.nhm.distributor.databinding.CheckDetailsBinding
import com.nhm.distributor.datastore.DataStoreKeys
import com.nhm.distributor.datastore.DataStoreUtil
import com.nhm.distributor.networking.filterByAadhaar
import com.nhm.distributor.networking.nakshayID
import com.nhm.distributor.screens.main.NBPA.addForms.NBPA_Form3.Companion.formFill3
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.File

@AndroidEntryPoint
class CheckDetails  : Fragment() {
    private lateinit var viewModel: CheckDetailsVM
    private var _binding: CheckDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CheckDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(CheckDetailsVM::class.java)
        MainActivity.Companion.mainActivity.get()?.callFragment(4)
        binding.apply {


//            val directory = File(requireActivity().filesDir, "camera_images")
//            if (!directory.exists()) {
//                directory.mkdirs()
//            }
//            val uriReal = FileProvider.getUriForFile(
//                requireContext(),
//                requireActivity().packageName + ".provider",
//                File(directory, "FoodItem.png")
//            )
//
//
//            val fdelete: File = File(uriReal.getPath())
//            fdelete.delete();
//            if(fdelete.exists()){
//                fdelete.getCanonicalFile().delete();
//                if(fdelete.exists()){
//                    requireActivity().deleteFile(fdelete.getName());
//                }
//            }


            DataStoreUtil.removeKey(DataStoreKeys.SELECT) {}


            var _idSelect : Int = 1
            radioGroupSelect.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    radioButtonNakshayId.id -> {
                        _idSelect = 1
                        editTextAadhaarNumber.hint = getString(R.string.nakshay_id)
                    }

                    radioButtonAadhaarNumber.id -> {
                        _idSelect = 2
                        editTextAadhaarNumber.hint = getString(R.string.aadhaar_number_without_star)
                    }
                }
            }


            btSignIn.singleClick {
                if (_idSelect == 1){
                    if (editTextAadhaarNumber.text.toString().isEmpty()
                    ) {
                        showSnackBar(getString(R.string.enter_nakshay_id))
                    } else {
                        var obj: JSONObject = JSONObject()
                        obj = JSONObject().apply {
                            put(nakshayID, editTextAadhaarNumber.text.toString())
                        }
                        viewModel.checkAadhaarNo(obj){
                            val itemProducts = this.data
                            Log.e("TAG", "checkAadhaarNo: "+this.toString())
                            if (itemProducts.size == 0){
                                findNavController().navigate(R.id.action_checkDetails_to_nbpa, Bundle().apply {
                                    putString("isExist", "no")
                                    putString("isAadhaar", "no")
                                    putString("aadhaarNumber", ""+editTextAadhaarNumber.text.toString())
                                })
                            } else {
                                viewModel.formListDetail(
                                    view = requireView(),
                                    ""+itemProducts[0].id
                                ) {
                                    val schemeDetailArray = this.schemeDetail.size
                                    Log.e("TAG", "formListDetail: "+schemeDetailArray.toString())
                                    if(this.schemeDetail.size >= 6){
                                        MaterialAlertDialogBuilder(requireContext(), R.style.LogoutDialogTheme)
                                            .setTitle(resources.getString(R.string.app_name))
                                            .setMessage(resources.getString(R.string.food_more_than_6))
                                            .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                                                dialog.dismiss()
                                            }
                                            .setCancelable(false)
                                            .show()
                                    } else {
                                        formFill3 = false
                                        findNavController().navigate(R.id.action_checkDetails_to_nbpa, Bundle().apply {
                                            putString("isExist", "yes")
                                            putString("_id", ""+itemProducts[0].id)
                                        })
                                    }
                                }
                            }
                        }
                    }
                } else if (_idSelect == 2){
                    if (editTextAadhaarNumber.text.toString()
                            .isEmpty() || editTextAadhaarNumber.text.toString().length != 12
                    ) {
                        showSnackBar(getString(R.string.enter_valid_aadhaar_number))
                    } else {
                        var obj: JSONObject = JSONObject()
                        obj = JSONObject().apply {
                            put(filterByAadhaar, editTextAadhaarNumber.text.toString())
                        }
                        viewModel.checkAadhaarNo(obj){
                            val itemProducts = this.data
                            Log.e("TAG", "checkAadhaarNo: "+this.toString())
                            if (itemProducts.size == 0){
                                formFill3 = false
                                findNavController().navigate(R.id.action_checkDetails_to_nbpa, Bundle().apply {
                                    putString("isExist", "no")
                                    putString("isAadhaar", "yes")
                                    putString("aadhaarNumber", ""+editTextAadhaarNumber.text.toString())
                                })
                            } else {
                                viewModel.formListDetail(
                                    view = requireView(),
                                    ""+itemProducts[0].id
                                ) {
                                    val schemeDetailArray = this.schemeDetail.size
                                    Log.e("TAG", "formListDetail: "+schemeDetailArray.toString())
                                    if(this.schemeDetail.size >= 6){
                                        MaterialAlertDialogBuilder(requireContext(), R.style.LogoutDialogTheme)
                                            .setTitle(resources.getString(R.string.app_name))
                                            .setMessage(resources.getString(R.string.food_more_than_6))
                                            .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
                                                dialog.dismiss()
                                            }
                                            .setCancelable(false)
                                            .show()
                                    } else {
                                        formFill3 = false
                                        findNavController().navigate(R.id.action_checkDetails_to_nbpa, Bundle().apply {
                                            putString("isExist", "yes")
                                            putString("_id", ""+itemProducts[0].id)
                                        })
                                    }
                                }
                            }
                        }
                    }
                }


            }
        }
    }



//    override fun onDestroyView() {
//        super.onDestroyView()
//        MainActivity.mainActivity.get()?.callFragment(4)
//        isProductLoad = false
//        isProductLoadMember = false
//    }
}