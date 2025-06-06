package com.nhm.distributor.screens.main.members

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
import com.nhm.distributor.databinding.MemberDetailBinding
import com.nhm.distributor.models.ItemMember
import com.nhm.distributor.networking.AADHAAR_URL
import com.nhm.distributor.networking.IMAGE_URL
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.utils.loadImage
import com.nhm.distributor.utils.parcelable
import com.nhm.distributor.utils.singleClick
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MemberDetail : Fragment() {
    private val viewModel: MemberVM by viewModels()
    private var _binding: MemberDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MemberDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(3)
//        change = false
        binding.apply {
            val model = arguments?.parcelable<ItemMember>("key")
            Log.e("TAG", "modelmodel " + model.toString())

            model?.let {
                editTextFN.setText("" + model.name)
                editTextLN.setText("" + model.last_name)

                model.profile_image_name?.let {
                    ivProfileImage.loadImage(type = 1, url = { IMAGE_URL+model.profile_image_name })
                }
                editMobile.setText(model.mobile_no)
//                editTextGender.setText(model.gender)
                editTextAadhaarNumber.setText(model.aadhar_card)
                editTextAddress.setText(model.address)
                model.aadhar_card_doc?.let {
                    ivImageAadhaarImage.loadImage(type = 1, url = { AADHAAR_URL+model.aadhar_card_doc })
                }
                lateinit var viewer: StfalconImageViewer<String>
                ivProfileImage.singleClick {
//                    (IMAGE_URL+model.profile_image_name).let {
//                        arrayListOf(it).imageZoom(ivProfileImage, 2)
//                    }
                    viewer = StfalconImageViewer.Builder<String>(requireActivity(), arrayListOf((IMAGE_URL+model.profile_image_name))) { view, image ->
                        Picasso.get().load(image).into(view)
                    }.withImageChangeListener {
                        viewer.updateTransitionImage(ivProfileImage)
                    }
                        .withBackgroundColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color._D9000000
                            )
                        )
                        .show()
                }
                ivImageAadhaarImage.singleClick {
//                    (AADHAAR_URL+model.aadhar_card_doc).let {
//                        arrayListOf(it).imageZoom(ivImageAadhaarImage, 2)
//                    }
                    viewer = StfalconImageViewer.Builder<String>(requireActivity(), arrayListOf((AADHAAR_URL+model.aadhar_card_doc))) { view, image ->
                        Picasso.get().load(image).into(view)
                    }.withImageChangeListener {
                        viewer.updateTransitionImage(ivImageAadhaarImage)
                    }
                        .withBackgroundColor(
                            ContextCompat.getColor(
                                requireActivity(),
                                R.color._D9000000
                            )
                        )
                        .show()
                }


                editTextFN.isEnabled = false
                editTextLN.isEnabled = false
                editMobile.isEnabled = false
                editTextGender.isEnabled = false
                editTextAadhaarNumber.isEnabled = false
                editTextAddress.isEnabled = false
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.mainActivity.get()?.callFragment(4)
//        change = false
    }
}