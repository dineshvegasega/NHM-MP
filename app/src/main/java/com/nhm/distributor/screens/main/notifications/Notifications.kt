package com.nhm.distributor.screens.main.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhm.distributor.R
import com.nhm.distributor.databinding.NotificationsBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.models.ItemNotification
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.PaginationScrollListener
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class Notifications : Fragment() {
    private val viewModel: NotificationsVM by viewModels()
    private var _binding: NotificationsBinding? = null
    private val binding get() = _binding!!

    var deleteAlert : AlertDialog?= null


    private var LOADER_TIME: Long = 500
    private var pageStart: Int = 1
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var totalPages: Int = 1
    private var currentPage: Int = pageStart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.notifications)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            inclideHeaderSearch.btClose.text = getString(R.string.clear_all)
            inclideHeaderSearch.btClose.visibility = View.VISIBLE

            idDataNotFound.textDesc.text = getString(R.string.currently_no_notifications)

            inclideHeaderSearch.btClose.singleClick {
                if(deleteAlert?.isShowing == true) {
                    return@singleClick
                }

                deleteAlert = MaterialAlertDialogBuilder(requireContext(), R.style.LogoutDialogTheme)
                    .setTitle(resources.getString(R.string.app_name))
                    .setMessage(resources.getString(R.string.are_your_sure_want_to_delete_all_notifications))
                    .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                        dialog.dismiss()
                       readData(LOGIN_DATA) { loginUser ->
                                if (loginUser != null) {
                                    val obj: JSONObject = JSONObject().apply {
                                        put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
                                    }
                                    if(networkFailed) {
                                        viewModel.deleteNotification(obj)
                                    } else {
                                        requireContext().callNetworkDialog()
                                    }
                                }
                       }

                    }
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }

            loadFirstPage()
            recyclerView.setHasFixedSize(true)
            binding.recyclerView.adapter = viewModel.adapter
            binding.recyclerView.itemAnimator = DefaultItemAnimator()

            observerDataRequest()

            recyclerViewScroll()

        }
    }



    private fun recyclerViewScroll() {
        binding.apply {
            recyclerView.addOnScrollListener(object : PaginationScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
                override fun loadMoreItems() {
                    isLoading = true
                    currentPage += 1
                    if(totalPages >= currentPage){
                        Handler(Looper.myLooper()!!).postDelayed({
                            loadNextPage()
                        }, LOADER_TIME)
                    }
                }
                override fun getTotalPageCount(): Int {
                    return totalPages
                }
                override fun isLastPage(): Boolean {
                    return isLastPage
                }
                override fun isLoading(): Boolean {
                    return isLoading
                }
            })
        }
    }


    private fun loadFirstPage() {
        pageStart  = 1
        isLoading = false
        isLastPage = false
        totalPages  = 1
        currentPage  = pageStart
//        results.clear()
        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                val obj: JSONObject = JSONObject().apply {
                    put(page, pageStart)
                    put(is_read, false)
                    put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
                }
                if(networkFailed) {
                    viewModel.notifications(obj)
                    binding.idNetworkNotFound.root.visibility = View.GONE
                } else {
//                    requireContext().callNetworkDialog()
                    binding.idNetworkNotFound.root.visibility = View.VISIBLE
                }
            }
        }
    }

    fun loadNextPage() {
        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                val obj: JSONObject = JSONObject().apply {
                    put(page, currentPage)
                    put(is_read, false)
                    put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
                }
                if(networkFailed) {
                    viewModel.notificationsSecond(obj)
                } else {
                    requireContext().callNetworkDialog()
                }
            }
        }
    }


    var results: ArrayList<ItemNotification> = ArrayList()

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    private fun observerDataRequest(){

        viewModel.updateNotifications.value = -1
        viewModel.updateNotifications.observe(requireActivity()) {
            if (it != -1){
                results.removeAt(it)
                viewModel.adapter.addAllSearch(results)
                viewModel.updateNotifications.value = -1
            }

        }


        viewModel.deleteNotifications.observe(requireActivity()) {
            if(it){
                results.clear()
                loadFirstPage()
            }
        }



        viewModel.itemNotifications.observe(requireActivity()) {
            val typeToken = object : TypeToken<List<ItemNotification>>() {}.type
            val changeValue = Gson().fromJson<List<ItemNotification>>(Gson().toJson(it.data), typeToken)

//            if(isNotificationNext == false){
//                results.addAll(changeValue as MutableList<ItemNotification>)
//            }



            results = changeValue as ArrayList<ItemNotification>
            viewModel.adapter.addAllSearch(results)
            totalPages = it.meta?.total_pages!!
            if (currentPage == totalPages) {
                viewModel.adapter.removeLoadingFooter()
            } else if (currentPage <= totalPages) {
                viewModel.adapter.addLoadingFooter()
                isLastPage = false
            } else {
                isLastPage = true
            }

            if (viewModel.adapter.itemCount > 0) {
                binding.idDataNotFound.root.visibility = View.GONE
            } else {
                binding.idDataNotFound.root.visibility = View.VISIBLE
            }
        }

        viewModel.itemNotificationsSecond.observe(requireActivity()) {
            val typeToken = object : TypeToken<List<ItemNotification>>() {}.type
            val changeValue = Gson().fromJson<List<ItemNotification>>(Gson().toJson(it.data), typeToken)
//            if(isNotificationNext == false){
//                results.addAll(changeValue as MutableList<ItemNotification>)
                changeValue.map { _id ->
//                    Log.e("TAG", "newId.notification_id "+Gson().toJson(results.toString()))
//                    changeValue.map { newId ->
                    //var aa =  Gson().toJson(results.toString())

                    if (!Gson().toJson(results.toString()).contains(_id.notification_id.toString())){
//                        Log.e("TAG", "newId.notification_idAA "+_id.notification_id)
                        results.add(_id)
                    } else {
//                        Log.e("TAG", "newId.notification_idBB "+_id.notification_id)
                    }

//                        if (Gson().toJson(results.toString()).toString().contains(_id.notification_id.toString())){
//                            Log.e("TAG", "newId.notification_id "+_id.notification_id)
////
////                            // var zz = newId
////                            results.add(_id)
////                        }
//                    }
                }

//            }
//            results.addAll(changeValue as MutableList<ItemNotification>)



            viewModel.adapter.removeLoadingFooter()
            isLoading = false
            viewModel.adapter.addAllSearch(results)
            if (currentPage != totalPages) viewModel.adapter.addLoadingFooter()
            else isLastPage = true
        }



    }


    override fun onDestroyView() {
//        _binding = null
        deleteAlert?.let {
            deleteAlert!!.cancel()
        }
        super.onDestroyView()
    }
}