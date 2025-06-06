package com.nhm.distributor.screens.main.subscription

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.nhm.distributor.R
import com.nhm.distributor.databinding.SubscriptionBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.changeDateFormat
import com.nhm.distributor.utils.imageZoom
import com.nhm.distributor.utils.loadImage
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class Subscription : Fragment(){
    private val viewModel: SubscriptionVM by activityViewModels()
    private var _binding: SubscriptionBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter : SubscriptionPagerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)

        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.subscription)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE
            inclideHeaderSearch.textHeaderEditTxt.visibility = View.GONE

            readData(LOGIN_DATA) { loginUser ->
                if (loginUser != null) {
                    val data = Gson().fromJson(loginUser, Login::class.java)
                    data.profile_image_name?.let {
                        inclidePersonalProfile.ivImageProfile.loadImage(type = 1, url = { data.profile_image_name.url })
                        inclidePersonalProfile.ivImageProfile.singleClick {
                            data.profile_image_name?.let {
                                arrayListOf(it.url).imageZoom(inclidePersonalProfile.ivImageProfile, 2)
                            }
                        }
                    }
                    inclidePersonalProfile.textNameOfMember.text = "${data.vendor_first_name} ${data.vendor_last_name}"
                    inclidePersonalProfile.textMobileNumber.text = "+91-${data.mobile_no}"
                    inclidePersonalProfile.textMembershipIdValue.text = "${data.member_id}"
                    data.validity_to?.let {
                        inclidePersonalProfile.textValidUptoValue.text = "${data.validity_to.changeDateFormat("yyyy-MM-dd", "dd-MMM-yyyy")}"
                    }


                    if(networkFailed) {
                        val _id = Gson().fromJson(loginUser, Login::class.java)?.vending_state?.id
                        val obj: JSONObject = JSONObject().apply {
                            put(state_id, _id)
                        }
                        viewModel.subscription(obj)
                    } else {
                        requireContext().callNetworkDialog()
                    }
                }
            }


            adapter= SubscriptionPagerAdapter(requireActivity())
            adapter.notifyDataSetChanged()
            introViewPager.isUserInputEnabled = false
            adapter.addFragment(ViewManage())
            adapter.addFragment(SubscriptionHistory())

            Handler(Looper.getMainLooper()).postDelayed({
                introViewPager.adapter=adapter
                val array = listOf<String>(getString(R.string.view_manage), getString(R.string.history))
                TabLayoutMediator(tabLayout, introViewPager) { tab, position ->
                    tab.text = array[position]
                    //setTabStyle(tabLayout, array[position])
                }.attach()
            }, 100)

            introViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })

            viewModel.purchaseSubscription.observe(viewLifecycleOwner, Observer {
                if (it){
                    introViewPager.setCurrentItem(1)
                }
            })
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.onCleared()
    }

}