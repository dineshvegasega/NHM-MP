package com.nhm.distributor.screens.onboarding.loginOtp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import com.nhm.distributor.networking.*
import com.nhm.distributor.R
import com.nhm.distributor.databinding.LoginOtpBinding
import com.nhm.distributor.fcm.VerifyBroadcastReceiver
import com.nhm.distributor.screens.interfaces.SMSListener
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.OtpTimer
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.parseOneTimeCode
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class LoginOtp : Fragment() , OtpTimer.SendOtpTimerData {
    private var _binding: LoginOtpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginOtpVM by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginOtpBinding.inflate(inflater)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)
        OtpTimer.sendOtpTimerData = this

        binding.apply {
            textBack.singleClick {
                view.findNavController().navigateUp()
            }

            viewModel.isSend.value = false
            viewModel.isSend.observe(viewLifecycleOwner, Observer {
                editTextSendOtp.setText(if (it == true) {getString(R.string.resendOtp)} else {getString(R.string.send_otp)})
                if (it == true){
                    OtpTimer.startTimer()
                    btSignIn.setEnabled(true)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._E79D46, null)))
                }else{
                    btSignIn.setEnabled(false)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._999999, null)))
                }
            })

            viewModel.isSendMutable.value = false
            viewModel.isSendMutable.observe(viewLifecycleOwner, Observer {
                if (it == true){
                    tvTime.visibility = View.GONE
                    OtpTimer.stopTimer()
                }
            })


            editTextSendOtp.singleClick {
                if (editTextMobileNumber.text.toString().isEmpty() || editTextMobileNumber.text.toString().length != 10){
                    showSnackBar(getString(R.string.enterMobileNumber))
                }else{
                    val obj: JSONObject = JSONObject().apply {
                        put(mobile_no, editTextMobileNumber.text.toString())
                        put(slug, login)
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
                        put(slug, login)
                        put(user_type, USER_TYPE)
                    }
                    if(networkFailed) {
                        viewModel.verifyOTPData(view = requireView(), obj)
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
                } else if (editTextOtp.text.toString().isEmpty()){
                    showSnackBar(getString(R.string.enterOtp))
                } else{
                    val obj: JSONObject = JSONObject().apply {
                        put(mobile_no, binding.editTextMobileNumber.text.toString())
                        put(otp, binding.editTextOtp.text.toString())
                        put(slug, login)
                        put(user_type, USER_TYPE)
                    }
                    if(networkFailed) {
                        viewModel.verifyOTPData(view = requireView(), obj)
                    } else {
                        requireContext().callNetworkDialog()
                    }
                }
            }



            editTextLoginWith.singleClick {
                view.findNavController().navigate(R.id.action_loginOtp_to_loginPassword)
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