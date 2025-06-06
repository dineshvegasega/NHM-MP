package com.nhm.distributor.screens.main.schemes.liveSchemes

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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhm.distributor.R
import com.nhm.distributor.databinding.LiveSchemesBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.models.ItemLiveScheme
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.isBackApp
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.locale
import com.nhm.distributor.utils.PaginationScrollListener
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.isNetworkAvailable
import com.nhm.distributor.utils.mainThread
import com.nhm.distributor.utils.onRightDrawableClicked
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.json.JSONObject


@AndroidEntryPoint
class LiveSchemes : Fragment() {
    private val viewModel: LiveSchemesVM by viewModels()
    private var _binding: LiveSchemesBinding? = null
    private val binding get() = _binding!!

    companion object {
        var isReadLiveSchemes: Boolean? = false
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
        _binding = LiveSchemesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        isReadLiveSchemes = true



        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.live_schemes)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_schemes)

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

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    inclideHeaderSearch.editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        if (count >= 1) R.drawable.ic_cross_white else R.drawable.ic_search,
                        0
                    );
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
            recyclerView.addOnScrollListener(object :
                PaginationScrollListener(recyclerView.layoutManager as LinearLayoutManager) {
                override fun loadMoreItems() {
                    isLoading = true
                    currentPage += 1
                    if (totalPages >= currentPage) {
                        Handler(Looper.myLooper()!!).postDelayed({
                            loadNextPage()
                        }, LOADER_TIME)
                    }
//                    Log.e("TAG", "currentPage "+currentPage)
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
        pageStart = 1
        isLoading = false
        isLastPage = false
        totalPages = 1
        currentPage = pageStart
        results.clear()
        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                val obj: JSONObject = JSONObject().apply {
                    put(page, currentPage)
                    put(search_input, binding.inclideHeaderSearch.editTextSearch.text.toString())
                    put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
                }
                if (requireContext().isNetworkAvailable()) {
                    isBackApp = true
                    viewModel.liveScheme(obj)
                    binding.idNetworkNotFound.root.visibility = View.GONE
                } else {
//                    requireContext().callNetworkDialog()
                    binding.idNetworkNotFound.root.visibility = View.VISIBLE
                }
            }
        }
    }

    fun loadNextPage() {
//        Log.e("TAG", "loadNextPage "+currentPage)
        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                val obj: JSONObject = JSONObject().apply {
                    put(page, currentPage)
                    put(search_input, binding.inclideHeaderSearch.editTextSearch.text.toString())
                    put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
                }
                if (requireContext().isNetworkAvailable()) {
                    isBackApp = true
                    viewModel.liveSchemeSecond(obj)
                } else {
                    requireContext().callNetworkDialog()
                }
            }
        }
    }


    var results: MutableList<ItemLiveScheme> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    private fun observerDataRequest() {
        viewModel.itemLiveSchemes.observe(viewLifecycleOwner, Observer {
            viewModel.show()
            val typeToken = object : TypeToken<List<ItemLiveScheme>>() {}.type
            val changeValue =
                Gson().fromJson<List<ItemLiveScheme>>(Gson().toJson(it.data), typeToken)

            if (IS_LANGUAGE) {
                if (MainActivity.context.get()!!
                        .getString(R.string.englishVal) == "" + locale
                ) {
                    val itemStateTemp = changeValue
                    results.addAll(itemStateTemp)
                    viewModel.adapter.addAllSearch(results)
                    viewModel.hide()

                    if (viewModel.adapter.itemCount > 0) {
                        binding.idDataNotFound.root.visibility = View.GONE
                    } else {
                        binding.idDataNotFound.root.visibility = View.VISIBLE
                    }
                } else {
                    val itemStateTemp = changeValue
                    mainThread {
                            itemStateTemp.forEach {
                                delay(50)
//                                val nameChanged: String = if(it.name != null) viewModel.callApiTranslate(""+locale, it.name) else ""
//                                val descChanged: String = if(it.description != null) viewModel.callApiTranslate(""+locale, it.description) else ""
//
//                                apply {
//                                    it.name = nameChanged
//                                    it.description = descChanged
//                                }
                            }

//                        itemStateTemp.forEach {
//                            delay(50)
//                            val nameChanged: String = if(it.name != null) it.name else ""
//                            val descChanged: String = if(it.description != null) it.description else ""
//                            val convertValue: String = viewModel.callApiTranslate(""+locale, nameChanged +" ⚖ "+ descChanged)
//                            apply {
//                                it.name = convertValue.split("⚖")[0].trim()
//                                it.description = convertValue.split("⚖")[1].trim()
//                            }
//                        }

//                        Log.e("TAG", "PACKAGE_NAME "+PACKAGE_NAME)
//                        Log.e("TAG", "SIGNATURE_NAME "+SIGNATURE_NAME)


//                        if (itemStateTemp.size != 0) {
//                            var title = ""
//                            var description = ""
//                            itemStateTemp.forEach {
//                                delay(50)
//                                title += if (it.name != null) it.name + " ¿ " else " " + " ¿ "
//                                description += if (it.description != null) it.description + " ⬱ " else " " + " ⬱ "
//                            }
//
//                            val nameChanged: String =
//                                viewModel.callApiTranslate("" + viewModel.locale, title)
//                            val nameChangedSplit = nameChanged.split("¿")
//
////                            val descriptionChanged: String =
////                                viewModel.callApiTranslate("" + viewModel.locale, description)
////                            val descriptionChangedSplit = descriptionChanged.split("✍")
//
//                            for (i in 0..itemStateTemp.size - 1) {
//                                itemStateTemp[i].apply {
//                                    this.name = nameChangedSplit[i].trim()
////                                    this.description = descriptionChangedSplit[i].trim()
////                                    Log.e("TAG", "")
//                                }
//                            }
//                        }


//                        ⚖

                        results.addAll(itemStateTemp)
                        viewModel.adapter.addAllSearch(results)
                        viewModel.hide()

                        if (viewModel.adapter.itemCount > 0) {
                            binding.idDataNotFound.root.visibility = View.GONE
                        } else {
                            binding.idDataNotFound.root.visibility = View.VISIBLE
                        }
                    }
                }
            } else {
                val itemStateTemp = changeValue
                results.addAll(itemStateTemp)
                viewModel.adapter.addAllSearch(results)
                viewModel.hide()

                if (viewModel.adapter.itemCount > 0) {
                    binding.idDataNotFound.root.visibility = View.GONE
                } else {
                    binding.idDataNotFound.root.visibility = View.VISIBLE
                }
            }


            totalPages = it.meta?.total_pages!!
            if (currentPage == totalPages) {
                viewModel.adapter.removeLoadingFooter()
            } else if (currentPage <= totalPages) {
                viewModel.adapter.addLoadingFooter()
                isLastPage = false
            } else {
                isLastPage = true
            }
        })


        viewModel.itemLiveSchemesSecond.observe(viewLifecycleOwner, Observer {
            viewModel.show()
            val typeToken = object : TypeToken<List<ItemLiveScheme>>() {}.type
            val changeValue =
                Gson().fromJson<List<ItemLiveScheme>>(Gson().toJson(it.data), typeToken)
            if (IS_LANGUAGE) {
                if (MainActivity.context.get()!!
                        .getString(R.string.englishVal) == "" + locale
                ) {
                    val itemStateTemp = changeValue
                    results.addAll(itemStateTemp)
                    viewModel.adapter.addAllSearch(results)
                    viewModel.hide()
                } else {
                    val itemStateTemp = changeValue
                    mainThread {
                            itemStateTemp.forEach {
                                delay(50)
//                                val nameChanged: String = if(it.name != null) viewModel.callApiTranslate(""+locale, it.name) else ""
//                                val descChanged: String = if(it.description != null) viewModel.callApiTranslate(""+locale, it.description) else ""
//
//                                apply {
//                                    it.name = nameChanged
//                                    it.description = descChanged
//                                }
                            }

//                        itemStateTemp.forEach {
//                            delay(50)
//                            val nameChanged: String = if(it.name != null) it.name else ""
//                            val descChanged: String = if(it.description != null) it.description else ""
//                            val convertValue: String = viewModel.callApiTranslate(""+locale, nameChanged +" ⚖ "+ descChanged)
//                            apply {
//                                it.name = convertValue.split("⚖")[0].trim()
//                                it.description = convertValue.split("⚖")[1].trim()
//                            }
//                        }

//                        if (itemStateTemp.size != 0) {
//                            var title = ""
//                            var description = ""
//                            itemStateTemp.forEach {
//                                title += if (it.name != null) it.name + " ✍ " else " " + " ✍ "
//                                description += if (it.description != null) it.description + " ✍ " else " " + " ✍ "
//                            }
//
//                            val nameChanged: String =
//                                viewModel.callApiTranslate("" + viewModel.locale, title)
//                            val nameChangedSplit = nameChanged.split("✍")
//
//                            val descriptionChanged: String =
//                                viewModel.callApiTranslate("" + viewModel.locale, description)
//                            val descriptionChangedSplit = descriptionChanged.split("✍")
//
//                            for (i in 0..itemStateTemp.size - 1) {
//                                itemStateTemp[i].apply {
//                                    this.name = nameChangedSplit[i].trim()
//                                    this.description = descriptionChangedSplit[i].trim()
//                                }
//                            }
//                        }

                        results.addAll(itemStateTemp)
                        viewModel.adapter.addAllSearch(results)
                        viewModel.hide()
                    }
                }
            } else {
                val itemStateTemp = changeValue
                results.addAll(itemStateTemp)
                viewModel.adapter.addAllSearch(results)
                viewModel.hide()
            }


            viewModel.adapter.removeLoadingFooter()
            isLoading = false
            viewModel.adapter.addAllSearch(results)
            if (currentPage != totalPages) viewModel.adapter.addLoadingFooter()
            else isLastPage = true
        })


        viewModel.applyLink.observe(requireActivity()) { position ->
            if (position != -1) {
//                var data = results.get(position).apply {
//                    user_scheme_status = "applied"
//                }
//                viewModel.adapter.notifyDataSetChanged()
//                if (networkFailed) {
//                    viewModel.viewDetail(data, position = position, requireView(), 3)
//                } else {
//                    requireContext().callNetworkDialog()
//                }
            }
        }


    }


//    override fun onDestroyView() {
//        _binding = null
//        super.onDestroyView()
//    }


}