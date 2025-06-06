package com.nhm.distributor.screens.main.subscription

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhm.distributor.R
import com.nhm.distributor.databinding.SubscriptionHistoryBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.ItemTransactionHistory
import com.nhm.distributor.models.Login
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.utils.PaginationScrollListener
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.callPermissionDialog
import com.nhm.distributor.utils.firstCharIfItIsLowercase
import com.nhm.distributor.utils.getFormatedStringFromDays
import com.nhm.distributor.utils.getNumberToWord
import com.nhm.distributor.utils.getPdfName
import com.nhm.distributor.utils.isNetworkAvailable
import com.nhm.distributor.utils.onRightDrawableClicked
import com.nhm.distributor.utils.roundOffDecimal
import com.nhm.distributor.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class SubscriptionHistory : Fragment(){
    private val viewModel: SubscriptionVM by activityViewModels()
    private var _binding: SubscriptionHistoryBinding ?= null
    private val binding get() = _binding!!


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
        _binding = SubscriptionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }




    private fun callMediaPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            )
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES)
            )
        } else{
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            if(!permissions.entries.toString().contains("false")){
                dispatchTakePictureIntent()
            } else {
                requireActivity().callPermissionDialog{
                    someActivityResultLauncher.launch(this)
                }
            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
    }



    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)

        binding.apply {
            idDataNotFound.textDesc.text = getString(R.string.currently_no_transaction_history)


            recyclerView.setHasFixedSize(true)
            binding.recyclerView.adapter = viewModel.adapter
            binding.recyclerView.itemAnimator = DefaultItemAnimator()

            observerDataRequest()

            recyclerViewScroll()

            searchHandler()

            search.setOnClickListener {
                if (containerSearch.isVisible == true) containerSearch.visibility = View.GONE else containerSearch.visibility = View.VISIBLE
            }

            viewModel.purchaseSubscription.value = false
            viewModel.purchaseSubscription.observe(viewLifecycleOwner, Observer {
                loadFirstPage()
            })
        }
    }




    private fun searchHandler() {
        binding.apply {
            editTextSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    loadFirstPage()
                }
                true
            }

            editTextSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    editTextSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, if(count >= 1) R.drawable.ic_cross_white else R.drawable.ic_search, 0);
                }
            })

            editTextSearch.onRightDrawableClicked {
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
//        viewModel.results.clear()

        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {

                val obj: JSONObject = JSONObject().apply {
                    put(page, currentPage)
                    put(transaction_id, binding.editTextSearch.text.toString())
                    put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
                }
                if(requireContext().isNetworkAvailable()) {
                    viewModel.subscriptionHistory(obj)
                    binding.idNetworkNotFound.root.visibility = View.GONE
                } else {
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
                    put(transaction_id, binding.editTextSearch.text.toString())
                    put(user_id, Gson().fromJson(loginUser, Login::class.java).id)
                }
                if(requireContext().isNetworkAvailable()) {
                    viewModel.subscriptionHistorySecond(obj)
                } else {
                    requireContext().callNetworkDialog()
                }
            }
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun observerDataRequest(){
        viewModel.itemSubscriptionDetail.observe(viewLifecycleOwner, Observer {
            binding.apply {
                textTitleInvoiceNoVal.text = it.order_id
                textTitleInvoiceDateVal.text = it.date_time
                textTitleMembershipIdVal.text = it.membership_id
                textTitlePlanTypeVal.text = it.plan_type.firstCharIfItIsLowercase()
                textTitlePaymentMethodVal.text = it.payment_method
                textItemType.text = it.plan_type.firstCharIfItIsLowercase()
                textQuantity.text = getFormatedStringFromDays(it.payment_validity.toInt(), 2, requireContext())
                textPrice.text = resources.getString(
                    R.string.rupees, it.net_amount.toDouble().roundOffDecimal()
                )

                textDiscountRate.text = root.resources.getString(R.string.discount_pdf, "${it.coupon_discount}%")
                textDiscountPrice.text = resources.getString(
                    R.string.rupees, it?.coupon_amount?.toDouble()?.roundOffDecimal()
                )
                if(it.coupon_discount.toDouble() > 0.0){
                    layoutDiscount.visibility = View.VISIBLE
                } else {
                    layoutDiscount.visibility = View.GONE
                }

                textGstRate.text = root.resources.getString(R.string.gst_pdf, "${it.gst_rate}%")
                textGstPrice.text =  resources.getString(
                    R.string.rupees, it.gst_amount.toDouble().roundOffDecimal()
                )

                textTotalPrice.text = resources.getString(
                    R.string.rupees, it.total_amount.toDouble().roundOffDecimal()
                )

                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val data = Gson().fromJson(loginUser, Login::class.java)
                        textTitleCustomerNameVal.text = data.vendor_first_name+ " " +data.vendor_last_name
                        textTitleCustomerAddressVal.text ="${data.vending_address.replace("\n", ", ")+", "+data.vending_district.name+", "+data.vending_state.name+", "+data.vending_pincode.pincode}"
                    }
                }
                val totalValues = getNumberToWord(it.total_amount.toDouble().roundOffDecimal(), 2, requireContext())
                textWords.setText(Html.fromHtml("<p><b><font color=#0173B7>"+requireContext().resources.getString(R.string._amount_in_words)+" </font><font color=#000000>"+totalValues+"</font></b></p>", Html.FROM_HTML_MODE_COMPACT));
            }
            Handler(Looper.getMainLooper()).postDelayed(Thread {
                callMediaPermissions()
            }, 100)
        })

        viewModel.itemLiveSubscriptionHistory.observe(viewLifecycleOwner, Observer {
            val typeToken = object : TypeToken<List<ItemTransactionHistory>>() {}.type
            val changeValue =
                Gson().fromJson<List<ItemTransactionHistory>>(Gson().toJson(it.data), typeToken)
//            viewModel.results.clear()
//            viewModel.results.addAll(changeValue)
            viewModel.adapter.addAllSearch(changeValue)
//            Log.e("TAG", "viewModel.resultsZZ "+viewModel.results.size)
            if (viewModel.adapter.itemCount > 0) {
                binding.idDataNotFound.root.visibility = View.GONE
            } else {
                binding.idDataNotFound.root.visibility = View.VISIBLE
            }
//            totalPages = 1
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


        viewModel.itemLiveSubscriptionHistorySecond.observe(viewLifecycleOwner, Observer {
            val typeToken = object : TypeToken<List<ItemTransactionHistory>>() {}.type
            val changeValue =
                Gson().fromJson<List<ItemTransactionHistory>>(Gson().toJson(it.data), typeToken)
//            viewModel.results.addAll(changeValue)
            viewModel.adapter.addAll(changeValue)

            viewModel.adapter.removeLoadingFooter()
            isLoading = false
            if (currentPage != totalPages) viewModel.adapter.addLoadingFooter()
            else isLastPage = true
        })
    }






    private fun dispatchTakePictureIntent() {
        val bitmap: Bitmap = getBitmapFromView(binding.mainContainerPdf)
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path, getPdfName())
        try {
            if (!path.exists()) path.mkdirs()
            if (!file.exists()) file.createNewFile()
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            page.canvas.drawBitmap(bitmap, 0F, 0F, null)
            pdfDocument.finishPage(page)
            val ostream: FileOutputStream = FileOutputStream(file)
            pdfDocument.writeTo(ostream)
            ostream.close()
            pdfDocument.close()

            val data= if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file)
            }else{
                val imagePath: File = File(file.absolutePath)
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", imagePath)
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(data, "application/pdf")  // application/pdf   image/*
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
            showSnackBar(getString(R.string.successfully_downloaded))
        } catch (e: IOException) {
        }
    }

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }




}