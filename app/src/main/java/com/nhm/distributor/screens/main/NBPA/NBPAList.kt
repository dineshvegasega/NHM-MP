package com.nhm.distributor.screens.main.NBPA

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.nhm.distributor.R
import com.nhm.distributor.databinding.DialogFilterBinding
import com.nhm.distributor.databinding.NbpaListBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.networking.Main
import com.nhm.distributor.networking.Screen
import com.nhm.distributor.networking.USER_TYPE
import com.nhm.distributor.networking.USER_TYPE_ADMIN
import com.nhm.distributor.networking.filterByAadhaar
import com.nhm.distributor.networking.filterByEndDate
import com.nhm.distributor.networking.filterByMobile
import com.nhm.distributor.networking.filterByName
import com.nhm.distributor.networking.filterByNikshayId
import com.nhm.distributor.networking.filterByStartDate
import com.nhm.distributor.networking.filterByDistributorNumber
import com.nhm.distributor.networking.user_id
import com.nhm.distributor.networking.user_type
import com.nhm.distributor.screens.mainActivity.MainActivity
//import com.nhm.distributor.screens.main.products.ProductsVM.Companion.isFilterLoad
//import com.nhm.distributor.screens.main.products.ProductsVM.Companion.isProductLoad
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.isFilterLoad
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.isProductLoad
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.userIdForGlobal
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.text.SimpleDateFormat
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.userType
import com.nhm.distributor.utils.callPermissionDialog
import com.nhm.distributor.utils.getChannelName
import com.nhm.distributor.utils.getTitle
import com.nhm.distributor.utils.mainThread
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@AndroidEntryPoint
class NBPAList : Fragment() {
    private val viewModel: NBPAViewModel by viewModels()
    private var _binding: NbpaListBinding? = null
    private val binding get() = _binding!!


    var page: Int = 1


    lateinit var adapter2: NBPAAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NbpaListBinding.inflate(inflater)
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
                getString(R.string.Nikshay_Beneficiary_Program_Activities)
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
                    radioButtonName.text = getString(R.string.enterName)
                    editTextName.setHint(getString(R.string.enterName))

                    readData(LOGIN_DATA) { loginUser ->
                        if (loginUser != null) {
                            val user = Gson().fromJson(loginUser, Login::class.java)
                            if (user.user_role == USER_TYPE_ADMIN) {
                                radioButtonDistributorNumber.visibility = View.VISIBLE
                                editTextDistributorNumber.visibility = View.VISIBLE
                            } else {
                                radioButtonDistributorNumber.visibility = View.GONE
                                editTextDistributorNumber.visibility = View.GONE
                            }
                        }
                    }


                    radioButtonName.singleClick {
                        radioButtonName.isChecked = true
                        radioButtonMobile.isChecked = false
                        radioButtonAadhaar.isChecked = false
                        radioButtonNikshayId.isChecked = false
                        radioButtonDistributorNumber.isChecked = false
                        radioButtonDate.isChecked = false
                    }

                    radioButtonMobile.singleClick {
                        radioButtonName.isChecked = false
                        radioButtonMobile.isChecked = true
                        radioButtonAadhaar.isChecked = false
                        radioButtonNikshayId.isChecked = false
                        radioButtonDistributorNumber.isChecked = false
                        radioButtonDate.isChecked = false
                    }

                    radioButtonAadhaar.singleClick {
                        radioButtonName.isChecked = false
                        radioButtonMobile.isChecked = false
                        radioButtonAadhaar.isChecked = true
                        radioButtonNikshayId.isChecked = false
                        radioButtonDistributorNumber.isChecked = false
                        radioButtonDate.isChecked = false
                    }

                    radioButtonNikshayId.singleClick {
                        radioButtonName.isChecked = false
                        radioButtonMobile.isChecked = false
                        radioButtonAadhaar.isChecked = false
                        radioButtonNikshayId.isChecked = true
                        radioButtonDistributorNumber.isChecked = false
                        radioButtonDate.isChecked = false
                    }

                    radioButtonDistributorNumber.singleClick {
                        radioButtonName.isChecked = false
                        radioButtonMobile.isChecked = false
                        radioButtonAadhaar.isChecked = false
                        radioButtonNikshayId.isChecked = false
                        radioButtonDistributorNumber.isChecked = true
                        radioButtonDate.isChecked = false
                    }

                    radioButtonDate.singleClick {
                        radioButtonName.isChecked = false
                        radioButtonMobile.isChecked = false
                        radioButtonAadhaar.isChecked = false
                        radioButtonNikshayId.isChecked = false
                        radioButtonDistributorNumber.isChecked = false
                        radioButtonDate.isChecked = true
                    }

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
                    radioButtonNikshayId.isChecked = viewModel.filterNikshayIdBoolean
                    radioButtonDistributorNumber.isChecked = viewModel.filterDistributorNumberBoolean
                    radioButtonDate.isChecked =
                        if (viewModel.filterStartDateBoolean || viewModel.filterEndDateBoolean) {
                            true
                        } else {
                            false
                        }

                    editTextName.setText(viewModel.filterName)
                    editTextMobile.setText(viewModel.filterMobile)
                    editTextAadhaar.setText(viewModel.filterAadhaar)
                    editTextNikshayId.setText(viewModel.filterNikshayId)
                    editTextDistributorNumber.setText(viewModel.filterDistributorNumber)
                    editTextStartDate.setText(viewModel.filterStartDate)
                    editTextEndDate.setText(viewModel.filterEndDate)

                    btClose.singleClick {
                        editTextName.setText("")
                        editTextMobile.setText("")
                        editTextAadhaar.setText("")
                        editTextNikshayId.setText("")
                        editTextDistributorNumber.setText("")
                        editTextStartDate.setText("")
                        editTextEndDate.setText("")
                        viewModel.filterName = ""
                        viewModel.filterMobile = ""
                        viewModel.filterAadhaar = ""
                        viewModel.filterNikshayId = ""
                        viewModel.filterDistributorNumber = ""
                        viewModel.filterStartDate = ""
                        viewModel.filterEndDate = ""
                        viewModel.filterNameBoolean = false
                        viewModel.filterMobileBoolean = false
                        viewModel.filterAadhaarBoolean = false
                        viewModel.filterNikshayIdBoolean = false
                        viewModel.filterDistributorNumberBoolean = false
                        viewModel.filterStartDateBoolean = false
                        viewModel.filterEndDateBoolean = false
//                        dialog.dismiss()

                        radioButtonName.isChecked = false
                        radioButtonMobile.isChecked = false
                        radioButtonAadhaar.isChecked = false
                        radioButtonNikshayId.isChecked = false
                        radioButtonDistributorNumber.isChecked = false
                        radioButtonDate.isChecked = false
                    }


                    btApply.singleClick {
                        viewModel.filterName = editTextName.text.toString()
                        viewModel.filterMobile = editTextMobile.text.toString()
                        viewModel.filterAadhaar = editTextAadhaar.text.toString()
                        viewModel.filterNikshayId = editTextNikshayId.text.toString()
                        viewModel.filterDistributorNumber = editTextDistributorNumber.text.toString()
                        viewModel.filterStartDate = editTextStartDate.text.toString()
                        viewModel.filterEndDate = editTextEndDate.text.toString()

                        viewModel.filterNameBoolean = radioButtonName.isChecked
                        viewModel.filterMobileBoolean = radioButtonMobile.isChecked
                        viewModel.filterAadhaarBoolean = radioButtonAadhaar.isChecked
                        viewModel.filterNikshayIdBoolean = radioButtonNikshayId.isChecked
                        viewModel.filterDistributorNumberBoolean = radioButtonDistributorNumber.isChecked
                        viewModel.filterStartDateBoolean = radioButtonDate.isChecked
                        viewModel.filterEndDateBoolean = radioButtonDate.isChecked

                        if (radioButtonDate.isChecked) {
                            try {
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                                val startTimeInMillis =
                                    dateFormat.parse(viewModel.filterStartDate).time.toString()
                                val endTimeInMillis =
                                    dateFormat.parse(viewModel.filterEndDate).time.toString()
                                Log.e("TAG", "startTimeInMillis " + startTimeInMillis)
                                Log.e("TAG", "endTimeInMillis " + endTimeInMillis)
                                if (startTimeInMillis > endTimeInMillis) {
                                    Log.e("TAG", "AAAAAA " + startTimeInMillis)
                                    dialog.dismiss()
                                    showSnackBar(dialogBinding.root.resources.getString(R.string.EndDateshouldbegreaterthanStartDate))
                                } else {
                                    Log.e("TAG", "BBBBBB " + startTimeInMillis)
                                    page = 1
                                    isProductLoad = true
                                    viewModel.itemsProduct.clear()

                                    if (radioButtonName.isChecked && viewModel.filterName.isEmpty()) {
                                        showSnackBar(resources.getString(R.string.enterName))
                                    } else if (radioButtonMobile.isChecked && viewModel.filterMobile.isEmpty()) {
                                        showSnackBar(resources.getString(R.string.enterMobileNumberForm))
                                    } else if (radioButtonAadhaar.isChecked && viewModel.filterAadhaar.isEmpty()) {
                                        showSnackBar(resources.getString(R.string.enter_valid_aadhaar_number))
                                    } else if (radioButtonNikshayId.isChecked && viewModel.filterNikshayId.isEmpty()) {
                                        showSnackBar(resources.getString(R.string.enter_valid_nikshay_id))
                                    } else {
                                        filters(
                                            if (radioButtonName.isChecked) viewModel.filterName else "",
                                            if (radioButtonMobile.isChecked) viewModel.filterMobile else "",
                                            if (radioButtonAadhaar.isChecked) viewModel.filterAadhaar else "",
                                            if (radioButtonNikshayId.isChecked) viewModel.filterNikshayId else "",
                                            if (radioButtonDistributorNumber.isChecked) viewModel.filterDistributorNumber else "",
                                            if (radioButtonDate.isChecked) viewModel.filterStartDate + " 00:00:00" else "",
                                            if (radioButtonDate.isChecked) viewModel.filterEndDate + " 23:59:59" else "",
                                        )
                                    }


                                    dialog.dismiss()
                                }
                            } catch (e: Exception) {
                                Log.e("TAG", "CCCCCC " + e.message)
                                dialog.dismiss()
                                showSnackBar(dialogBinding.root.resources.getString(R.string.SelectValidStartDateandEndDate))
                            }
                        } else {
                            Log.e("TAG", "DDDDDD ")

                            page = 1
                            isProductLoad = true
                            viewModel.itemsProduct.clear()

                            if (radioButtonName.isChecked && viewModel.filterName.isEmpty()) {
                                showSnackBar(resources.getString(R.string.enterName))
                            } else if (radioButtonMobile.isChecked && viewModel.filterMobile.isEmpty()) {
                                showSnackBar(resources.getString(R.string.enterMobileNumberForm))
                            } else if (radioButtonAadhaar.isChecked && viewModel.filterAadhaar.isEmpty()) {
                                showSnackBar(resources.getString(R.string.enter_valid_aadhaar_number))
                            } else if (radioButtonNikshayId.isChecked && viewModel.filterNikshayId.isEmpty()) {
                                showSnackBar(resources.getString(R.string.enter_valid_nikshay_id))
                            } else if (radioButtonDistributorNumber.isChecked && viewModel.filterDistributorNumber.isEmpty()) {
                                showSnackBar(resources.getString(R.string.distributorMobile))
                            } else {
                                filters(
                                    if (radioButtonName.isChecked) viewModel.filterName else "",
                                    if (radioButtonMobile.isChecked) viewModel.filterMobile else "",
                                    if (radioButtonAadhaar.isChecked) viewModel.filterAadhaar else "",
                                    if (radioButtonNikshayId.isChecked) viewModel.filterNikshayId else "",
                                    if (radioButtonDistributorNumber.isChecked) viewModel.filterDistributorNumber else "",
                                    if (radioButtonDate.isChecked) viewModel.filterStartDate + " 00:00:00" else "",
                                    if (radioButtonDate.isChecked) viewModel.filterEndDate + " 23:59:59" else ""
                                )
                            }

//                            filters(
//                                if (radioButtonName.isChecked) viewModel.filterName else "",
//                                if (radioButtonMobile.isChecked) viewModel.filterMobile else "",
//                                if (radioButtonAadhaar.isChecked) viewModel.filterAadhaar else "",
//                                if (radioButtonDate.isChecked) viewModel.filterStartDate + " 00:00:00" else "",
//                                if (radioButtonDate.isChecked) viewModel.filterEndDate + " 23:59:59" else "",
//                            )
                            dialog.dismiss()
                        }

//                        startNewDownload("https://www.fsa.usda.gov/Internet/FSA_File/tech_assist.pdf")
//                        callMediaPermissions()

                    }
                }
            }



            adapter2 = NBPAAdapter()
            binding.rvList2.adapter = adapter2



            if (isFilterLoad) {
                page = 1
                isProductLoad = true
                viewModel.itemsProduct.clear()
            }

            Log.e("TAG", "isFilterLoad " + isFilterLoad)
            Log.e("TAG", "isProductLoadAA " + isProductLoad)

            filters(
                "",
                "",
                "",
                "",
                "",
                "",
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
                    isProductLoad = true

                    filters(
                        if (viewModel.filterNameBoolean) viewModel.filterName else "",
                        if (viewModel.filterMobileBoolean) viewModel.filterMobile else "",
                        if (viewModel.filterAadhaarBoolean) viewModel.filterAadhaar else "",
                        if (viewModel.filterNikshayIdBoolean) viewModel.filterNikshayId else "",
                        if (viewModel.filterDistributorNumberBoolean) viewModel.filterDistributorNumber else "",
                        if (viewModel.filterStartDateBoolean) viewModel.filterStartDate + " 00:00:00" else "",
                        if (viewModel.filterEndDateBoolean) viewModel.filterEndDate + " 23:59:59" else "",
                    )
                }
            })


            viewModel.itemProducts?.observe(viewLifecycleOwner) {
                Log.e("TAG", "itemsss " + it.data.size)
                if (it.data.size != 0) {
//                    idPBLoading.visibility = View.VISIBLE
                } else {
                    idPBLoading.visibility = View.GONE
                }


                Log.e("TAG", "isProductLoad " + isProductLoad)
                if (isProductLoad) {
                    viewModel.itemsProduct.addAll(it.data)
                    isProductLoad = false
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
        filterName: String,
        filterMobile: String,
        filterAadhaar: String,
        filterNikshayId: String,
        filterDistributorNumber: String,
        filterStartDate: String,
        filterEndDate: String
    ) {
        var obj: JSONObject = JSONObject()
        Log.e("TAG", "page " + page)
        Log.e("TAG", "filterStartDate " + filterStartDate)
        Log.e("TAG", "filterEndDate " + filterEndDate)
        if (userType == USER_TYPE) {
            obj = JSONObject().apply {
                put(com.nhm.distributor.networking.page, "" + page)
                put(user_type, USER_TYPE)
//                put(user_id, userIdForGlobal)
                put(filterByName, filterName)
                put(filterByMobile, filterMobile)
                put(filterByAadhaar, filterAadhaar)
                put(filterByNikshayId, filterNikshayId)
                put(filterByStartDate, filterStartDate)
                put(filterByEndDate, filterEndDate)
            }
        }

        if (userType == USER_TYPE_ADMIN) {
            obj = JSONObject().apply {
                put(com.nhm.distributor.networking.page, page)
                put(user_type, USER_TYPE_ADMIN)
                put(filterByName, filterName)
                put(filterByMobile, filterMobile)
                put(filterByAadhaar, filterAadhaar)
                put(filterByNikshayId, filterNikshayId)
                put(filterByDistributorNumber, filterDistributorNumber)
                put(filterByStartDate, filterStartDate)
                put(filterByEndDate, filterEndDate)
            }
        }
        viewModel.getProducts(obj, page)
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun startNewDownload(url: String?) {
        val downloadmanager: DownloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri: Uri? = Uri.parse(url)
        val request: DownloadManager.Request = DownloadManager.Request(uri)
        request.setTitle("Java_Programming.pdf")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "Java_Programming.xlsx"
        )
        val downloadId = downloadmanager.enqueue(request)
    }
//    override fun onStop() {
//        super.onStop()
//        isProductLoad = true
//        isProductLoadMember = true
//    }


    fun downloadSS(link: String, path: String, progress: ((Long, Long) -> Unit)? = null): Long {
        try {
            val url = URL(link)
            val connection = url.openConnection()
            connection.connect()
            val length =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) connection.contentLengthLong else connection.contentLength.toLong()
            url.openStream().use { input ->
                FileOutputStream(File(path)).use { output ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytesRead = input.read(buffer)
                    var bytesCopied = 0L
                    while (bytesRead >= 0) {
                        output.write(buffer, 0, bytesRead)
                        bytesCopied += bytesRead
                        progress?.invoke(bytesCopied, length)
                        bytesRead = input.read(buffer)
                    }
                    return bytesCopied
                }
            }
        } catch (e: Exception) {
            Log.e("mTAG", "Progress: ${e.message}")
        }
        return 0
    }

    private fun download(url: String, title: String) {
        val directory = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )

        if (!directory.exists()) {
            directory.mkdir()
        }

        val fileName = File(directory, title)

        if (!fileName.exists()) {
            fileName.createNewFile()
        }


//        val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//            StrictMode.setThreadPolicy(policy)


//        mainThread {
//            val handler = object : Handler(Looper.getMainLooper()) {
//                override fun handleMessage(msg: Message) {
//                    val (progress, length) = msg.obj as Pair<Long, Long>
//                    Log.e("mTAG", "Progress: $progress, Length: $length")
//                }
//            }
//            downloadSS(url, fileName.toString()) { progress, length ->
//                handler.sendMessage(handler.obtainMessage(0, progress to length))
//            }
//        }




//        downloadSS(url, fileName.toString()) { progress, length ->
//            // handling the result on main thread
//            handler.sendMessage(handler.obtainMessage(0, progress to length))
//
////            val progressprogress = (progress * 100 / length).toInt()
////
////
////            mainThread {
////                binding.apply {
////                    progressbar.setIndeterminate(false);
////                    progressbar.setMax(100);
////                    progressbar.setProgress(progressprogress);
////                    progressbar.visibility = View.VISIBLE
////                }
////
////                if (progress == length) {
////                    Log.e("mTAG", "fileNamefileName: $fileName")
////                    notification(100, title)
//////                        Handler(Looper.getMainLooper()).postDelayed({
////                    startActivity(goToFileIntent(MainActivity.Companion.context.get()!!, fileName));
//////                        }, 100)
////                    binding.progressbar.visibility = View.GONE
////                }
////            }
//
////            requireActivity().runOnUiThread {
////                binding.apply {
////                    progressbar.setIndeterminate(false);
////                    progressbar.setMax(100);
////                    progressbar.setProgress(progressprogress);
////                    progressbar.visibility = View.VISIBLE
////                }
////
////                if (progress == length) {
////                    Log.e("mTAG", "fileNamefileName: $fileName")
////                    notification(100, title)
//////                        Handler(Looper.getMainLooper()).postDelayed({
////                    startActivity(goToFileIntent(MainActivity.Companion.context.get()!!, fileName));
//////                        }, 100)
////                    binding.progressbar.visibility = View.GONE
////                }
////            }
//
////            Handler(Looper.getMainLooper()).postDelayed({
////                binding.apply {
////                    progressbar.setIndeterminate(false);
////                    progressbar.setMax(100);
////                    progressbar.setProgress(progressprogress);
////                    progressbar.visibility = View.VISIBLE
////                }
////
////                if (progress == length) {
////                    Log.e("mTAG", "fileNamefileName: $fileName")
////                    notification(100, title)
//////                        Handler(Looper.getMainLooper()).postDelayed({
////                    startActivity(goToFileIntent(MainActivity.Companion.context.get()!!, fileName));
//////                        }, 100)
////                    binding.progressbar.visibility = View.GONE
////                }
////            }, 100)
//        }


//        val u = URL(url)
//
//        val connect = u.openConnection() as HttpURLConnection
//
//        if (connect.getResponseCode() != HttpURLConnection.HTTP_OK) {
//            Log.w("ERROR SERVER RETURNED HTTP", connect.getResponseCode().toString() + "")
//        }
//
//        connect.getInputStream().use { `is` ->
//            FileOutputStream(fileName).use { fos ->
//                val bytes = ByteArray(1024)
//                var b = 0
//                while ((`is`.read(bytes, 0, 1024).also { b = it }) != -1) {
//                    fos.write(bytes, 0, b)
//                }
//            }
//        }


        // Create bamchik folder if not created yet
//        val folder = File(getExternalFilesDir(null)?.absoluteFile, "MusicTrack")
//        val check = folder.mkdirs()
//        var path = getExternalFilesDir(null)?.absolutePath + "/MusicTrack/music." + "fileExt"
//        val SDK_INT = Build.VERSION.SDK_INT
//        if (SDK_INT > 8) {
//            val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//            StrictMode.setThreadPolicy(policy)
        binding.progressbar.visibility = View.VISIBLE
            val executor: ExecutorService = Executors.newSingleThreadExecutor()
            val handler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    // Length is the total size of file and progress is how much file have been downloaded, now you can show progress according to this
                    val (progress, length) = msg.obj as Pair<Long, Long>
                    Log.v("mTAG", "Progress: $progress, Length: $length")
                    val progressprogress = (progress * 100 / length).toInt()
                    Log.e("mTAG", "progressprogress: $progressprogress")

                    binding.apply {
                        progressbar.setIndeterminate(false);
                        progressbar.setMax(100);
                        progressbar.setProgress(progressprogress);
                    }

                    if(progress == length){
                        Log.e("mTAG", "fileNamefileName: $fileName")
                        binding.apply {
                            progressbar.setIndeterminate(false);
                            progressbar.setProgress(0);
                            progressbar.setMax(100);
                            progressbar.visibility = View.GONE
                        }


                        notification(100, title)
//                        Handler(Looper.getMainLooper()).postDelayed({
                            startActivity(goToFileIntent(MainActivity.Companion.context.get()!!, fileName));
//                        }, 100)

                    }

                }

            }

            executor.execute {
                //Background work here
                downloadSS(url,  fileName.toString()) { progress, length ->
                    // handling the result on main thread
                    handler.sendMessage(handler.obtainMessage(0, progress to length))
                }
//                handler.post {
//
//                    //UI Thread work here
////                    progress
////                    Log.v("mTAG", "Progress: $progress, Length: $progress")
//                }
//            }
        }
    }


    fun goToFileIntent(context: Context, file: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            FileProvider.getUriForFile(
                context,
                context.getApplicationContext().getPackageName() + ".provider",
                file
            )
        } else {
            val imagePath: File = File(file.absolutePath)
            FileProvider.getUriForFile(
                context,
                context.getApplicationContext().getPackageName() + ".provider",
                imagePath
            )
        }

//        val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val mimeType = context.contentResolver.getType(data)
        intent.setDataAndType(data, mimeType)
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        return intent
    }


    @SuppressLint("MutableImplicitPendingIntent")
    fun notification(notiId: Int, title: String) {
        val chName = "Download Sheets"
        val intent = Intent(Intent.ACTION_VIEW)

        val directory = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS
        )

        if (!directory.exists()) {
            directory.mkdir()
        }

        val fileName = File(directory, title)

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            FileProvider.getUriForFile(
                requireActivity(),
                requireActivity().getApplicationContext().getPackageName() + ".provider",
                fileName
            )
        } else {
            val imagePath: File = File(fileName.absolutePath)
            FileProvider.getUriForFile(
                requireActivity(),
                requireActivity().getApplicationContext().getPackageName() + ".provider",
                imagePath
            )
        }

//        val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val mimeType = requireActivity().contentResolver.getType(data)
        intent.setDataAndType(data, mimeType)
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION


        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            PendingIntent.getActivity(
                requireActivity(),
                notiId,
                intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                requireActivity(),
                notiId,
                intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        } else {
            PendingIntent.getActivity(
                requireActivity(),
                notiId,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        }


        val importance = NotificationManager.IMPORTANCE_HIGH
        var mChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = NotificationChannel(chName, chName, importance)
            mChannel.enableLights(true)
            mChannel.lightColor = Color.WHITE
            mChannel.setShowBadge(true)
            mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }


        val notification = NotificationCompat.Builder(requireActivity(), chName)
            .setSmallIcon(R.mipmap.ic_launcher) //.setLargeIcon(icon)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentTitle(getString(R.string.app_name))
            .setStyle(NotificationCompat.BigTextStyle().bigText(title))
            .setAutoCancel(true)
            .setChannelId(chName)
            .setContentIntent(pendingIntent)
            .setContentText(title).build()
        val notificationManager =
            requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationManager.createNotificationChannel(
            mChannel!!
        )
        notificationManager.notify(notiId, notification)
    }


    private fun callMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
        } else {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            if (!permissions.entries.toString().contains("false")) {
                download(
                    "https://dl.ebooksworld.ir/motoman/Apress.React.Native.for.Mobile.Development.2nd.Edition.www.EBooksWorld.ir.pdf",
                    "SSSSS.pdf"
                )
            } else {
                requireActivity().callPermissionDialog {
                    someActivityResultLauncher.launch(this)
                }
            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
    }

}