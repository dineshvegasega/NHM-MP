package com.nhm.distributor.screens.main.subscription

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ViewManageBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.ItemCouponLiveList
import com.nhm.distributor.models.Login
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.dateTime
import com.nhm.distributor.utils.gen
import com.nhm.distributor.utils.getDateToLongTime
import com.nhm.distributor.utils.getDateToLongTimeNow
import com.nhm.distributor.utils.hideKeyboard
import com.nhm.distributor.utils.orderId
import com.nhm.distributor.utils.roundOffDecimal
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ViewManage : Fragment() {
    private val viewModel: SubscriptionVM by activityViewModels()
    private var _binding: ViewManageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewManageBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)

        binding.apply {
            editTextSelectMonthYear.singleClick {
                requireActivity().showDropDownDialog(type = 18){
                    binding.editTextSelectMonthYear.setText(name)
                    viewModel.monthYear = position + 1
                }
            }

            editTextChooseNumber.singleClick {
                requireActivity().showDropDownDialog(type = 19){
                    binding.editTextChooseNumber.setText(name)
                    viewModel.number = position + 1
                }
            }

            btCalculatePrice.singleClick {
                hideKeyboard()
                if (viewModel.number > 0 && viewModel.monthYear > 0) {
                    viewModel.validity = when (viewModel.monthYear) {
                        1 -> if (viewModel.number == 1) "${viewModel.number} " + getString(R.string.month) else "${viewModel.number} " + getString(
                            R.string.months
                        )

                        2 -> if (viewModel.number == 1) "${viewModel.number} " + getString(R.string.year) else "${viewModel.number} " + getString(
                            R.string.years
                        )

                        else -> ""
                    }
                    viewModel.validityMonths = when (viewModel.monthYear) {
                        1 -> viewModel.number
                        2 -> viewModel.number * 12
                        else -> 0
                    }
                    viewModel.validityDays = when (viewModel.monthYear) {
                        1 -> viewModel.number * 30
                        2 -> viewModel.number * 365
                        else -> 0
                    }


                    readData(LOGIN_DATA) { loginUser ->
                        if (loginUser != null) {
                            val data = Gson().fromJson(loginUser, Login::class.java)
                            if (networkFailed) {
                                if(data?.vending_state?.id != null){
                                    val obj: JSONObject = JSONObject().apply {
                                        put(state_id, data?.vending_state?.id)
                                        put(no_of_month_year, viewModel.number)
                                        put(
                                            month_year,
                                            if (viewModel.monthYear == 1) "month" else "year"
                                        )
                                    }
                                    viewModel.couponLiveList(obj)
                                } else {
                                    showSnackBar(resources.getString(R.string.need_to_add_complete_subscription))
                                }
                            } else {
                                requireContext().callNetworkDialog()
                            }
                        }
                    }


                    viewModel.couponLiveListCalled.observe(viewLifecycleOwner, Observer {
                        if (it) {
                            val typeToken = object : TypeToken<ArrayList<ItemCouponLiveList>>() {}.type
                            val changeValue =
                                Gson().fromJson<ArrayList<ItemCouponLiveList>>(
                                    Gson().toJson(viewModel.itemCouponLiveList.value?.data),
                                    typeToken
                                )
                            val changeValueMain : ArrayList<ItemCouponLiveList> = ArrayList()
                            if (changeValue.size > 0) {
                                changeValue.forEach {
                                    if (getDateToLongTime(it.coupon_validity) >= getDateToLongTimeNow()){
                                        changeValueMain.add(it)
                                    }
                                }
                                if (changeValueMain.size > 0) {
                                    var lar = changeValueMain[0].coupon_discount
                                    for (i in changeValueMain.listIterator()){
                                        if(lar < i.coupon_discount){
                                            lar = i.coupon_discount
                                        }
                                    }
                                    viewModel.couponDiscount = lar
                                } else {
                                    viewModel.couponDiscount = 0.0
                                }
                            } else {
                                viewModel.couponDiscount = 0.0
                            }

                            viewModel.membershipCost =
                                viewModel.subscription.value?.subscription_cost?.times(viewModel.validityMonths) ?: 0.0
                            viewModel.couponDiscountPrice = (viewModel.membershipCost * viewModel.couponDiscount) / 100
                            viewModel.afterCouponDiscount = viewModel.membershipCost - viewModel.couponDiscountPrice
                            viewModel.gstPrice = (viewModel.afterCouponDiscount * viewModel.gst) / 100
                            viewModel.afterGst = viewModel.afterCouponDiscount + viewModel.gstPrice
                            viewModel.totalCost = viewModel.afterGst

                            update(
                                viewModel.membershipCost,
                                "" + viewModel.validityDays,
                                viewModel.gst,
                                viewModel.gstPrice,
                                viewModel.couponDiscount,
                                viewModel.couponDiscountPrice,
                                viewModel.totalCost,
                            )
                            groupVisibility.visibility = View.VISIBLE
                        } else {
                            viewModel.couponDiscount = 0.0
                        }
                    })
                } else {
                    showSnackBar(getString(R.string.please_select_month_year_and_number))
                }
            }



            btPurchaseSubscription.singleClick {
              hideKeyboard()
                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val data = Gson().fromJson(loginUser, Login::class.java)
                        if (networkFailed) {
//                            Log.e("TAG", "user_id "+data.id)
//                            Log.e("TAG", "membership_id "+data.membership_id)
//                            val orderId = data.membership_id.orderId()
//                            Log.e("TAG", "orderId "+orderId)
//                            Log.e("TAG", "validity "+viewModel.validityDays)
//                            Log.e("TAG", "net_amount "+viewModel.membershipCost)
//                            Log.e("TAG", "coupon_discount "+viewModel.couponDiscount)
//                            Log.e("TAG", "coupon_amount "+ if (viewModel.couponDiscountPrice == 0.0) "0.00" else viewModel.couponDiscountPrice)
//                            Log.e("TAG", "gst_rate "+viewModel.gst)
//                            Log.e("TAG", "gst_amount "+viewModel.gstPrice)
//                            Log.e("TAG", "totalCost "+viewModel.totalCost)


                            val obj: JSONObject = JSONObject().apply {
                                put(user_id, data.id)
                                put(membership_id, data.membership_id)
                                put(order_id, data.membership_id.orderId())
                                put(date_time, dateTime())
                                put(transaction_id, gen())
                                put(plan_type, "subscription")
                                put(payment_method, "UPI")
                                put(payment_status,"success")
                                put(payment_validity, viewModel.validityDays)
                                put(net_amount, viewModel.membershipCost)
                                put(coupon_discount, viewModel.couponDiscount)
                                put(coupon_amount, if (viewModel.couponDiscountPrice == 0.0) "0.00" else viewModel.couponDiscountPrice)
                                put(gst_rate, viewModel.gst)
                                put(gst_amount, viewModel.gstPrice)
                                put(total_amount, viewModel.totalCost)
                            }
                            if (viewModel.number > 0 && viewModel.monthYear > 0) {
                                if (viewModel.totalCost > 0) {
                                    viewModel.purchaseSubscription(obj)
                                } else {
                                    showSnackBar(getString(R.string.please_calculate_price))
                                }
                            } else {
                                showSnackBar(getString(R.string.please_select_month_year_and_number))
                            }
                        } else {
                            requireContext().callNetworkDialog()
                        }
                    }
                }
            }



            viewModel.purchaseSubscription.observe(viewLifecycleOwner, Observer {
                groupVisibility.visibility = View.GONE
                editTextSelectMonthYear.setText("")
                editTextChooseNumber.setText("")
                viewModel.number = 0
                viewModel.monthYear = 0
            })
        }
    }

    //    val mrp: BigDecimal= "0.00".toBigDecimal(),
//    val price: BigDecimal = "0.00".toBigDecimal(),
    private fun update(
        membershipCost: Double = 0.00,
        validity: String,
        gst: Double,
        gstPrice: Double,
        couponDiscount: Double,
        couponDiscountPrice: Double,
        totalCost: Double = 0.00
    ) {
        binding.apply {
            textMembershipCostValue.text = resources.getString(
                R.string.rupees, membershipCost.roundOffDecimal()
            )
            textValidityValue.text = resources.getString(R.string.days, "${validity}")
//            textGSTValue.text = "${gst} %"
//            textCouponDiscountValue.text = "${couponDiscount} %"

            textCouponDiscountTxt.text = resources.getString(R.string.discount, "${couponDiscount}%")
            textCouponDiscountValue.text = resources.getString(
                R.string.rupees, couponDiscountPrice.roundOffDecimal()
            )
            textGSTTxt.text = resources.getString(R.string.gst, "${gst}%")
            textGSTValue.text = resources.getString(
                R.string.rupees, gstPrice.roundOffDecimal()
            )

            textTotalCostValue.text =
                resources.getString(R.string.rupees, totalCost.roundOffDecimal())

            if(couponDiscount > 0.0){
                groupCouponDiscountVisibility.visibility = View.VISIBLE
            } else {
                groupCouponDiscountVisibility.visibility = View.GONE
            }
        }
    }


    private fun showDropDownMonthYearDialog() {
        val list = resources.getStringArray(R.array.month_year_array)
        MaterialAlertDialogBuilder(requireContext(), R.style.DropdownDialogTheme)
            .setTitle(resources.getString(R.string.select_month_year))
            .setItems(list) { _, which ->
                binding.editTextSelectMonthYear.setText(list[which])
                viewModel.monthYear = which + 1
            }.show()
    }


    private fun showDropDownChooseNumberDialog() {
        val list = resources.getStringArray(R.array.numbers_array)
        MaterialAlertDialogBuilder(requireContext(), R.style.DropdownDialogTheme)
            .setTitle(resources.getString(R.string.choose_number))
            .setItems(list) { _, which ->
                binding.editTextChooseNumber.setText(list[which])
                viewModel.number = which + 1
            }.show()
    }

//    override fun onPause() {
//        super.onPause()
//    }
//    override fun onDestroyView() {
//        super.onDestroyView()
//        Handler(Looper.myLooper()!!).postDelayed({
//            binding.groupVisibility.visibility = View.GONE
//        }, 100)
//
//    }
}