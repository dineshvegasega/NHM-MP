package com.nhm.distributor.screens.main.NBPA.viewForms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nhm.distributor.databinding.EditForm2Binding
import com.nhm.distributor.screens.main.NBPA.NBPAViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NBPAView_Form2 : Fragment() {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: EditForm2Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = EditForm2Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)

        binding.apply {

//            ivMenu.singleClick {
//                NBPAEdit.callBackListener!!.onCallBack(1000)
//            }

            var model = viewModel.editDataNew!!.data

            nbpaDetail2.apply {
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

                editTextPatientCheckupDate.setText(""+model.patientCheckupDate)
//                editTextHemoglobinLevelAge.setText(""+model.hemoglobinLevelAge)
//                editTextHemoglobinCheckupDate.setText(""+model.hemoglobinCheckupDate)
                if (model.hemoglobinLevelAge.isNullOrEmpty()){
                    editTextHemoglobinLevelAge.setText("")
                }else{
                    editTextHemoglobinLevelAge.setText(""+model.hemoglobinLevelAge)
                }

                if (model.hemoglobinCheckupDate.isNullOrEmpty()){
                    editTextHemoglobinCheckupDate.setText("")
                }else{
                    editTextHemoglobinCheckupDate.setText(""+model.hemoglobinCheckupDate)
                }

                if (model.muktiID.isNullOrEmpty()){
                    editTextMuktiID.setText("")
                }else{
                    editTextMuktiID.setText(""+model.muktiID)
                }

                if (model.aadhaarNumber.isNullOrEmpty()){
                    editTextAadhaarNumber.setText("")
                }else{
                    editTextAadhaarNumber.setText(""+model.aadhaarNumber)
                }

//                editTextMuktiID.setText(""+model.muktiID)
                editTextNakshayID.setText(""+model.nakshayID)
//                editTextAadhaarNumber.setText(""+model.aadhaarNumber)
                editTextBusiness.setText(""+model.business)
//                editTextBankAccount.setText(""+model.bankAccount)
//                editTextBankIFSC.setText(""+model.bankIFSC)
//                editTextTreatmentSupporterName.setText(""+model.treatmentSupporterName)
//                editTextTreatmentSupporterPost.setText(""+model.treatmentSupporterPost)
//                editTextTreatmentSupporterMobileNumber.setText(""+model.treatmentSupporterMobileNumber)
//                editTextTreatmentEndDate.setText(""+model.treatmentSupporterEndDate)
//                editTextTreatmentResult.setText(""+model.treatmentSupporterResult)


                if (model.bankAccount.isNullOrEmpty()){
                    editTextBankAccount.setText("")
                }else{
                    editTextBankAccount.setText(""+model.bankAccount)
                }

                if (model.bankIFSC.isNullOrEmpty()){
                    editTextBankIFSC.setText("")
                }else{
                    editTextBankIFSC.setText(""+model.bankIFSC)
                }

                if (model.treatmentSupporterName.isNullOrEmpty()){
                    editTextTreatmentSupporterName.setText("")
                }else{
                    editTextTreatmentSupporterName.setText(""+model.treatmentSupporterName)
                }

                if (model.treatmentSupporterPost.isNullOrEmpty()){
                    editTextTreatmentSupporterPost.setText("")
                }else{
                    editTextTreatmentSupporterPost.setText(""+model.treatmentSupporterPost)
                }

                if (model.treatmentSupporterMobileNumber.isNullOrEmpty()){
                    editTextTreatmentSupporterMobileNumber.setText("")
                }else{
                    editTextTreatmentSupporterMobileNumber.setText(""+model.treatmentSupporterMobileNumber)
                }

                if (model.treatmentSupporterEndDate.isNullOrEmpty()){
                    editTextTreatmentEndDate.setText("")
                }else{
                    editTextTreatmentEndDate.setText(""+model.treatmentSupporterEndDate)
                }

                if (model.treatmentSupporterResult.isNullOrEmpty()){
                    editTextTreatmentResult.setText("")
                }else{
                    editTextTreatmentResult.setText(""+model.treatmentSupporterResult)
                }
            }

            btSignIn.visibility = View.GONE

            nbpaDetail2.apply {
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
            }
        }
    }
}