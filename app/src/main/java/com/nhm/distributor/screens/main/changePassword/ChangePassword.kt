package com.nhm.distributor.screens.main.changePassword

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ChangePasswordBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.isValidPassword
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody

@AndroidEntryPoint
class ChangePassword : Fragment() {
    private val viewModel: ChangePasswordVM by viewModels()
    private var _binding: ChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChangePasswordBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(3)

        binding.apply {
            btSignIn.setEnabled(false)
            viewModel.isAgree.observe(viewLifecycleOwner, Observer {
                if (it == true){
                    btSignIn.setEnabled(true)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._E79D46, null)))
                } else {
                    btSignIn.setEnabled(false)
                    btSignIn.setBackgroundTintList(
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
                    start=editTextCreatePassword.getSelectionStart()
                    end=editTextCreatePassword.getSelectionEnd()
                    editTextCreatePassword.setTransformationMethod(null)
                    editTextCreatePassword.setSelection(start,end)
                }else{
                    counter = 0
                    imgCreatePassword.setImageResource(R.drawable.ic_eye_closed)
                    start=editTextCreatePassword.getSelectionStart()
                    end=editTextCreatePassword.getSelectionEnd()
                    editTextCreatePassword.setTransformationMethod(PasswordTransformationMethod())
                    editTextCreatePassword.setSelection(start,end)
                }
            }


            var counter2 = 0
            var start2: Int
            var end2: Int
            imgReEnterPassword.singleClick {
                if(counter2 == 0){
                    counter2 = 1
                    imgReEnterPassword.setImageResource(R.drawable.ic_eye_open)
                    start2=editTextReEnterPassword.getSelectionStart()
                    end2=editTextReEnterPassword.getSelectionEnd()
                    editTextReEnterPassword.setTransformationMethod(null)
                    editTextReEnterPassword.setSelection(start2,end2)
                }else{
                    counter2 = 0
                    imgReEnterPassword.setImageResource(R.drawable.ic_eye_closed)
                    start2=editTextReEnterPassword.getSelectionStart()
                    end2=editTextReEnterPassword.getSelectionEnd()
                    editTextReEnterPassword.setTransformationMethod(PasswordTransformationMethod())
                    editTextReEnterPassword.setSelection(start2,end2)
                }
            }



            editTextCreatePassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(!editTextCreatePassword.text.toString().isEmpty()){
                        if(editTextCreatePassword.text.toString().length >= 0 && editTextCreatePassword.text.toString().length < 8){
                            textCreatePasswrordMsg.setText(R.string.InvalidPassword)
                            textCreatePasswrordMsg.visibility = View.VISIBLE
                        } else if(!isValidPassword(editTextCreatePassword.text.toString().trim())){
                            textCreatePasswrordMsg.setText(R.string.InvalidPassword)
                            textCreatePasswrordMsg.visibility = View.VISIBLE
                        } else {
                            textCreatePasswrordMsg.visibility = View.GONE
                        }
                    }
                    editTextCreatePassword.requestFocus()
                }
            })


            editTextReEnterPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                @SuppressLint("SuspiciousIndentation")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(!editTextCreatePassword.text.toString().isEmpty()){
                        if(!editTextReEnterPassword.text.toString().isEmpty()){
                            if (editTextCreatePassword.text.toString() != editTextReEnterPassword.text.toString()){
                                textReEnterPasswrordMsg.setText(R.string.CreatePasswordReEnterPasswordisnotsame)
                                textReEnterPasswrordMsg.visibility = View.VISIBLE
                                getAgreeValue()
                            } else {
                                textReEnterPasswrordMsg.visibility = View.GONE
                                getAgreeValue()
                            }
                        }
                    }
                    editTextReEnterPassword.requestFocus()
                }
            })


            btSignIn.singleClick {
                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                        requestBody.addFormDataPart(mobile_number, "" + Gson().fromJson(loginUser, Login::class.java).mobile_no)
                        requestBody.addFormDataPart(password, editTextCreatePassword.text.toString())
                        if(networkFailed) {
                            viewModel.updatePassword(requestBody.build())
                        } else {
                            requireContext().callNetworkDialog()
                        }
                    }
                }



                viewModel.itemUpdatePasswordResult.observe(requireActivity(), Observer {
                    if (it) {
                        MainActivity.mainActivity.get()?.clearData()
                    }
                })
            }
        }
    }



    fun getAgreeValue(){
        binding.apply {
            if(editTextCreatePassword.text.toString().isEmpty()){
                viewModel.isAgree.value = false
            } else if(editTextCreatePassword.text.toString().length >= 0 && editTextCreatePassword.text.toString().length < 8){
                viewModel.isAgree.value = false
            } else if(!isValidPassword(editTextCreatePassword.text.toString().trim())){
                viewModel.isAgree.value = false
            } else if (editTextReEnterPassword.text.toString().isEmpty()){
                viewModel.isAgree.value = false
            } else if (editTextCreatePassword.text.toString() != editTextReEnterPassword.text.toString()){
                viewModel.isAgree.value = false
            }  else {
                viewModel.isAgree.value = true
            }
        }
    }


    override fun onDestroyView() {
        MainActivity.mainActivity.get()?.callFragment(4)
        viewModel.isAgree.value = false
        _binding = null
        super.onDestroyView()
    }

}