package com.nhm.distributor.screens.main.members

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nhm.distributor.R
import com.nhm.distributor.databinding.DialogFilterBinding
import com.nhm.distributor.databinding.MemberListBinding
import com.nhm.distributor.networking.USER_TYPE_ADMIN
import com.nhm.distributor.networking.search_input
import com.nhm.distributor.networking.user_id
import com.nhm.distributor.networking.user_role
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.isFilterLoadMember
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.isProductLoadMember
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.userIdForGlobal
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.text.SimpleDateFormat

@AndroidEntryPoint
class MemberList : Fragment() {
    private val viewModel: MemberVM by viewModels()
    private var _binding: MemberListBinding? = null
    private val binding get() = _binding!!


    var page: Int = 1


    lateinit var adapter2: MemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MemberListBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        Log.e("TAG", "onViewCreatedQQ")
//        isBackStack = true

        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text =
                getString(R.string.all_distributors)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_schemes)

            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            inclideHeaderSearch.btFilter.visibility = View.VISIBLE
            inclideHeaderSearch.btFilter.singleClick {

                val dialogBinding = DialogFilterBinding.inflate(
                    root.context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE
                    ) as LayoutInflater
                )
                val dialog = BottomSheetDialog(root.context)
                dialog.setContentView(dialogBinding.root)
                dialog.setOnShowListener { dia ->
                    val bottomSheetDialog = dia as BottomSheetDialog
                    val bottomSheetInternal: FrameLayout =
                        bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
                    bottomSheetInternal.setBackgroundResource(R.drawable.bg_top_round_corner)
                }
                dialog.show()

                dialogBinding.apply {
                    radioButtonName.singleClick {
                        radioButtonName.isChecked = true
                        radioButtonMobile.isChecked = false
                        radioButtonAadhaar.isChecked = false
                        radioButtonDate.isChecked = false
                    }

                    radioButtonMobile.singleClick {
                        radioButtonName.isChecked = false
                        radioButtonMobile.isChecked = true
                        radioButtonAadhaar.isChecked = false
                        radioButtonDate.isChecked = false
                    }

                    radioButtonAadhaar.singleClick {
                        radioButtonName.isChecked = false
                        radioButtonMobile.isChecked = false
                        radioButtonAadhaar.isChecked = true
                        radioButtonDate.isChecked = false
                    }

                    radioButtonDate.visibility = View.GONE
                    linearDate.visibility = View.GONE
                    radioButtonDistributorNumber.visibility = View.GONE
                    editTextDistributorNumber.visibility = View.GONE

                    editTextStartDate.singleClick {
                        requireActivity().showDropDownDialog(type = 20) {
                            editTextStartDate.setText(title)
                        }
                    }

                    editTextEndDate.singleClick {
                        requireActivity().showDropDownDialog(type = 20) {
                            editTextEndDate.setText(title)
                        }
                    }
                    radioButtonName.isChecked = viewModel.filterNameBoolean
                    radioButtonMobile.isChecked = viewModel.filterMobileBoolean
                    radioButtonAadhaar.isChecked = viewModel.filterAadhaarBoolean
                    radioButtonDate.isChecked =
                        if (viewModel.filterStartDateBoolean || viewModel.filterEndDateBoolean) {
                            true
                        } else {
                            false
                        }

                    editTextName.setText(viewModel.filterName)
                    editTextMobile.setText(viewModel.filterMobile)
                    editTextAadhaar.setText(viewModel.filterAadhaar)
                    editTextStartDate.setText(viewModel.filterStartDate)
                    editTextEndDate.setText(viewModel.filterEndDate)

                    btClose.singleClick {
                        editTextName.setText("")
                        editTextMobile.setText("")
                        editTextAadhaar.setText("")
                        editTextStartDate.setText("")
                        editTextEndDate.setText("")
                        viewModel.filterName = ""
                        viewModel.filterMobile = ""
                        viewModel.filterAadhaar = ""
                        viewModel.filterStartDate = ""
                        viewModel.filterEndDate = ""
                        viewModel.filterNameBoolean = false
                        viewModel.filterMobileBoolean = false
                        viewModel.filterAadhaarBoolean = false
                        viewModel.filterStartDateBoolean = false
                        viewModel.filterEndDateBoolean = false
//                        dialog.dismiss()

                        radioButtonName.isChecked = false
                        radioButtonMobile.isChecked = false
                        radioButtonAadhaar.isChecked = false
                        radioButtonDate.isChecked = false
                    }


                    btApply.singleClick {
                        viewModel.filterName = editTextName.text.toString()
                        viewModel.filterMobile = editTextMobile.text.toString()
                        viewModel.filterAadhaar = editTextAadhaar.text.toString()
                        viewModel.filterStartDate = editTextStartDate.text.toString()
                        viewModel.filterEndDate = editTextEndDate.text.toString()

                        viewModel.filterNameBoolean = radioButtonName.isChecked
                        viewModel.filterMobileBoolean = radioButtonMobile.isChecked
                        viewModel.filterAadhaarBoolean = radioButtonAadhaar.isChecked
                        viewModel.filterStartDateBoolean = radioButtonDate.isChecked
                        viewModel.filterEndDateBoolean = radioButtonDate.isChecked

                        if (radioButtonDate.isChecked){
                            try{
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                                val startTimeInMillis = dateFormat.parse(viewModel.filterStartDate).time.toString()
                                val endTimeInMillis = dateFormat.parse(viewModel.filterEndDate).time.toString()
                                Log.e("TAG", "startTimeInMillis "+startTimeInMillis)
                                Log.e("TAG", "endTimeInMillis "+endTimeInMillis)
                                if(startTimeInMillis > endTimeInMillis){
                                    Log.e("TAG", "AAAAAA "+startTimeInMillis)
                                    dialog.dismiss()
                                    showSnackBar(dialogBinding.root.resources.getString(R.string.EndDateshouldbegreaterthanStartDate))
                                } else {
                                    Log.e("TAG", "BBBBBB "+startTimeInMillis)
                                    page = 1
                                    isProductLoadMember = true
                                    viewModel.itemsProduct.clear()

                                    if (radioButtonName.isChecked && viewModel.filterName.isEmpty()) {
                                        showSnackBar(resources.getString(R.string.enterName))
                                    } else if (radioButtonMobile.isChecked && viewModel.filterMobile.isEmpty()) {
                                        showSnackBar(resources.getString(R.string.enterMobileNumberForm))
                                    } else if (radioButtonAadhaar.isChecked && viewModel.filterAadhaar.isEmpty()) {
                                        showSnackBar(resources.getString(R.string.enter_valid_aadhaar_number))
                                    } else {
                                        var searchInput = if (radioButtonName.isChecked){
                                            viewModel.filterName
                                        } else if (radioButtonMobile.isChecked){
                                            viewModel.filterMobile
                                        } else if (radioButtonAadhaar.isChecked){
                                            viewModel.filterAadhaar
                                        } else {
                                            ""
                                        }
                                        filters(searchInput)
                                    }
                                    dialog.dismiss()
                                }
                            }catch (e : Exception){
                                Log.e("TAG", "CCCCCC "+e.message)
                                dialog.dismiss()
                                showSnackBar(dialogBinding.root.resources.getString(R.string.SelectValidStartDateandEndDate))
                            }
                        } else {
                            Log.e("TAG", "DDDDDD ")
                            page = 1
                            isProductLoadMember = true
                            viewModel.itemsProduct.clear()
                            if (radioButtonName.isChecked && viewModel.filterName.isEmpty()) {
                                showSnackBar(resources.getString(R.string.enterName))
                            } else if (radioButtonMobile.isChecked && viewModel.filterMobile.isEmpty()) {
                                showSnackBar(resources.getString(R.string.enterMobileNumberForm))
                            } else if (radioButtonAadhaar.isChecked && viewModel.filterAadhaar.isEmpty()) {
                                showSnackBar(resources.getString(R.string.enter_valid_aadhaar_number))
                            } else {
                                var searchInput = if (radioButtonName.isChecked){
                                    viewModel.filterName
                                } else if (radioButtonMobile.isChecked){
                                    viewModel.filterMobile
                                } else if (radioButtonAadhaar.isChecked){
                                    viewModel.filterAadhaar
                                } else {
                                    ""
                                }
                                filters(searchInput)
                            }
                            dialog.dismiss()
                        }
                    }
                }
            }



            adapter2 = MemberAdapter()
            binding.rvList2.adapter = adapter2



            if (isFilterLoadMember) {
                page = 1
                isProductLoadMember = true
                viewModel.itemsProduct.clear()
            }

            Log.e("TAG", "isFilterLoad " + isFilterLoadMember)
            Log.e("TAG", "isProductLoadAA " + isProductLoadMember)

            filters(
                ""
            )


            idNestedSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                // on scroll change we are checking when users scroll as bottom.
//                val aa = v.getChildAt(0).measuredHeight - v.measuredHeight
//
//                Log.e("TAG", "scrollYAA $scrollY  ::  $aa")
                if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    page++
                    idPBLoading.visibility = View.VISIBLE
//
//                    getDataFromAPI(page)
                    isProductLoadMember = true

                    var searchInput = if (viewModel.filterNameBoolean){
                        viewModel.filterName
                    } else if (viewModel.filterMobileBoolean){
                        viewModel.filterMobile
                    } else if (viewModel.filterAadhaarBoolean){
                        viewModel.filterAadhaar
                    } else {
                        ""
                    }

                    filters(searchInput)

                }
            })


            viewModel.itemProducts?.observe(viewLifecycleOwner) {
                Log.e("TAG", "itemsss " + it.data.size)
                if (it.data.size != 0) {
//                    idPBLoading.visibility = View.VISIBLE
                } else {
                    idPBLoading.visibility = View.GONE
                }


                Log.e("TAG", "isProductLoad " + isProductLoadMember)
                if (isProductLoadMember) {
                    viewModel.itemsProduct.addAll(it.data)
                    isProductLoadMember = false
                }

                if (viewModel.itemsProduct.size == 0) {
                    binding.idDataNotFound.root.visibility = View.VISIBLE
                } else {
                    binding.idDataNotFound.root.visibility = View.GONE
                }

                adapter2.submitData(viewModel.itemsProduct)
                adapter2.notifyDataSetChanged()


            }


        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun filters(
        filterSearch: String
    ) {
        var obj: JSONObject = JSONObject()
//        if (userType == USER_TYPE) {
            obj = JSONObject().apply {
                put(com.nhm.distributor.networking.page,""+ page)
                put(user_role, USER_TYPE_ADMIN)
                put(user_id, userIdForGlobal)
                put(search_input, filterSearch)
//                put(filterByMobile, filterMobile)
//                put(filterByAadhaar, filterAadhaar)
//                put(filterByStartDate, filterStartDate)
//                put(filterByEndDate, filterEndDate)
            }
//        }
//
//        if (userType == USER_TYPE_ADMIN) {
//            obj = JSONObject().apply {
//                put(com.nhm.distributor.networking.page, page)
//                put(user_type, USER_TYPE_ADMIN)
//                put(filterByName, filterName)
//                put(filterByMobile, filterMobile)
//                put(filterByAadhaar, filterAadhaar)
//                put(filterByStartDate, filterStartDate)
//                put(filterByEndDate, filterEndDate)
//            }
//        }
        viewModel.getProducts(obj, page)
    }

    override fun onStart() {
        super.onStart()
//        if (isFilterLoad){
//            page = 1
//            isProductLoad = true
//            viewModel.itemsProduct.clear()
//        }
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