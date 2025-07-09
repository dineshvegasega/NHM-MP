package com.nhm.distributor.screens.main.distribution.forms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nhm.distributor.databinding.Form1Binding
import com.nhm.distributor.screens.main.distribution.DistributionViewModel
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Form1 : Fragment() {
    private val viewModel: DistributionViewModel by viewModels()
    private var _binding: Form1Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = Form1Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            radioGroupFatherHusbandRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    radioButtonFather.id -> viewModel.fatherHusbandType = 1
                    radioButtonHusband.id -> viewModel.fatherHusbandType = 2
                }
            }

            radioGroupCardAPLBPLGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    radioButtonAPL.id -> viewModel.cardTypeAPLBPL = "APL"
                    radioButtonBPL.id -> viewModel.cardTypeAPLBPL = "BPL"
                }
            }

            editTextGender.singleClick {
                requireActivity().showDropDownDialog(type = 1) {
                    editTextGender.setText(name)
                    when (position) {
                        0 -> viewModel.gender = "Male"
                        1 -> viewModel.gender = "Female"
                        2 -> viewModel.gender = "Other"
                    }
                }
            }


            btSignIn.singleClick {
                viewModel.name = editTextName.text.toString()
                viewModel.fatherHusband = editTextFatherHusband.text.toString()
                viewModel.mother = editTextMother.text.toString()
                viewModel.age = editTextAge.text.toString()
                viewModel.height = editTextHeight.text.toString()
                viewModel.weight = editTextWeight.text.toString()
                viewModel.numberOfMembers = editTextNumberOfMembers.text.toString()
                viewModel.numberOfChildren = editTextNumberOfChildrens.text.toString()
                viewModel.address = editTextAddress.text.toString()
                viewModel.dmcName = editTextDMCName.text.toString()
                viewModel.block = editTextBlock.text.toString()
                viewModel.mobileNumber = editTextMobileNumbar.text.toString()
                viewModel.districtState = editTextDistrictState.text.toString()
            }
        }
    }
}