package com.nhm.distributor.screens.main.NBPA.addForms

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.nhm.distributor.R
import com.nhm.distributor.databinding.Form2Binding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.interfaces.CallBackListener
import com.nhm.distributor.screens.main.NBPA.NBPAViewModel
import com.nhm.distributor.screens.main.NBPA.addForms.NBPA_Form1.Companion.formFill1
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody

@AndroidEntryPoint
class NBPA_Form2 : Fragment(), CallBackListener {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: Form2Binding? = null
    private val binding get() = _binding!!

    companion object {
        var callBackListener: CallBackListener? = null
        var tabPosition = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Form2Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
        callBackListener = this
        binding.apply {
            radioGroupPulmonaryRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    radioButtonPulmonary.id -> viewModel.typeOfPatient = 1
                    radioButtonExtraPulmonary.id -> viewModel.typeOfPatient = 2
                    radioButtonOther.id -> viewModel.typeOfPatient = 3
                }
            }

            ivMenu.singleClick {
                NBPA.callBackListener!!.onCallBack(1000)
            }

            editTextPatientCheckupDate.singleClick {
                requireActivity().showDropDownDialog(type = 20) {
                    editTextPatientCheckupDate.setText(title)
                    viewModel.patientCheckupDate = title
                }
            }
            editTextHemoglobinCheckupDate.singleClick {
                requireActivity().showDropDownDialog(type = 20) {
                    editTextHemoglobinCheckupDate.setText(title)
                    viewModel.hemoglobinCheckupDate = title
                }
            }
            editTextTreatmentEndDate.singleClick {
                requireActivity().showDropDownDialog(type = 20) {
                    editTextTreatmentEndDate.setText(title)
                    viewModel.treatmentSupporterEndDate = title
                }
            }


            if (viewModel.start == "no") {
                btSignIn.visibility = View.VISIBLE
                if (viewModel.isAadhaar == "yes"){
                    editTextAadhaarNumber.setText("" + viewModel.aadhaarNumber)
                    editTextAadhaarNumber.isEnabled = false
                }
                if (viewModel.isAadhaar == "no"){
                    editTextNakshayID.setText("" + viewModel.nakshayID)
                    editTextNakshayID.isEnabled = false
                }
            } else {
                var model = viewModel.editDataNew!!.data

                if (model.typeOfPatient == "1") {
                    radioButtonPulmonary.isChecked = true
                } else if (model.typeOfPatient == "2") {
                    radioButtonExtraPulmonary.isChecked = true
                } else {
                    radioButtonOther.isChecked = true
                }
                for (i in 0..<radioGroupPulmonaryRadioGroup.getChildCount()) {
                    radioGroupPulmonaryRadioGroup.getChildAt(i).setEnabled(false)
                }

                editTextPatientCheckupDate.setText("" + model.patientCheckupDate)
//                editTextHemoglobinLevelAge.setText(""+model.hemoglobinLevelAge)
//                editTextHemoglobinCheckupDate.setText(""+model.hemoglobinCheckupDate)
                if (model.hemoglobinLevelAge.isNullOrEmpty()) {
                    editTextHemoglobinLevelAge.setText("")
                } else {
                    editTextHemoglobinLevelAge.setText("" + model.hemoglobinLevelAge)
                }

                if (model.hemoglobinCheckupDate.isNullOrEmpty()) {
                    editTextHemoglobinCheckupDate.setText("")
                } else {
                    editTextHemoglobinCheckupDate.setText("" + model.hemoglobinCheckupDate)
                }

                if (model.muktiID.isNullOrEmpty()) {
                    editTextMuktiID.setText("")
                } else {
                    editTextMuktiID.setText("" + model.muktiID)
                }


                if (model.aadhaarNumber.isNullOrEmpty()) {
                    editTextAadhaarNumber.setText("")
                } else {
                    editTextAadhaarNumber.setText("" + model.aadhaarNumber)
                }


                if (model.nakshayID.isNullOrEmpty()) {
                    editTextNakshayID.setText("")
                } else {
                    editTextNakshayID.setText("" + model.nakshayID)
                }

//                editTextHemoglobinLevelAge.setText(model.hemoglobinLevelAge.getNotNullData())
//                editTextHemoglobinCheckupDate.setText(model.hemoglobinCheckupDate.getNotNullData())
//                editTextAadhaarNumber.setText(model.aadhaarNumber.getNotNullData())


//                editTextMuktiID.setText(""+model.muktiID)
//                editTextNakshayID.setText("" + model.nakshayID)
//                editTextAadhaarNumber.setText(""+model.aadhaarNumber)
                editTextBusiness.setText("" + model.business)
//                editTextBankAccount.setText(""+model.bankAccount)
//                editTextBankIFSC.setText(""+model.bankIFSC)
//                editTextTreatmentSupporterName.setText(""+model.treatmentSupporterName)
//                editTextTreatmentSupporterPost.setText(""+model.treatmentSupporterPost)
//                editTextTreatmentSupporterMobileNumber.setText(""+model.treatmentSupporterMobileNumber)
//                editTextTreatmentEndDate.setText(""+model.treatmentSupporterEndDate)
//                editTextTreatmentResult.setText(""+model.treatmentSupporterResult)


                if (model.bankAccount.isNullOrEmpty()) {
                    editTextBankAccount.setText("")
                } else {
                    editTextBankAccount.setText("" + model.bankAccount)
                }

                if (model.bankIFSC.isNullOrEmpty()) {
                    editTextBankIFSC.setText("")
                } else {
                    editTextBankIFSC.setText("" + model.bankIFSC)
                }

                if (model.treatmentSupporterName.isNullOrEmpty()) {
                    editTextTreatmentSupporterName.setText("")
                } else {
                    editTextTreatmentSupporterName.setText("" + model.treatmentSupporterName)
                }

                if (model.treatmentSupporterPost.isNullOrEmpty()) {
                    editTextTreatmentSupporterPost.setText("")
                } else {
                    editTextTreatmentSupporterPost.setText("" + model.treatmentSupporterPost)
                }

                if (model.treatmentSupporterMobileNumber.isNullOrEmpty()) {
                    editTextTreatmentSupporterMobileNumber.setText("")
                } else {
                    editTextTreatmentSupporterMobileNumber.setText("" + model.treatmentSupporterMobileNumber)
                }

                if (model.treatmentSupporterEndDate.isNullOrEmpty()) {
                    editTextTreatmentEndDate.setText("")
                } else {
                    editTextTreatmentEndDate.setText("" + model.treatmentSupporterEndDate)
                }

                if (model.treatmentSupporterResult.isNullOrEmpty()) {
                    editTextTreatmentResult.setText("")
                } else {
                    editTextTreatmentResult.setText("" + model.treatmentSupporterResult)
                }

                btSignIn.visibility = View.GONE
                editTextPatientCheckupDate.isEnabled = false
                editTextHemoglobinLevelAge.isEnabled = false
                editTextHemoglobinCheckupDate.isEnabled = false
                editTextMuktiID.isEnabled = false
                editTextNakshayID.isEnabled = false
                editTextAadhaarNumber.isEnabled = false
                editTextBusiness.isEnabled = false
                editTextBankAccount.isEnabled = false
                editTextBankIFSC.isEnabled = false
                editTextTreatmentSupporterName.isEnabled = false
                editTextTreatmentSupporterPost.isEnabled = false
                editTextTreatmentSupporterMobileNumber.isEnabled = false
                editTextTreatmentEndDate.isEnabled = false
                editTextTreatmentResult.isEnabled = false
                for (i in 0..<radioGroupPulmonaryRadioGroup.getChildCount()) {
                    radioGroupPulmonaryRadioGroup.getChildAt(i).setEnabled(false)
                }
            }

            btSignIn.singleClick {
                Log.e("TAG", "formFill1: " + formFill1)

                if (editTextPatientCheckupDate.text.toString() == "") {
                    showSnackBar(getString(R.string.selectCheckupDate))
                } else if (editTextNakshayID.text.toString() == "") {
                    showSnackBar(getString(R.string.enterNakshayID))
                } else if (editTextAadhaarNumber.text.toString() == "") {
                    showSnackBar(getString(R.string.enterAadhaarNumber))
                } else if (editTextBusiness.text.toString() == "") {
                    showSnackBar(getString(R.string.enterBusiness))
                } else {
                    viewModel.hemoglobinLevelAge = editTextHemoglobinLevelAge.text.toString()
                    viewModel.muktiID = editTextMuktiID.text.toString()
                    viewModel.nakshayID = editTextNakshayID.text.toString()
                    viewModel.aadhaarNumber = editTextAadhaarNumber.text.toString()
                    viewModel.business = editTextBusiness.text.toString()
                    viewModel.bankAccount = editTextBankAccount.text.toString()
                    viewModel.bankIFSC = editTextBankIFSC.text.toString()
                    viewModel.treatmentSupporterName =
                        editTextTreatmentSupporterName.text.toString()
                    viewModel.treatmentSupporterPost =
                        editTextTreatmentSupporterPost.text.toString()
                    viewModel.treatmentSupporterMobileNumber =
                        editTextTreatmentSupporterMobileNumber.text.toString()
                    viewModel.treatmentSupporterResult = editTextTreatmentResult.text.toString()
                    NBPA.callBackListener!!.onCallBack(1002)

                }
            }
        }
    }


    override fun onCallBack(pos: Int) {
        Log.e("TAG", "onCallBackNo2 " + pos)
//        NBPA.callBackListener!!.onCallBack(1000)
    }
}