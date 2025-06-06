package com.nhm.distributor.screens.main.NBPA.editForms

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.nhm.distributor.R
import com.nhm.distributor.databinding.NbpaBinding
import com.nhm.distributor.models.ItemNBPAForm
import com.nhm.distributor.screens.interfaces.CallBackListener
import com.nhm.distributor.screens.main.NBPA.NBPAViewModel
import com.nhm.distributor.screens.main.NBPA.addForms.NBPA
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.isProductLoad
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.isProductLoadMember
import com.nhm.distributor.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NBPAEdit : Fragment() , CallBackListener {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: NbpaBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter : NBPAEdit_Adapter

    companion object{
        var callBackListener: CallBackListener? = null
        var tabPosition = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NbpaBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(3)
        callBackListener = this
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
        binding.apply {
            val model = arguments?.parcelable<ItemNBPAForm>("key")
            Log.e("TAG", "modelmodel "+model.toString())

            viewModel.formListDetail(
                view = requireView(),
                ""+model!!.id
            ) {
                viewModel.editDataNew = this

                adapter = NBPAEdit_Adapter(requireActivity())
                adapter.notifyDataSetChanged()
                introViewPager.isUserInputEnabled = true
                adapter.addFragment(NBPAEdit_Form1())
                adapter.addFragment(NBPAEdit_Form2())
                adapter.addFragment(NBPAEdit_Form3())
                adapter.addFragment(NBPAEdit_Form4())
                adapter.addFragment(NBPAEdit_Form5())

                Handler(Looper.getMainLooper()).postDelayed({
                    introViewPager.adapter=adapter
                    val array = listOf<String>(
                        getString(R.string.beneficiary_card),
                        getString(R.string.checkup),
                        getString(R.string.food_items),
                        getString(R.string.diet_chart),
                        getString(R.string.government_under_scheme),
                    )
                    TabLayoutMediator(tabLayout, introViewPager) { tab, position ->
                        tab.text = array[position]
                        tab.view.isEnabled = true
                    }.attach()
                }, 100)

                binding.tabLayout.touchables.forEach { it.isClickable = true }

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
                        NBPA.Companion.tabPosition = position
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)
                    }
                })
            }



        }
    }

    override fun onCallBack(pos: Int) {
        binding.apply {
            if (pos == 1000) {
                introViewPager.setCurrentItem(0, false)
            } else if (pos == 1001) {
                introViewPager.setCurrentItem(1, false)
            } else if (pos == 1002) {
                introViewPager.setCurrentItem(2, false)
            } else if (pos == 1003) {
                introViewPager.setCurrentItem(3, false)
            } else if (pos == 1004) {
                introViewPager.setCurrentItem(4, false)
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.mainActivity.get()?.callFragment(4)
        isProductLoad = false
        isProductLoadMember = false
    }
}