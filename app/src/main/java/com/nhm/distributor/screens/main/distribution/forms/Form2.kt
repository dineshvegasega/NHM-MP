package com.nhm.distributor.screens.main.distribution.forms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nhm.distributor.databinding.Form2Binding
import com.nhm.distributor.screens.main.distribution.DistributionViewModel
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class Form2 : Fragment() {
    private val viewModel: DistributionViewModel by viewModels()
    private var _binding: Form2Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Form2Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            radioGroupPulmonaryRadioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                    when (checkedId) {
                        radioButtonPulmonary.id ->  viewModel.typeOfPatient = "Pulmonary"
                        radioButtonExtraPulmonary.id ->  viewModel.typeOfPatient = "Extra Pulmonary"
                        radioButtonOther.id ->  viewModel.typeOfPatient = "Other"
                    }
                }
            })

            editTextPatientCheckupDate.singleClick {
                requireActivity().showDropDownDialog(type = 20){
                    editTextPatientCheckupDate.setText(title)
                    viewModel.patientCheckupDate = title
                }
            }
            editTextHemoglobinCheckupDate.singleClick {
                requireActivity().showDropDownDialog(type = 20){
                    editTextHemoglobinCheckupDate.setText(title)
                    viewModel.hemoglobinCheckupDate = title
                }
            }
            editTextTreatmentEndDate.singleClick {
                requireActivity().showDropDownDialog(type = 20){
                    editTextTreatmentEndDate.setText(title)
                    viewModel.treatmentSupporterEndDate = title
                }
            }


        }
    }
}