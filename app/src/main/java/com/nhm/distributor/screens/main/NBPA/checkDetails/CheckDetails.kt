package com.nhm.distributor.screens.main.NBPA.checkDetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.nhm.distributor.R
import com.nhm.distributor.databinding.CheckDetailsBinding
import com.nhm.distributor.networking.filterByAadhaar
import com.nhm.distributor.networking.nakshayID
import com.nhm.distributor.screens.main.NBPA.addForms.NBPA_Form3.Companion.formFill3
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.userIdForGlobal
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

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


//            var _idSelect : Int = 2
//            radioGroupSelect.setOnCheckedChangeListener(object :
//                RadioGroup.OnCheckedChangeListener {
//                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
//                    when (checkedId) {
//                        radioButtonNakshayId.id -> {
//                            _idSelect = 1
//                            editTextAadhaarNumber.hint = getString(R.string.nakshay_id)
//                        }
//                        radioButtonAadhaarNumber.id -> {
//                            _idSelect = 2
//                            editTextAadhaarNumber.hint = getString(R.string.aadhaar_number_without_star)
//                        }
//                    }
//                }
//            })


            btSignIn.singleClick {
//                if (_idSelect == 1){
//                    if (editTextAadhaarNumber.text.toString().isEmpty()
//                    ) {
//                        showSnackBar(getString(R.string.enter_nakshay_id))
//                    } else {
//                        var obj: JSONObject = JSONObject()
//                        obj = JSONObject().apply {
//                            put(nakshayID, editTextAadhaarNumber.text.toString())
//                        }
//                        viewModel.checkAadhaarNo(obj){
//                            var itemProducts = this.data
//                            Log.e("TAG", "checkAadhaarNo: "+this.toString())
//                            if (itemProducts.size == 0){
//                                findNavController().navigate(R.id.action_checkDetails_to_nbpa, Bundle().apply {
//                                    putString("isExist", "no")
//                                    putString("isAadhaar", "no")
//                                    putString("aadhaarNumber", ""+editTextAadhaarNumber.text.toString())
//                                })
//                            } else {
//                                if(userIdForGlobal == ""+itemProducts[0].user_id){
//                                    findNavController().navigate(R.id.action_checkDetails_to_nbpa, Bundle().apply {
//                                        putString("isExist", "yes")
//                                        putString("_id", ""+itemProducts[0].id)
//                                    })
//                                } else {
//                                    showSnackBar(view.resources.getString(R.string.already_exists_nakshay_id))
//                                }
//                            }
//                        }
//                    }
//                } else if (_idSelect == 2){
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
                            var itemProducts = this.data
                            Log.e("TAG", "checkAadhaarNo: "+this.toString())
                            if (itemProducts.size == 0){
                                formFill3 = false
                                findNavController().navigate(R.id.action_checkDetails_to_nbpa, Bundle().apply {
                                    putString("isExist", "no")
                                    putString("isAadhaar", "yes")
                                    putString("aadhaarNumber", ""+editTextAadhaarNumber.text.toString())
                                })
                            } else {
                                if(userIdForGlobal == ""+itemProducts[0].user_id){
                                    formFill3 = false
                                    findNavController().navigate(R.id.action_checkDetails_to_nbpa, Bundle().apply {
                                        putString("isExist", "yes")
                                        putString("_id", ""+itemProducts[0].id)
                                    })
                                } else {
                                    showSnackBar(view.resources.getString(R.string.already_exists_aadhaar))
                                }
                            }
                        }
                    }
//                }


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