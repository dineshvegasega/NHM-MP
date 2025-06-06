package com.nhm.distributor.screens.main.NBPA.addForms

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
import com.nhm.distributor.screens.interfaces.CallBackListener
import com.nhm.distributor.screens.main.NBPA.NBPAViewModel
import com.nhm.distributor.screens.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NBPA : Fragment(), CallBackListener {
    private var _binding: NbpaBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: NBPA_Add_Adapter
    private lateinit var viewModel: NBPAViewModel

    companion object {
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
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)

        viewModel.isAadhaar = ""
        viewModel.start = ""
        viewModel.scheme_id = ""
        viewModel.name = ""
        viewModel.fatherHusbandType = 1 // 1 = Father, 2 = Husband
        viewModel.fatherHusband = ""
        viewModel.mother = ""
        viewModel.gender = ""
        viewModel.age = ""
        viewModel.height = ""
        viewModel.weight = ""
        viewModel.numberOfMembers = ""
        viewModel.numberOfChildren = ""
        viewModel.address = ""
        viewModel.dmcName = ""
        viewModel.block = ""
        viewModel.mobileNumber = ""
        viewModel.districtState = ""
        viewModel.cardTypeAPLBPL = 1 // 1 = APL, 2 = BPL, 3 = Other

        viewModel.typeOfPatient = 1 // 1 = Pulmonary, 2 = Extra Pulmonary, 3 = Other
        viewModel.patientCheckupDate = ""
        viewModel.hemoglobinLevelAge = ""
        viewModel.hemoglobinCheckupDate = ""
        viewModel.muktiID = ""
        viewModel.nakshayID = ""
        viewModel.aadhaarNumber = ""
        viewModel.business = ""
        viewModel.bankAccount = ""
        viewModel.bankIFSC = ""
        viewModel.treatmentSupporterName = ""
        viewModel.treatmentSupporterPost = ""
        viewModel.treatmentSupporterMobileNumber = ""
        viewModel.treatmentSupporterEndDate = ""
        viewModel.treatmentSupporterResult = ""

        viewModel.foodMonth = ""
        viewModel.foodDate = ""
        viewModel.foodHeight = ""
        viewModel.foodSignatureImage = ""
        viewModel.foodItemImage = ""
        viewModel.foodIdentityImage = ""


        viewModel.dietChartDate = ""
        viewModel.dietChartEvaluation = ""
        viewModel.dietChartSuggestion = ""
        viewModel.dietChartServiceProvider = ""
        viewModel.homeVisitDate = ""
        viewModel.homeVisitWeight = ""
        viewModel.homeVisitSignature = ""
        viewModel.homeVisitRemark = ""

        viewModel.assistanceDBTDate = ""
        viewModel.assistanceDBTTotalAmount = ""
        viewModel.assistanceDBTDetails = ""
        viewModel.assistanceDBTRemark = ""
        viewModel.assistanceExtraGroceryPDS = ""
        viewModel.assistanceExtraGroceryPDSDetails = ""
        viewModel.assistanceExtraGroceryPDSRemark = ""
        viewModel.assistanceMultiVitaminDate = ""
        viewModel.assistanceMultiVitaminTotalNumber = ""
        viewModel.assistanceMultiVitaminDetails = ""
        viewModel.assistanceMultiVitaminRemark = ""
        viewModel.assistanceOtherHelp = ""
        viewModel.assistanceHelpDetails = ""
        viewModel.assistanceHelpRemark = ""
        viewModel.assistanceProjectCoordinatorSignature = ""
        viewModel.assistanceProjectManagerSignature = ""


        callBackListener = this
        binding.apply {
            adapter = NBPA_Add_Adapter(requireActivity())
            adapter.notifyDataSetChanged()



            var start = "" + arguments?.getString("isExist")
            viewModel.start = start
            if (start == "yes") {
                introViewPager.isUserInputEnabled = true
                var _id = "" + arguments?.getString("_id")
                viewModel.scheme_id = _id
                viewModel.formListDetail(
                    view = requireView(),
                    _id
                ) {
                    viewModel.editDataNew = this
                    Handler(Looper.getMainLooper()).postDelayed({
                        introViewPager.adapter = adapter
                        adapter.addFragment(NBPA_Form1())
                        adapter.addFragment(NBPA_Form2())
                        adapter.addFragment(NBPA_Form3())
                        adapter.addFragment(NBPA_Form4())
                        adapter.addFragment(NBPA_Form5())

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
                        introViewPager.setCurrentItem(2, false)
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
                            tabPosition = position
                        }

                        override fun onPageScrollStateChanged(state: Int) {
                            super.onPageScrollStateChanged(state)
                        }
                    })
                }
            } else {
                introViewPager.isUserInputEnabled = false
                viewModel.isAadhaar = start
                var isAadhaar = "" + arguments?.getString("isAadhaar")
                viewModel.isAadhaar = isAadhaar
                if (isAadhaar == "yes"){
                    var aadhaarNumber = "" + arguments?.getString("aadhaarNumber")
                    Log.e("TAG", "aadhaarNumber "+aadhaarNumber)
                    viewModel.aadhaarNumber = aadhaarNumber
                }
                if (isAadhaar == "no"){
                    var aadhaarNumber = "" + arguments?.getString("aadhaarNumber")
                    Log.e("TAG", "aadhaarNumber "+aadhaarNumber)
                    viewModel.nakshayID = aadhaarNumber
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    introViewPager.adapter = adapter
                    introViewPager.offscreenPageLimit = 5
                    adapter.addFragment(NBPA_Form1())
                    adapter.addFragment(NBPA_Form2())
                    adapter.addFragment(NBPA_Form3())
                    adapter.addFragment(NBPA_Form4())
                    adapter.addFragment(NBPA_Form5())

                    val array = listOf<String>(
                        getString(R.string.beneficiary_card),
                        getString(R.string.checkup),
                        getString(R.string.food_items),
                        getString(R.string.diet_chart),
                        getString(R.string.government_under_scheme),
                    )
                    TabLayoutMediator(tabLayout, introViewPager) { tab, position ->
                        tab.text = array[position]
                        tab.view.isEnabled = false
                    }.attach()
                    introViewPager.setCurrentItem(0, false)
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
                        if (position == 0){
//                            NBPA_Form1.callBackListener!!.onCallBack(1101)
                        } else if (position == 1){
//                            NBPA_Form1.callBackListener!!.onCallBack(1101)
//                            NBPA_Form2.callBackListener!!.onCallBack(1102)
                        } else if (position == 2){
//                            NBPA_Form2.callBackListener!!.onCallBack(1102)
//                            NBPA_Form3.callBackListener!!.onCallBack(1103)
                        } else if (position == 3){
//                            NBPA_Form3.callBackListener!!.onCallBack(1103)
//                            NBPA_Form4.callBackListener!!.onCallBack(1104)
                        } else if (position == 4){
//                            NBPA_Form4.callBackListener!!.onCallBack(1104)
//                            NBPA_Form5.callBackListener!!.onCallBack(1105)
                        }

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


//    override fun onStop() {
//        super.onStop()
//        isProductLoad = true
//        isProductLoadMember = true
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        isProductLoad = false
//        isProductLoadMember = false
//    }


}