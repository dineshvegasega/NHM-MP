package com.nhm.distributor.screens.main.complaintsFeedback.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhm.distributor.R
import com.nhm.distributor.databinding.HistoryBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.models.ItemHistory
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.PaginationScrollListener
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.onRightDrawableClicked
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class History : Fragment() {
    private val viewModel: HistoryVM by viewModels()
    private var _binding: HistoryBinding? = null
    private val binding get() = _binding!!

    companion object{
        var isReadComplaintFeedback: Boolean? = false
    }


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
        _binding = HistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        isReadComplaintFeedback = true

        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.AllComplaintsFeedback)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_complaints)

            loadFirstPage()
            recyclerView.setHasFixedSize(true)
            binding.recyclerView.adapter = viewModel.adapter
            binding.recyclerView.itemAnimator = DefaultItemAnimator()

            observerDataRequest()

            recyclerViewScroll()

            searchHandler()
        }
    }



    private fun searchHandler() {
        binding.apply {
            inclideHeaderSearch.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    loadFirstPage()
                }
                true
            }

            inclideHeaderSearch.editTextSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    inclideHeaderSearch.editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, if(count >= 1) R.drawable.ic_cross_white else R.drawable.ic_search, 0);
                }
            })

            inclideHeaderSearch.editTextSearch.onRightDrawableClicked {
                it.text.clear()
                loadFirstPage()
            }
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
        results.clear()
        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                val obj: JSONObject = JSONObject().apply {
                    put(page, currentPage)
                    put(search_input, binding.inclideHeaderSearch.editTextSearch.text.toString())
                    put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
                }
                if(networkFailed) {
                    viewModel.history(obj)
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
                    put(search_input, binding.inclideHeaderSearch.editTextSearch.text.toString())
                    put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
                }
                if(networkFailed) {
                    viewModel.historySecond(obj)
                } else {
                    requireContext().callNetworkDialog()
                }
            }
        }
    }


    var results: MutableList<ItemHistory> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    private fun observerDataRequest(){
        viewModel.itemHistory.observe(requireActivity()) {
            val typeToken = object : TypeToken<List<ItemHistory>>() {}.type
            val changeValue = Gson().fromJson<List<ItemHistory>>(Gson().toJson(it.data), typeToken)
            if(results.size == 0){
                results.addAll(changeValue as MutableList<ItemHistory>)
            }
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

        viewModel.itemHistorySecond.observe(requireActivity()) {
            val typeToken = object : TypeToken<List<ItemHistory>>() {}.type
            val changeValue = Gson().fromJson<List<ItemHistory>>(Gson().toJson(it.data), typeToken)
            results.addAll(changeValue as MutableList<ItemHistory>)
            viewModel.adapter.removeLoadingFooter()
            isLoading = false
            viewModel.adapter.addAllSearch(results)
            if (currentPage != totalPages) viewModel.adapter.addLoadingFooter()
            else isLastPage = true
        }



    }



//    override fun onDestroyView() {
//        _binding = null
//        super.onDestroyView()
//    }
}