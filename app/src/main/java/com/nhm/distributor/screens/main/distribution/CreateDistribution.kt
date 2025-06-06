package com.nhm.distributor.screens.main.distribution

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.nhm.distributor.databinding.CreateDistributionBinding
import com.nhm.distributor.screens.interfaces.CallBackListener
import com.nhm.distributor.screens.main.distribution.forms.Form1
import com.nhm.distributor.screens.main.distribution.forms.Form2
import com.nhm.distributor.screens.main.distribution.forms.Form3
import com.nhm.distributor.screens.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import com.nhm.distributor.R

@AndroidEntryPoint
class CreateDistribution : Fragment() {
    private val viewModel: DistributionViewModel by viewModels()
    private var _binding: CreateDistributionBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter : CreateDistributionAdapter

    companion object{
        var callBackListener: CallBackListener? = null
        var tabPosition = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreateDistributionBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)

        binding.apply {
            adapter = CreateDistributionAdapter(requireActivity())
            adapter.notifyDataSetChanged()
            introViewPager.isUserInputEnabled = false
            adapter.addFragment(Form1())
            adapter.addFragment(Form2())
            adapter.addFragment(Form3())
//            adapter.addFragment(Form4())
//            adapter.addFragment(Form5())
//            adapter.addFragment(Form6())
//            adapter.addFragment(Form7())
            Handler(Looper.getMainLooper()).postDelayed({
                introViewPager.adapter=adapter
                val array = listOf<String>(
                    getString(R.string.beneficiary_card),
                    getString(R.string.checkup),
                    getString(R.string.food_items),
//                    getString(R.string.form4),
//                    getString(R.string.form5),
//                    getString(R.string.form6),
//                    getString(R.string.form7)
                )
                TabLayoutMediator(tabLayout, introViewPager) { tab, position ->
                    tab.text = array[position]
                    tab.view.isEnabled = true
                }.attach()
            }, 100)


            binding.tabLayout.touchables.forEach { it.isClickable = false }

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
                    tabPosition = position
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })


        }

    }



}