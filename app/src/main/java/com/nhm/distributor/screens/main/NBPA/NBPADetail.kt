package com.nhm.distributor.screens.main.NBPA

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nhm.distributor.R
import com.nhm.distributor.databinding.NbpaDetailBinding
import com.nhm.distributor.models.ItemNBPAForm
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.isProductLoad
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.isProductLoadMember
import com.nhm.distributor.utils.loadImage
import com.nhm.distributor.utils.parcelable
import com.nhm.distributor.utils.singleClick
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NBPADetail : Fragment() {
    private val viewModel: NBPAViewModel by viewModels()
    private var _binding: NbpaDetailBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NbpaDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        MainActivity.mainActivity.get()?.callFragment(3)
//        change = false
        binding.apply {
            val model = arguments?.parcelable<ItemNBPAForm>("key")
            Log.e("TAG", "modelmodel "+model.toString())

            model?.let {
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
                    } else {
                        radioButtonBPL.isChecked = true
                    }
                    for (i in 0..<radioGroupCardAPLBPLGroup.getChildCount()) {
                        radioGroupCardAPLBPLGroup.getChildAt(i).setEnabled(false)
                    }
                }


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


                lateinit var viewer: StfalconImageViewer<String>

                nbpaDetail3.apply {
                    editTextMonth.setText(""+model.foodMonth)
                    editTextDate.setText(""+model.foodDate)
                    editTextHeight2.setText(""+model.foodHeight)
//                    model.foodSignatureImage.url.glideImagePortraitForImage(ivSignature.context, ivSignature)
//                    model.foodItemImage.url.glideImagePortraitForImage(ivImagePassportsizeImage.context, ivImagePassportsizeImage)
//                    model.foodIdentityImage.url.glideImagePortraitForImage(ivImageIdentityImage.context, ivImageIdentityImage)

//                    Glide.with(requireActivity())
//                        .load(model.foodSignatureImage.url)
//                        .apply(myOptionsGlidePortrait)
//                        .into(ivSignature)

                    ivSignature.loadImage(type = 1, url = { model.foodSignatureImage.url })
                    ivImagePassportsizeImage.loadImage(type = 1, url = { model.foodItemImage.url })
                    ivImageIdentityImage.loadImage(type = 1, url = { model.foodIdentityImage.url })

                    ivSignature.singleClick {
                        Log.e("TAG", "ivSignaturesingleClick")
                       viewer = StfalconImageViewer.Builder<String>(requireActivity(), arrayListOf(model.foodSignatureImage.url)) { view, image ->
                            Picasso.get().load(image).into(view)
                        }.withImageChangeListener {
                           viewer.updateTransitionImage(ivSignature)
                        }
                           .withBackgroundColor(
                               ContextCompat.getColor(
                                   requireActivity(),
                                   R.color._D9000000
                               )
                           )
                            .show()

//                        model.foodSignatureImage.url.let {
//                            arrayListOf(it).imageZoom(ivSignature, 2)
//                        }
//                        StfalconImageViewer.Builder<String>(requireActivity(),arrayListOf(model.foodSignatureImage.url)) { view, image ->
////                            Glide.with(MainActivity.mainActivity.get()!!)
////                                .load(image)
////                                .apply(myOptionsGlide)
////                                .into(view)
//                            view.loadImage(type = 1, url = { model.foodSignatureImage.url })
//                        }.withTransitionFrom(ivSignature)
//                            .withBackgroundColor(
//                                ContextCompat.getColor(
//                                    requireActivity(),
//                                    R.color._D9000000
//                                )
//                            )
//                            .show()
                    }
                    ivImagePassportsizeImage.singleClick {
                        Log.e("TAG", "ivImagePassportsizeImagesingleClick")
//                        model.foodItemImage.url.let {
//                            arrayListOf(it).imageZoom(ivImagePassportsizeImage, 2)
//                        }
                        viewer = StfalconImageViewer.Builder<String>(requireActivity(), arrayListOf(model.foodItemImage.url)) { view, image ->
                            Picasso.get().load(image).into(view)
                        }.withImageChangeListener {
                            viewer.updateTransitionImage(ivImagePassportsizeImage)
                        }
                            .withBackgroundColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color._D9000000
                                )
                            )
                            .show()
                    }
                    ivImageIdentityImage.singleClick {
//                        model.foodIdentityImage.url.let {
//                            arrayListOf(it).imageZoom(ivImageIdentityImage, 2)
//                        }
                        viewer = StfalconImageViewer.Builder<String>(requireActivity(), arrayListOf(model.foodIdentityImage.url)) { view, image ->
                            Picasso.get().load(image).into(view)
                        }.withImageChangeListener {
                            viewer.updateTransitionImage(ivImageIdentityImage)
                        }
                            .withBackgroundColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color._D9000000
                                )
                            )
                            .show()
                    }

                }



                nbpaDetail1.apply {
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
                }
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
                nbpaDetail3.apply {
                    editTextMonth.isEnabled = false
                    editTextDate.isEnabled = false
                    editTextHeight2.isEnabled = false
                }
            }

        }

    }


//    override fun onStop() {
//        super.onStop()
//        MainActivity.mainActivity.get()?.callFragment(4)
//        isProductLoad = true
//        isProductLoadMember = true
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.mainActivity.get()?.callFragment(4)
        isProductLoad = false
        isProductLoadMember = false
    }


    override fun onResume() {
        super.onResume()
        Log.e("TAG", "onResume")
    }


}