package com.nhm.distributor.screens.onboarding.onboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.R
import com.nhm.distributor.databinding.OnboardItemBinding
import com.squareup.picasso.Picasso
import com.nhm.distributor.genericAdapter.GenericAdapter
import com.nhm.distributor.networking.CompleteRegister
import com.nhm.distributor.networking.Repository
import com.nhm.distributor.networking.Screen
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardVM @Inject constructor(private val repository: Repository): ViewModel() {
    var itemMain : ArrayList<Onboard.Item> ?= ArrayList()

    var whichScreen = -1


    init {
        itemMain?.add(
            Onboard.Item(
                MainActivity.context.get()!!.getString(R.string.loginWithPassword),
                R.drawable.onboard2
            )
        )

        itemMain?.add(
            Onboard.Item(
                MainActivity.context.get()!!.getString(R.string.quick_registration),
                R.drawable.onboard1
            )
        )

//        itemMain?.add(
//            Onboard.Item(
//                MainActivity.context.get()!!.getString(R.string.loginWithMobileOTP),
//                R.drawable.onboard3
//            )
//        )
//        itemMain?.add(
//            Onboard.Item(
//                MainActivity.context.get()!!.getString(R.string.completeProfileRegistration),
//                R.drawable.onboard4
//            )
//        )
    }

    var clickEvent = MutableLiveData<Boolean>(false)

    fun callNextPage(it: View) {
            when(whichScreen) {
                0 -> it.findNavController().navigate(R.id.action_onboard_to_loginPassword)
                1 -> it.findNavController().navigate(R.id.action_onboard_to_quickRegistration)
//                2 -> it.findNavController().navigate(R.id.action_onboard_to_loginOtp)
                2 -> it.findNavController().navigate(R.id.action_onboard_to_register, Bundle().apply {
                    putString(Screen, CompleteRegister)
                })
            }
    }

    val photosAdapter = object : GenericAdapter<OnboardItemBinding, Onboard.Item>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = OnboardItemBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindHolder(binding: OnboardItemBinding, dataClass: Onboard.Item, position: Int) {
            binding.txtTitle.text = dataClass.name
            Picasso.get().load(
                dataClass.image
            ).into(binding!!.ivIcon)

            binding.mainContainer.setBackgroundResource(if (dataClass.isSelected == true) R.drawable.orange_dark else R.drawable.orange_light)

            binding.ivIcon.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.teal_200), android.graphics.PorterDuff.Mode.SRC_ATOP);

//            if(position == 0){
//                binding.txtTitleTop.visibility = View.VISIBLE
//            }else{
                binding.txtTitleTop.visibility = View.INVISIBLE
//            }

            if (dataClass.isSelected == true){
                binding.ivIcon.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
                binding.txtTitle.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            }else if (dataClass.isSelected == false){
                binding.ivIcon.setColorFilter(ContextCompat.getColor(binding.root.context, R.color.black), android.graphics.PorterDuff.Mode.SRC_ATOP);
                binding.txtTitle.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
            }

            binding.btImage.setImageDrawable(ContextCompat.getDrawable(binding.root.context, if (dataClass.isSelected == true) R.drawable.radio_sec_filled else R.drawable.radio_sec_empty));

            binding.root.singleClick {
                whichScreen = position
                val list = currentList
                list.forEach {
                    it.isSelected = dataClass == it
                    clickEvent.value = true
                }
                notifyDataSetChanged()
            }
        }
    }


}