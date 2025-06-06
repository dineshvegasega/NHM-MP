package com.nhm.distributor.screens.main.changeMobile

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ChangeMobileBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.networking.*
import com.nhm.distributor.networking.getJsonRequestBody
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.OtpTimer
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONObject

@AndroidEntryPoint
class ChangeMobile : Fragment() , OtpTimer.SendOtpTimerData {
    private val viewModel: ChangeMobileVM by viewModels()
    private var _binding: ChangeMobileBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChangeMobileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        OtpTimer.sendOtpTimerData = this

        binding.apply {
            editTextVeryfyOtp.setEnabled(false)

            viewModel.isAgree.value = false
            viewModel.isAgree.observe(viewLifecycleOwner, Observer {
                if (it == true){
//                    Log.e("TAG", "isAgreeAA "+viewModel.isAgree.value)
                    btSignIn.setEnabled(true)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._E79D46, null)))
                } else {
                   // Log.e("TAG", "isAgreeBB "+viewModel.isAgree.value)
                    btSignIn.setEnabled(false)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
                }
            })

            viewModel.isSend.value = false
            viewModel.isSend.observe(viewLifecycleOwner, Observer {
                editTextSendOtp.setText(if (it == true) {getString(R.string.resendOtp)} else {getString(R.string.send_otp)})
                if (it == true){
                    OtpTimer.startTimer()
                    editTextVeryfyOtp.setEnabled(true)
                    editTextVeryfyOtp.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._E79D46, null)))
                }else{
                    editTextVeryfyOtp.setEnabled(false)
                    editTextVeryfyOtp.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
                }
            })


            viewModel.isSendMutable.value = false
            viewModel.isSendMutable.observe(viewLifecycleOwner, Observer {
                if (it) isTimer = ""
                if (it == true){
                    viewModel.isSendMutable
                    tvTime.visibility = View.GONE
                    OtpTimer.stopTimer()
                    editTextSendOtp.setEnabled(false)
                    editTextVeryfyOtp.setEnabled(false)
                    editTextSendOtp.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
                    editTextVeryfyOtp.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))

                    if (editTextMobileNumber.text.toString().isEmpty()){
                        viewModel.isAgree.value = false
                    } else if (editTextOtp.text.toString().isEmpty()){
                        viewModel.isAgree.value = false
                    } else if (viewModel.isOtpVerified == false){
                        viewModel.isAgree.value = false
                    } else {
                        viewModel.isAgree.value = true
                    }
                }
            })



            editTextSendOtp.singleClick {
                if (editTextMobileNumber.text.toString().isEmpty() || editTextMobileNumber.text.toString().length != 10){
                    showSnackBar(getString(R.string.enterMobileNumber))
                } else{
                    val obj: JSONObject = JSONObject().apply {
                        put(mobile_no, binding.editTextMobileNumber.text.toString())
                        put(slug, signup)
                        put(user_type, USER_TYPE)
                    }
                    if(networkFailed) {
                        viewModel.sendOTP(view = requireView(), obj)
                    } else {
                        requireContext().callNetworkDialog()
                    }
                }
            }

            editTextVeryfyOtp.singleClick {
                if (editTextOtp.text.toString().isEmpty()){
                    showSnackBar(getString(R.string.enterOtp))
                }else{
                    val obj: JSONObject = JSONObject().apply {
                        put(mobile_no, editTextMobileNumber.text.toString())
                        put(otp, editTextOtp.text.toString())
                        put(slug, signup)
                        put(user_type, USER_TYPE)
                    }
                    if(networkFailed) {
                        viewModel.verifyOTP(view = requireView(), obj)
                    } else {
                        requireContext().callNetworkDialog()
                    }
                }
            }


            editTextMobileNumber.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!isTimer.isNotEmpty()) {
                        viewModel.isAgree.value = false
                        viewModel.isSendMutable.value = false
                        editTextSendOtp.setEnabled(true)
                        editTextSendOtp.setBackgroundTintList(
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    getResources(), R.color._E79D46, null)))
                        editTextMobileNumber.requestFocus()
                    }
                }
            })

            editTextOtp.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!isTimer.isNotEmpty()) {
                        viewModel.isAgree.value = false
                        viewModel.isSendMutable.value = false
                        editTextSendOtp.setEnabled(true)
                        editTextSendOtp.setBackgroundTintList(
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    getResources(), R.color._E79D46, null)))
                        editTextOtp.requestFocus()
                    }
                }
            })



            btSignIn.singleClick {
                if (editTextMobileNumber.text.toString().isEmpty()){
                    showSnackBar(getString(R.string.enterMobileNumber))
                } else if (editTextOtp.text.toString().isEmpty()){
                    showSnackBar(getString(R.string.enterOtp))
                } else if (viewModel.isOtpVerified == false){
                    showSnackBar(getString(R.string.invalid_OTP))
                } else {
                    val obj: JSONObject = JSONObject().apply {
                        put(mobile_no, editTextMobileNumber.text.toString())
                        put(user_type, USER_TYPE)
                    }
                    readData(LOGIN_DATA) { loginUser ->
                        if (loginUser != null) {
                            val _id = Gson().fromJson(loginUser, Login::class.java).id
                            if(networkFailed) {
                                viewModel.profileUpdate(view = requireView(), ""+_id, obj.getJsonRequestBody())
                            } else {
                                requireContext().callNetworkDialog()
                            }
                        }
                    }
                }
            }
        }

    }



    var isTimer = ""
    @OptIn(DelicateCoroutinesApi::class)
    override fun otpData(string: String) {
//        Log.e("TAG", "otpData "+string)
        isTimer = string
        binding.apply {
            tvTime.visibility = if (string.isNotEmpty()) View.VISIBLE else View.GONE
            tvTime.text = getString(R.string.the_verify_code_will_expire_in_00_59, string)

            if(string.isEmpty()){
                editTextSendOtp.setText(getString(R.string.resendOtp))
                editTextSendOtp.setEnabled(true)
                editTextSendOtp.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._E79D46, null)))
            } else {
                editTextSendOtp.setEnabled(false)
                editTextSendOtp.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null)))
            }
        }
    }



    override fun onDestroyView() {
        OtpTimer.sendOtpTimerData = null
        OtpTimer.stopTimer()
        _binding = null
        super.onDestroyView()
    }
}