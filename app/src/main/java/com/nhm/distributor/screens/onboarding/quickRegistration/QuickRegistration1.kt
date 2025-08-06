package com.nhm.distributor.screens.onboarding.quickRegistration


import android.annotation.SuppressLint
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
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.Task
import com.nhm.distributor.R
import com.nhm.distributor.databinding.QuickRegistration1Binding
import com.nhm.distributor.fcm.VerifyBroadcastReceiver
import com.nhm.distributor.screens.interfaces.CallBackListener
import com.nhm.distributor.screens.interfaces.SMSListener
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.networking.*
import com.nhm.distributor.utils.OtpTimer
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.parseOneTimeCode
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONObject


@AndroidEntryPoint
class QuickRegistration1 : Fragment(), CallBackListener , OtpTimer.SendOtpTimerData {
    private var _binding: QuickRegistration1Binding? = null
    private val binding get() = _binding!!
    private val viewModel: QuickRegistrationVM by activityViewModels()

    companion object{
        var callBackListener: CallBackListener? = null
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = QuickRegistration1Binding.inflate(inflater)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("ClickableViewAccessibility", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)

        binding.model = viewModel
        binding.lifecycleOwner = this

        callBackListener = this
        OtpTimer.sendOtpTimerData = this



        binding.apply {
            editTextVeryfyOtp.setEnabled(false)

            if (viewModel.loginType == 1) {
                editTextGender.visibility = View.GONE
            } else if (viewModel.loginType == 2) {
                editTextGender.visibility = View.VISIBLE
            }

            editTextGender.singleClick {
                requireActivity().showDropDownDialog(type = 1) {
                    binding.editTextGender.setText(name)
                    when (position) {
                        0 -> viewModel.data.gender = "Male"
                        1 -> viewModel.data.gender = "Female"
                        2 -> viewModel.data.gender = "Other"
                    }
                }
            }


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
                                    someActivityResultLauncher.launch(intent!!)
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
                        editTextSendOtp.setEnabled(true)
                        editTextSendOtp.setBackgroundTintList(
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    getResources(), R.color._E79D46, null)))
                        QuickRegistration.callBackListener!!.onCallBack(21)
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
                        editTextSendOtp.setEnabled(true)
                        editTextSendOtp.setBackgroundTintList(
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    getResources(), R.color._E79D46, null)))
                        QuickRegistration.callBackListener!!.onCallBack(21)
                        editTextOtp.requestFocus()
                    }
                }
            })



            editTextAddress.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(count > 0){
                        checkValidation()
                    } else {
                        QuickRegistration.callBackListener!!.onCallBack(21)
                    }
                }
            })

        }
    }







    override fun onCallBack(pos: Int) {
        binding.apply {
            if( pos == 1){
                checkValidation()
            }
        }
    }

    fun checkValidation() {
        binding.apply {
            if(editTextFN.text.toString().isEmpty()){
                showSnackBar(getString(R.string.first_name))
            } else if (editTextLN.text.toString().isEmpty()){
                showSnackBar(getString(R.string.last_name))
            } else if (editTextMobileNumber.text.toString().isEmpty() || editTextMobileNumber.text.toString().length != 10){
                showSnackBar(getString(R.string.mobileNumber))
            } else if (viewModel.loginType == 2 && editTextGender.text.toString().isEmpty()) {
                showSnackBar(getString(R.string.gender))
            } else if (editTextAadhaarNumber.text.toString().isEmpty() || editTextAadhaarNumber.text.toString().length != 12){
                showSnackBar(getString(R.string.aadhaar_number))
            } else if (editTextAddress.text.toString().isEmpty()){
                showSnackBar(getString(R.string.address))
            } else {
                viewModel.data.vendor_first_name = editTextFN.text.toString()
                viewModel.data.vendor_last_name = editTextLN.text.toString()
                viewModel.data.mobile_no = editTextMobileNumber.text.toString()
                viewModel.data.gender = editTextGender.text.toString()
                viewModel.data.address = editTextAddress.text.toString()
                QuickRegistration.callBackListener!!.onCallBack(2)
            }
        }
    }


    var isTimer = ""
    @OptIn(DelicateCoroutinesApi::class)
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


