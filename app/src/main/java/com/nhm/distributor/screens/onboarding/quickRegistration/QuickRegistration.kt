package com.nhm.distributor.screens.onboarding.quickRegistration

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.nhm.distributor.R
import com.nhm.distributor.databinding.QuickRegistrationBinding
import com.nhm.distributor.screens.interfaces.CallBackListener
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.networking.*
import com.nhm.distributor.utils.OtpTimer
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.callPermissionDialog
import com.nhm.distributor.utils.getCameraPath
import com.nhm.distributor.utils.getMediaFilePathFor
import com.nhm.distributor.utils.isValidPassword
import com.nhm.distributor.utils.loadHtml
import com.nhm.distributor.utils.mainThread
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.showOptions
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.showSnackBarWithoutKeypad
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

@AndroidEntryPoint
class QuickRegistration : Fragment(), CallBackListener {
    private var _binding: QuickRegistrationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuickRegistrationVM by activityViewModels()
    var tabPosition: Int = 0;

    companion object {
        var callBackListener: CallBackListener? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = QuickRegistrationBinding.inflate(inflater)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)

        MainActivity.mainActivity.get()?.callFragment(0)
        callBackListener = this
        binding.apply {
//            val adapter= QuickRegistrationAdapter(requireActivity())
//            adapter.notifyDataSetChanged()
//            introViewPager.adapter=adapter
//            introViewPager.setUserInputEnabled(false)

            btSignIn.setEnabled(true)
            btSignIn.setBackgroundTintList(
                ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        getResources(), R.color._E79D46, null
                    )
                )
            )

            btDistributor.singleClick {
                viewModel.loginType = 1
                setSearchButtons()
//                adapter.notifyDataSetChanged()
//                introViewPager.adapter=adapter
                editTextGender.visibility = View.GONE
//                checkValidationClick()
            }

            btController.singleClick {
                viewModel.loginType = 2
                setSearchButtons()
//                adapter.notifyDataSetChanged()
//                introViewPager.adapter=adapter
                editTextGender.visibility = View.VISIBLE
//                checkValidationClick()
            }


            if (viewModel.loginType == 1) {
                editTextGender.visibility = View.GONE
            } else if (viewModel.loginType == 2) {
                editTextGender.visibility = View.VISIBLE
            }

            editTextGender.singleClick {
                requireActivity().showDropDownDialog(type = 1) {
                    binding.editTextGender.setText(name)
                    when (position) {
                        0 -> viewModel.data.gender = getString(R.string.maleGender)
                        1 -> viewModel.data.gender = getString(R.string.femaleGender)
                        2 -> viewModel.data.gender = getString(R.string.otherGender)
                    }
                }
            }


            layoutAadhaarImage.singleClick {
                imagePosition = 1
                callMediaPermissions()
            }

            layoutPassportSizeImage.singleClick {
                imagePosition = 2
                callMediaPermissions()
            }

//            viewModel.isAgree.observe(viewLifecycleOwner, Observer {
//                if (it == true){
//                    btSignIn.setEnabled(true)
//                    btSignIn.setBackgroundTintList(
//                        ColorStateList.valueOf(
//                            ResourcesCompat.getColor(
//                                getResources(), R.color._E79D46, null)))
//                } else {
//                    btSignIn.setEnabled(false)
//                    btSignIn.setBackgroundTintList(
//                        ColorStateList.valueOf(
//                            ResourcesCompat.getColor(
//                                getResources(), R.color._999999, null)))
//                }
//            })
//
//            viewModel.isSendMutable.value = false
//            viewModel.isSendMutable.observe(viewLifecycleOwner, Observer {
//                if (it == true){
//                    btSignIn.setEnabled(true)
//                    btSignIn.setBackgroundTintList(
//                        ColorStateList.valueOf(
//                            ResourcesCompat.getColor(
//                                getResources(), R.color._E79D46, null)))
//                }
//            })


            var counter = 0
            var start: Int
            var end: Int
            imgCreatePassword.singleClick {
                if (counter == 0) {
                    counter = 1
                    imgCreatePassword.setImageResource(R.drawable.ic_eye_open)
                    start = editTextCreatePassword.getSelectionStart()
                    end = editTextCreatePassword.getSelectionEnd()
                    editTextCreatePassword.setTransformationMethod(null)
                    editTextCreatePassword.setSelection(start, end)
                } else {
                    counter = 0
                    imgCreatePassword.setImageResource(R.drawable.ic_eye_closed)
                    start = editTextCreatePassword.getSelectionStart()
                    end = editTextCreatePassword.getSelectionEnd()
                    editTextCreatePassword.setTransformationMethod(PasswordTransformationMethod())
                    editTextCreatePassword.setSelection(start, end)
                }
            }


            var counter2 = 0
            var start2: Int
            var end2: Int
            imgReEnterPassword.singleClick {
                if (counter2 == 0) {
                    counter2 = 1
                    imgReEnterPassword.setImageResource(R.drawable.ic_eye_open)
                    start2 = editTextReEnterPassword.getSelectionStart()
                    end2 = editTextReEnterPassword.getSelectionEnd()
                    editTextReEnterPassword.setTransformationMethod(null)
                    editTextReEnterPassword.setSelection(start2, end2)
                } else {
                    counter2 = 0
                    imgReEnterPassword.setImageResource(R.drawable.ic_eye_closed)
                    start2 = editTextReEnterPassword.getSelectionStart()
                    end2 = editTextReEnterPassword.getSelectionEnd()
                    editTextReEnterPassword.setTransformationMethod(PasswordTransformationMethod())
                    editTextReEnterPassword.setSelection(start2, end2)
                }
            }


//            cbRememberMe.singleClick {
//                checkValidationClick()
//            }

            textTerms.singleClick {
                viewModel.show()
                mainThread {
                    openDialog(3)
                }
            }


//
//            editTextCreatePassword.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                }
//
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    if(!editTextCreatePassword.text.toString().isEmpty()){
//                        if(editTextCreatePassword.text.toString().length >= 0 && editTextCreatePassword.text.toString().length < 8){
//                            textCreatePasswrordMsg.setText(R.string.InvalidPassword)
//                            textCreatePasswrordMsg.visibility = View.VISIBLE
//                        } else if(!isValidPassword(editTextCreatePassword.text.toString().trim())){
//                            textCreatePasswrordMsg.setText(R.string.InvalidPassword)
//                            textCreatePasswrordMsg.visibility = View.VISIBLE
//                        } else {
//                            textCreatePasswrordMsg.visibility = View.GONE
//                        }
//                    }
//                    editTextCreatePassword.requestFocus()
//                }
//            })
//
//
//            editTextReEnterPassword.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                }
//
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                }
//
//                @SuppressLint("SuspiciousIndentation")
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    if(!editTextCreatePassword.text.toString().isEmpty()){
//                        if(!editTextReEnterPassword.text.toString().isEmpty()){
//                            if (editTextCreatePassword.text.toString() != editTextReEnterPassword.text.toString()){
//                                textReEnterPasswrordMsg.setText(R.string.CreatePasswordReEnterPasswordisnotsame)
//                                textReEnterPasswrordMsg.visibility = View.VISIBLE
//                                checkValidation()
//                            } else {
//                                textReEnterPasswrordMsg.visibility = View.GONE
//                                checkValidation()
//                            }
//                        }
//                    }
//                    editTextReEnterPassword.requestFocus()
//                }
//            })
//


            btSignIn.singleClick {
//                if (tabPosition == 0){
//                    QuickRegistration1.callBackListener!!.onCallBack(1)
//                } else if (tabPosition == 1){
//                    QuickRegistration2.callBackListener!!.onCallBack(3)
//                }
//                loadProgress(tabPosition)


                checkValidationClick()

            }

            textBack.singleClick {
//                if (tabPosition == 0){
//                    view.findNavController().navigateUp()
//                } else if (tabPosition == 1){
//                    btSignIn.setEnabled(true)
//                    btSignIn.setBackgroundTintList(
//                        ColorStateList.valueOf(
//                            ResourcesCompat.getColor(
//                                getResources(), R.color._E79D46, null)))
//                    introViewPager.setCurrentItem(0, false)
//                    btSignIn.setText(getString(R.string.continues))
//                }
//                loadProgress(tabPosition)
                view.findNavController().navigateUp()
            }


//            introViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                override fun onPageScrolled(
//                    position: Int,
//                    positionOffset: Float,
//                    positionOffsetPixels: Int
//                ) {
//                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//                }
//
//                override fun onPageSelected(position: Int) {
//                    introViewPager.requestLayout()
//                    super.onPageSelected(position)
//                    tabPosition = position
//                    if(position == 1) {
//                        btSignIn.setEnabled(false)
//                        btSignIn.setBackgroundTintList(
//                            ColorStateList.valueOf(
//                                ResourcesCompat.getColor(
//                                    getResources(), R.color._999999, null)))
//                    }
//                }
//
//                override fun onPageScrollStateChanged(state: Int) {
//                    super.onPageScrollStateChanged(state)
//                }
//            })
//
//
//            introViewPager.setPageTransformer { page, position ->
//                introViewPager.updatePagerHeightForChild(page)
//            }
        }
    }


    private fun loadProgress(tabPosition: Int) {
        val forOne = 100 / 2
        val myPro = tabPosition + 1
        val myProTotal = forOne * myPro
        binding.loading.progress = myProTotal
    }

    override fun onCallBack(pos: Int) {
        binding.apply {
            if (pos == 21) {
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (pos == 2) {
                introViewPager.setCurrentItem(1, false)
                btSignIn.setText(getString(R.string.RegisterNow))
            } else if (pos == 4) {
                val obj: JSONObject = JSONObject().apply {
                    put(vendor_first_name, viewModel.data.vendor_first_name)
                    put(vendor_last_name, viewModel.data.vendor_last_name)
                    put(mobile_no, viewModel.data.mobile_no)
                    put(password, viewModel.data.password)
                    put(user_type, USER_TYPE)
                }
//                if(networkFailed) {
//                    viewModel.register(view = requireView(), obj)
//                } else {
//                    requireContext().callNetworkDialog()
//                }
            }
        }
        loadProgress(tabPosition)
    }


    override fun onDestroyView() {
        viewModel.isAgree.value = false
        OtpTimer.sendOtpTimerData = null
        OtpTimer.stopTimer()
        _binding = null
        super.onDestroyView()
    }


    private fun setSearchButtons() {
        if (viewModel.loginType == 1) {
            binding.btDistributor.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.white
                )
            )
            binding.btController.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.black
                )
            )
            binding.btDistributor.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E2861d))
            binding.btController.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btDistributor.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E2861d))
            binding.btController.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E2861d))
        } else {
            binding.btDistributor.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.black
                )
            )
            binding.btController.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.white
                )
            )
            binding.btDistributor.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btController.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E2861d))
            binding.btDistributor.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E2861d))
            binding.btController.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E2861d))
        }

    }


    fun checkValidationClick() {
        binding.apply {
            if (editTextFN.text.toString().isEmpty()) {
                showSnackBar(getString(R.string.first_name))
            } else if (editTextLN.text.toString().isEmpty()) {
                showSnackBar(getString(R.string.last_name))
            } else if((binding.editTextMobileNumber.text.toString().isEmpty() || binding.editTextMobileNumber.text.toString().length != 10) ||
                (binding.editTextMobileNumber.text.toString().startsWith("0") ||
                        binding.editTextMobileNumber.text.toString().startsWith("1") ||
                        binding.editTextMobileNumber.text.toString().startsWith("2") ||
                        binding.editTextMobileNumber.text.toString().startsWith("3") ||
                        binding.editTextMobileNumber.text.toString().startsWith("4") ||
                        binding.editTextMobileNumber.text.toString().startsWith("5")
                        )){
                showSnackBar(getString(R.string.enterMobileNumber))
            } else if (viewModel.loginType == 2 && editTextGender.text.toString().isEmpty()) {
                showSnackBar(getString(R.string.gender))
            } else if (editTextAadhaarNumber.text.toString()
                    .isEmpty() || editTextAadhaarNumber.text.toString().length != 12
            ) {
                showSnackBar(getString(R.string.aadhaar_number))
            } else if (viewModel.data.aadhar_card_doc == null) {
                showSnackBar(getString(R.string.aadhaar_image))
            } else if (viewModel.data.profile_image_name == null) {
                showSnackBar(getString(R.string.profiler_image))
            } else if (editTextAddress.text.toString().isEmpty()) {
                showSnackBar(getString(R.string.address))
            } else if (editTextCreatePassword.text.toString().isEmpty()) {
                showSnackBar(getString(R.string.createPassword))
            } else if (editTextCreatePassword.text.toString().length >= 0 && editTextCreatePassword.text.toString().length < 8) {
                showSnackBar(getString(R.string.InvalidPassword))
            } else if (!isValidPassword(editTextCreatePassword.text.toString().trim())) {
                showSnackBar(getString(R.string.InvalidPassword))
            } else if (editTextReEnterPassword.text.toString().isEmpty()) {
                showSnackBar(getString(R.string.reEnterPassword))
            } else if (editTextReEnterPassword.text.toString().length >= 0 && editTextReEnterPassword.text.toString().length < 8) {
                showSnackBar(getString(R.string.InvalidPassword))
            } else if (!isValidPassword(editTextReEnterPassword.text.toString().trim())) {
                showSnackBar(getString(R.string.InvalidPassword))
            } else if (editTextCreatePassword.text.toString() != editTextReEnterPassword.text.toString()) {
                showSnackBar(getString(R.string.CreatePasswordReEnterPasswordisnotsame))
            } else if (cbRememberMe.isChecked == false) {
                showSnackBar(getString(R.string.Pleaseselectagree))
//                btSignIn.setEnabled(false)
//                btSignIn.setBackgroundTintList(
//                    ColorStateList.valueOf(
//                        ResourcesCompat.getColor(
//                            getResources(), R.color._999999, null)))
//            } else if (cbRememberMe.isChecked == true){
//                showSnackBarWithoutKeypad(getString(R.string.Pleaseselectagree))
//                btSignIn.setEnabled(true)
//                btSignIn.setBackgroundTintList(
//                    ColorStateList.valueOf(
//                        ResourcesCompat.getColor(
//                            getResources(), R.color._E79D46, null)))
            } else {
                viewModel.data.vendor_first_name = editTextFN.text.toString()
                viewModel.data.vendor_last_name = editTextLN.text.toString()
                viewModel.data.mobile_no = editTextMobileNumber.text.toString()
                viewModel.data.aadhar_card = editTextAadhaarNumber.text.toString()
//                viewModel.data.aadhar_card_doc = editTextAadhaarNumber.text.toString()
                if (viewModel.loginType == 1) {
                    viewModel.data.gender = ""
                } else if (viewModel.loginType == 2) {
                    viewModel.data.gender = editTextGender.text.toString()
                }
                viewModel.data.address = editTextAddress.text.toString()
                viewModel.data.password = editTextCreatePassword.text.toString()


//                btSignIn.setEnabled(true)
//                btSignIn.setBackgroundTintList(
//                    ColorStateList.valueOf(
//                        ResourcesCompat.getColor(
//                            getResources(), R.color._E79D46, null)))

                val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
//                    .addFormDataPart(user_role, USER_TYPE)


                if (viewModel.data.vendor_first_name != null) {
                    requestBody.addFormDataPart(vendor_first_name, viewModel.data.vendor_first_name!!)
                }
                if (viewModel.data.vendor_last_name != null) {
                    requestBody.addFormDataPart(vendor_last_name, viewModel.data.vendor_last_name!!)
                }
                if (viewModel.data.mobile_no != null) {
                    requestBody.addFormDataPart(mobile_no, viewModel.data.mobile_no!!)
                }
                if (viewModel.data.aadhar_card != null) {
                    requestBody.addFormDataPart(aadhar_card, viewModel.data.aadhar_card!!)
                }
                if (viewModel.data.address != null) {
                    requestBody.addFormDataPart(residential_address, viewModel.data.address!!)
                }
                if (viewModel.data.password != null) {
                    requestBody.addFormDataPart(password, viewModel.data.password!!)
                }

                if(viewModel.loginType == 1){
                    requestBody.addFormDataPart(user_type, USER_TYPE)
                    requestBody.addFormDataPart(user_role, USER_TYPE)
                }
                if(viewModel.loginType == 2) {
                    requestBody.addFormDataPart(user_type, USER_TYPE_ADMIN)
                    requestBody.addFormDataPart(user_role, USER_TYPE_ADMIN)

                    if (viewModel.data.gender != null) {
                        requestBody.addFormDataPart(gender, viewModel.data.gender!!)
                    }
                }

                if (viewModel.data.aadhar_card_doc != null) {
                    requestBody.addFormDataPart(
                        aadhar_card_doc,
                        File(viewModel.data.aadhar_card_doc!!).name,
                        File(viewModel.data.aadhar_card_doc!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.profile_image_name != null) {
                    requestBody.addFormDataPart(
                        profile_image_name,
                        File(viewModel.data.profile_image_name!!).name,
                        File(viewModel.data.profile_image_name!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }
//
//                val obj: JSONObject = JSONObject().apply {
//                    put(vendor_first_name, viewModel.data.vendor_first_name)
//                    put(vendor_last_name, viewModel.data.vendor_last_name)
//                    put(mobile_no, viewModel.data.mobile_no)
//                    put(aadhar_card, viewModel.data.aadhar_card)
//                    put(gender, viewModel.data.gender)
//                    put(residential_address, viewModel.data.address)
//                    put(password, viewModel.data.password)
//                    if (viewModel.loginType == 1) {
//                        put(user_type, USER_TYPE)
//                        put(user_role, USER_TYPE)
//                    }
//                    if (viewModel.loginType == 2) {
//                        put(user_type, USER_TYPE_ADMIN)
//                        put(user_role, USER_TYPE_ADMIN)
//                    }
//
//                    if (viewModel.data.aadhar_card != null) {
//                        requestBody.addFormDataPart(
//                            aadhar_card_doc,
//                            viewModel.data.aadhar_card!!
//                        )
//                    }
//                }

                if(networkFailed) {
                    viewModel.registerWithFiles(
                        view = requireView(),
                        requestBody.build(),
                        "" + viewModel.data.vendor_first_name!!
                    )
//                    viewModel.register(view = requireView(), obj)
                } else {
                    requireContext().callNetworkDialog()
                }
            }
        }
    }


    fun checkValidation() {
        binding.apply {
            if (editTextFN.text.toString().isEmpty()) {
                showSnackBarWithoutKeypad(getString(R.string.first_name))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (editTextLN.text.toString().isEmpty()) {
                showSnackBarWithoutKeypad(getString(R.string.last_name))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (editTextMobileNumber.text.toString()
                    .isEmpty() || editTextMobileNumber.text.toString().length != 10
            ) {
                showSnackBarWithoutKeypad(getString(R.string.mobileNumber))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (viewModel.loginType == 2 && editTextGender.text.toString().isEmpty()) {
                showSnackBarWithoutKeypad(getString(R.string.gender))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (editTextAadhaarNumber.text.toString()
                    .isEmpty() || editTextAadhaarNumber.text.toString().length != 12
            ) {
                showSnackBarWithoutKeypad(getString(R.string.aadhaar_number))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (editTextAddress.text.toString().isEmpty()) {
                showSnackBarWithoutKeypad(getString(R.string.address))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (editTextCreatePassword.text.toString().isEmpty()) {
                showSnackBarWithoutKeypad(getString(R.string.createPassword))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (editTextCreatePassword.text.toString().length >= 0 && editTextCreatePassword.text.toString().length < 8) {
                showSnackBarWithoutKeypad(getString(R.string.InvalidPassword))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (!isValidPassword(editTextCreatePassword.text.toString().trim())) {
                showSnackBarWithoutKeypad(getString(R.string.InvalidPassword))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (editTextReEnterPassword.text.toString().isEmpty()) {
                showSnackBarWithoutKeypad(getString(R.string.reEnterPassword))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (editTextReEnterPassword.text.toString().length >= 0 && editTextReEnterPassword.text.toString().length < 8) {
                showSnackBarWithoutKeypad(getString(R.string.InvalidPassword))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (!isValidPassword(editTextReEnterPassword.text.toString().trim())) {
                showSnackBarWithoutKeypad(getString(R.string.InvalidPassword))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (editTextCreatePassword.text.toString() != editTextReEnterPassword.text.toString()) {
                showSnackBarWithoutKeypad(getString(R.string.CreatePasswordReEnterPasswordisnotsame))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (cbRememberMe.isChecked == false) {
                showSnackBarWithoutKeypad(getString(R.string.Pleaseselectagree))
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (cbRememberMe.isChecked == true) {
                btSignIn.setEnabled(true)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._E79D46, null
                        )
                    )
                )
            } else {
                viewModel.data.vendor_first_name = editTextFN.text.toString()
                viewModel.data.vendor_last_name = editTextLN.text.toString()
                viewModel.data.mobile_no = editTextMobileNumber.text.toString()
                viewModel.data.aadhar_card = editTextAadhaarNumber.text.toString()
                if (viewModel.loginType == 1) {
                    viewModel.data.gender = ""
                } else if (viewModel.loginType == 2) {
                    viewModel.data.gender = editTextGender.text.toString()
                }
                viewModel.data.address = editTextAddress.text.toString()
                viewModel.data.password = editTextCreatePassword.text.toString()


                btSignIn.setEnabled(true)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._E79D46, null
                        )
                    )
                )

            }
        }
    }


    private fun openDialog(type: Int) {
        val mybuilder = Dialog(requireActivity())
        mybuilder.setContentView(R.layout.dialog_load_html)
        mybuilder.show()
        val window = mybuilder.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(R.color._00000000)
        val yes = mybuilder.findViewById<AppCompatImageView>(R.id.imageCross)
        val title = mybuilder.findViewById<AppCompatTextView>(R.id.textTitleMain)
        val text = mybuilder.findViewById<AppCompatTextView>(R.id.textTitleText)
        when (type) {
            1 -> title.text = resources.getString(R.string.about_us)
            2 -> title.text = resources.getString(R.string.privacy_policy)
            3 -> title.text = resources.getString(R.string.terms_amp_conditions)
        }
        requireContext().loadHtml(type) {
            text.text = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        yes?.singleClick {
            mybuilder.dismiss()
        }
        viewModel.hide()
    }


    var imagePosition = 0
    @SuppressLint("MissingPermission")
    private var pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            lifecycleScope.launch {
                if (uri != null) {
                    when (imagePosition) {
                        1 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
                            viewModel.data.aadhar_card_doc = compressedImageFile.path
                            binding.textViewAadhaarImage.setText(File(viewModel.data.aadhar_card_doc!!).name)
                        }
                        2 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
                            viewModel.data.profile_image_name = compressedImageFile.path
                            binding.textViewPassportSizeImage.setText(File(viewModel.data.profile_image_name!!).name)
                        }
                    }
                }
            }
        }


    var uriReal: Uri? = null
    @SuppressLint("MissingPermission")
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        lifecycleScope.launch {
            if (uri == true) {

                when (imagePosition) {
                    1 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
                        viewModel.data.aadhar_card_doc = compressedImageFile.path
                        binding.textViewAadhaarImage.setText(compressedImageFile.name)
                    }
                    2 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
                        viewModel.data.profile_image_name = compressedImageFile.path
                        binding.textViewPassportSizeImage.setText(File(viewModel.data.profile_image_name!!).name)
                    }
                }
            }
        }
    }



    private fun callMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            )
        } else {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }


    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            if (!permissions.entries.toString().contains("false")) {
                requireActivity().showOptions {
                    when (this) {
                        1 -> forCamera()
                        2 -> forGallery()
                    }
                }
            } else {
                requireActivity().callPermissionDialog {
                    someActivityResultLauncher.launch(this)
                }
            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
    }




    private fun forCamera() {
        requireActivity().getCameraPath {
            uriReal = this
            captureMedia.launch(uriReal)
        }
    }

    private fun forGallery() {
        requireActivity().runOnUiThread() {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }


}