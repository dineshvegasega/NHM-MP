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
import com.nhm.distributor.R
import com.nhm.distributor.databinding.Form1Binding
import com.nhm.distributor.screens.interfaces.CallBackListener
import com.nhm.distributor.screens.main.NBPA.NBPAViewModel
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NBPA_Form1 : Fragment() , CallBackListener {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: Form1Binding? = null
    private val binding get() = _binding!!

    companion object {
        var callBackListener: CallBackListener? = null
        var tabPosition = 0
        var formFill1 = false
    }

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
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
        callBackListener = this

        binding.apply {
            radioGroupFatherHusbandRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    radioButtonFather.id -> viewModel.fatherHusbandType = 1
                    radioButtonHusband.id -> viewModel.fatherHusbandType = 2
                }
            }

            radioGroupCardAPLBPLGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    radioButtonAPL.id -> viewModel.cardTypeAPLBPL = 1
                    radioButtonBPL.id -> viewModel.cardTypeAPLBPL = 2
                    radioButtonOther.id -> viewModel.cardTypeAPLBPL = 3
                }
            }

            editTextGender.singleClick {
                requireActivity().showDropDownDialog(type = 1) {
                    editTextGender.setText(name)
                    when (position) {
                        0 -> viewModel.gender = getString(R.string.maleGender)
                        1 -> viewModel.gender = getString(R.string.femaleGender)
                        2 -> viewModel.gender = getString(R.string.otherGender)
                    }
                }
            }

            editTextDMCName.singleClick {
                requireActivity().showDropDownDialog(type = 23) {
                    editTextDMCName.setText(name)

                    viewModel.dmcName = name

//                    when (position) {
//                        0 -> viewModel.gender = getString(R.string.maleGender)
//                        1 -> viewModel.gender = getString(R.string.femaleGender)
//                        2 -> viewModel.gender = getString(R.string.otherGender)
//                    }
                }
            }


            if (viewModel.start == "no") {
                btSignIn.visibility = View.VISIBLE
            } else {
                val model = viewModel.editDataNew!!.data

                editTextName.setText("" + model.name)
                if (model.fatherHusbandType == 1) {
                    radioButtonFather.isChecked = true
                } else {
                    radioButtonHusband.isChecked = true
                }
                for (i in 0..<radioGroupFatherHusbandRadioGroup.getChildCount()) {
                    radioGroupFatherHusbandRadioGroup.getChildAt(i).setEnabled(false)
                }

                editTextFatherHusband.setText("" + model.fatherHusband)
                if (model.mother.isNullOrEmpty()) {
                    editTextMother.setText("")
                } else {
                    editTextMother.setText("" + model.mother)
                }
//                editTextMother.setText(model.mother.getNotNullData())
                editTextGender.setText("" + model.gender)
                editTextAge.setText("" + model.age)
                editTextHeight.setText("" + model.height)
                editTextWeight.setText("" + model.weight)
                editTextNumberOfMembers.setText("" + model.numberOfMembers)
                editTextNumberOfChildrens.setText("" + model.numberOfChildren)
                editTextAddress.setText("" + model.address)
                editTextDMCName.setText("" + model.dmcName)
                editTextBlock.setText("" + model.block)
                editTextMobileNumbar.setText("" + model.mobileNumber)
                editTextDistrictState.setText("" + model.districtState)
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

                btSignIn.visibility = View.GONE
                editTextName.isEnabled = false
                editTextFatherHusband.isEnabled = false
                editTextMother.isEnabled = false
                editTextGender.isEnabled = false
                editTextAge.isEnabled = false
                editTextHeight.isEnabled = false
                editTextWeight.isEnabled = false
                editTextNumberOfMembers.isEnabled = false
                editTextNumberOfChildrens.isEnabled = false
                editTextAddress.isEnabled = false
                editTextDMCName.isEnabled = false
                editTextBlock.isEnabled = false
                editTextMobileNumbar.isEnabled = false
                editTextDistrictState.isEnabled = false
                for (i in 0..<radioGroupFatherHusbandRadioGroup.getChildCount()) {
                    radioGroupFatherHusbandRadioGroup.getChildAt(i).setEnabled(false)
                }
                for (i in 0..<radioGroupCardAPLBPLGroup.getChildCount()) {
                    radioGroupCardAPLBPLGroup.getChildAt(i).setEnabled(false)
                }
            }

            btSignIn.singleClick {
                getData(true)
            }
        }
    }



    override fun onCallBack(pos: Int) {
        getData(false)
    }

//var mapData : HashMap<String, Boolean> = HashMap()


    private fun getData(isButton: Boolean) {
        binding.apply {
            if (editTextName.text.toString() == "") {
                showSnackBar(getString(R.string.enterName))
                formFill1 = false
            } else if (editTextFatherHusband.text.toString() == "") {
                showSnackBar(
                    if (viewModel.fatherHusbandType == 1) {
                        getString(R.string.enterFatherName)
                    } else {
                        getString(R.string.enterHusbandName)
                    }
                )
                formFill1 = false
//                } else if (editTextMother.text.toString() == "") {
//                    showSnackBar(getString(R.string.enterMotherName))
            } else if (editTextGender.text.toString() == "") {
                showSnackBar(getString(R.string.selectGender))
                formFill1 = false
            } else if (editTextAge.text.toString() == "") {
                showSnackBar(getString(R.string.enterAge))
                formFill1 = false
            } else if (editTextHeight.text.toString() == "") {
                showSnackBar(getString(R.string.enterHeight))
                formFill1 = false
            } else if (editTextWeight.text.toString() == "") {
                showSnackBar(getString(R.string.enterWeight))
                formFill1 = false
            } else if (editTextNumberOfMembers.text.toString() == "") {
                showSnackBar(getString(R.string.enterNumbersOfMembers))
                formFill1 = false
            } else if (editTextNumberOfChildrens.text.toString() == "") {
                showSnackBar(getString(R.string.enterNumbersOfChildrens))
                formFill1 = false
            } else if (editTextAddress.text.toString() == "") {
                showSnackBar(getString(R.string.enterAddress))
                formFill1 = false
            } else if (editTextDMCName.text.toString() == "") {
                showSnackBar(getString(R.string.enterDMCName))
                formFill1 = false
            } else if (editTextBlock.text.toString() == "") {
                showSnackBar(getString(R.string.enterBlock))
                formFill1 = false
            } else if (editTextMobileNumbar.text.toString() == "") {
                showSnackBar(getString(R.string.enterMobileNumberForm))
                formFill1 = false
            } else if (editTextMobileNumbar.text.toString().length != 10 ||
                        editTextMobileNumbar.text.toString().startsWith("0") ||
                        editTextMobileNumbar.text.toString().startsWith("1") ||
                        editTextMobileNumbar.text.toString().startsWith("2") ||
                        editTextMobileNumbar.text.toString().startsWith("3") ||
                        editTextMobileNumbar.text.toString().startsWith("4") ||
                        editTextMobileNumbar.text.toString().startsWith("5")
                            ) {
                showSnackBar(getString(R.string.enterMobileNumber))
                formFill1 = false
            } else if (editTextDistrictState.text.toString() == "") {
                showSnackBar(getString(R.string.enterDistrictState))
                formFill1 = false
            } else {
                viewModel.name = editTextName.text.toString()
                viewModel.fatherHusband = editTextFatherHusband.text.toString()
                viewModel.mother = editTextMother.text.toString()
                viewModel.age = editTextAge.text.toString()
                viewModel.height = editTextHeight.text.toString()
                viewModel.weight = editTextWeight.text.toString()
                viewModel.numberOfMembers = editTextNumberOfMembers.text.toString()
                viewModel.numberOfChildren = editTextNumberOfChildrens.text.toString()
                viewModel.address = editTextAddress.text.toString()
//                viewModel.dmcName = editTextDMCName.text.toString()
                viewModel.block = editTextBlock.text.toString()
                viewModel.mobileNumber = editTextMobileNumbar.text.toString()
                viewModel.districtState = editTextDistrictState.text.toString()
                formFill1 = true
                if (isButton){
                    NBPA.callBackListener!!.onCallBack(1001)
                } else {
                }
            }
        }
        Log.e("TAG", "getData: "+formFill1)
    }
}