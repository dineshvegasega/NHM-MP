package com.nhm.distributor.screens.onboarding.forgetPassword

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ForgetPasswordBinding
import com.nhm.distributor.fcm.VerifyBroadcastReceiver
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.interfaces.SMSListener
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.OtpTimer
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.isValidPassword
import com.nhm.distributor.utils.parseOneTimeCode
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ForgetPassword : Fragment() , OtpTimer.SendOtpTimerData {
    private var _binding: ForgetPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ForgetPasswordVM by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ForgetPasswordBinding.inflate(inflater)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)
        OtpTimer.sendOtpTimerData = this


        binding.apply {
            editTextVeryfyOtp.isEnabled = false
            btSignIn.isEnabled = false
            textBack.singleClick {
                view.findNavController().navigateUp()
            }


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

            var counter = 0
            var start: Int
            var end: Int
            imgCreatePassword.singleClick {
                if(counter == 0){
                    counter = 1
                    imgCreatePassword.setImageResource(R.drawable.ic_eye_open)
                    start=editTextPassword.getSelectionStart()
                    end=editTextPassword.getSelectionEnd()
                    editTextPassword.setTransformationMethod(null)
                    editTextPassword.setSelection(start,end)
                }else{
                    counter = 0
                    imgCreatePassword.setImageResource(R.drawable.ic_eye_closed)
                    start=editTextPassword.getSelectionStart()
                    end=editTextPassword.getSelectionEnd()
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod())
                    editTextPassword.setSelection(start,end)
                }
            }


            viewModel.isSend.value = false
            viewModel.isSend.observe(viewLifecycleOwner, Observer {
                editTextSendOtp.setText(if (it == true) {getString(R.string.resendOtp)} else {getString(R.string.send_otp)})
                if (it == true){
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
                if (it == true){
                    tvTime.visibility = View.GONE
//                    OtpTimer.sendOtpTimerData = null
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

                    btSignIn.setEnabled(true)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._E79D46, null)))
                }
            })


            editTextSendOtp.singleClick {
                if (editTextMobileNumber.text.toString().isEmpty() || editTextMobileNumber.text.toString().length != 10){
                    showSnackBar(getString(R.string.enterMobileNumber))
                }else{
                    val obj: JSONObject = JSONObject().apply {
                        put(mobile_no, binding.editTextMobileNumber.text.toString())
                        put(slug, forgot)
                        put(user_type, USER_TYPE)
                    }
                    if(networkFailed) {
                        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
                        requireContext().registerReceiver(
                            VerifyBroadcastReceiver(),
                            intentFilter,
                            Context.RECEIVER_NOT_EXPORTED
                        )
                        val client = SmsRetriever.getClient(requireContext())
                        client.startSmsUserConsent(null)
                        val task: Task<Void> = client.startSmsRetriever()
                        task.addOnSuccessListener {
                            VerifyBroadcastReceiver.initSMSListener(object : SMSListener {
                                override fun onSuccess(intent: Intent?) {
                                    someActivityResultLauncher.launch(intent)
                                }
                            })
                        }
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
                        put(slug, forgot)
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
                    if (isTimer.isEmpty()) {
                        editTextSendOtp.setEnabled(true)
                        editTextSendOtp.setBackgroundTintList(
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    getResources(), R.color._E79D46, null)))
                        viewModel.isOtpVerified = false
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
                    if (isTimer.isEmpty()) {
                        editTextSendOtp.setEnabled(true)
                        editTextSendOtp.setBackgroundTintList(
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    getResources(), R.color._E79D46, null)))
                        viewModel.isOtpVerified = false
                        editTextOtp.requestFocus()
                    }
                }
            })


            btSignIn.singleClick {
                if (editTextMobileNumber.text.toString().isEmpty() || editTextMobileNumber.text.toString().length != 10){
                    showSnackBar(getString(R.string.enterMobileNumber))
                } else if (editTextPassword.text.toString().isEmpty()){
                    showSnackBar(getString(R.string.YourNewPassword))
                } else if(editTextPassword.text.toString().length >= 0 && editTextPassword.text.toString().length < 8){
                    showSnackBar(getString(R.string.InvalidPassword))
                } else if(!isValidPassword(editTextPassword.text.toString().trim())){
                    showSnackBar(getString(R.string.InvalidPassword))
                } else{
                    val obj: JSONObject = JSONObject().apply {
                        put(mobile_number, editTextMobileNumber.text.toString())
                        put(password, editTextPassword.text.toString())
                    }
                    if(networkFailed) {
                        viewModel.passwordUpdate(view = requireView(), obj)
                    } else {
                        requireContext().callNetworkDialog()
                    }
                }
            }

        }
    }





    var isTimer = ""
    override fun otpData(string: String) {
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



    val someActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val message = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            if (message != null) {
                binding.apply {
                    editTextOtp.requestFocus()
                    editTextOtp.setText(message.parseOneTimeCode())
                    editTextOtp.text?.length?.let { editTextOtp.setSelection(it) }
                    OtpTimer.sendOtpTimerData = null
                    OtpTimer.stopTimer()
                    isTimer = ""
                    tvTime.visibility = View.GONE
                }
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