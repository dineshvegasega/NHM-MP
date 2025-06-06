package com.nhm.distributor.screens.main.NBPA.editForms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nhm.distributor.databinding.EditForm1Binding
import com.nhm.distributor.screens.main.NBPA.NBPAViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NBPAEdit_Form1 : Fragment() {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: EditForm1Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = EditForm1Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
          binding.apply {
              var model = viewModel.editDataNew!!.data

              nbpaDetail1.apply {
                  editTextName.setText(""+model.name)
                  if (model.fatherHusbandType == 1) {
                      radioButtonFather.isChecked = true
                  } else {
                      radioButtonHusband.isChecked = true
                  }
                  for (i in 0..<radioGroupFatherHusbandRadioGroup.getChildCount()) {
                      radioGroupFatherHusbandRadioGroup.getChildAt(i).setEnabled(false)
                  }

                  editTextFatherHusband.setText(""+model.fatherHusband)
                  if (model.mother.isNullOrEmpty()){
                      editTextMother.setText("")
                  }else{
                      editTextMother.setText(""+model.mother)
                  }
                  editTextGender.setText(""+model.gender)
                  editTextAge.setText(""+model.age)
                  editTextHeight.setText(""+model.height)
                  editTextWeight.setText(""+model.weight)
                  editTextNumberOfMembers.setText(""+model.numberOfMembers)
                  editTextNumberOfChildrens.setText(""+model.numberOfChildren)
                  editTextAddress.setText(""+model.address)
                  editTextDMCName.setText(""+model.dmcName)
                  editTextBlock.setText(""+model.block)
                  editTextMobileNumbar.setText(""+model.mobileNumber)
                  editTextDistrictState.setText(""+model.districtState)

                  if (model.cardTypeAPLBPL == 1) {
                      radioButtonAPL.isChecked = true
                  } else if (model.cardTypeAPLBPL == 2) {
                      radioButtonBPL.isChecked = true
                  } else {
                      radioButtonOther.isChecked = true
                  }
                  for (i in 0..<radioGroupCardAPLBPLGroup.getChildCount()) {
                      radioGroupCardAPLBPLGroup.getChildAt(i).setEnabled(false)
                  }
              }


//              nbpaDetail1.apply {
//                  editTextName.isEnabled = false
//                  editTextFatherHusband.isEnabled = false
//                  editTextMother.isEnabled = false
//                  editTextGender.isEnabled = false
//                  editTextAge.isEnabled = false
//                  editTextHeight.isEnabled = false
//                  editTextWeight.isEnabled = false
//                  editTextNumberOfMembers.isEnabled = false
//                  editTextNumberOfChildrens.isEnabled = false
//                  editTextAddress.isEnabled = false
//                  editTextDMCName.isEnabled = false
//                  editTextBlock.isEnabled = false
//                  editTextMobileNumbar.isEnabled = false
//                  editTextDistrictState.isEnabled = false
//              }
          }
    }
}