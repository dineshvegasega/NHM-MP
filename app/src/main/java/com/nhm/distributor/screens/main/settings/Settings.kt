package com.nhm.distributor.screens.main.settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import com.google.android.play.core.appupdate.AppUpdateManager
//import com.google.android.play.core.appupdate.AppUpdateManagerFactory
//import com.google.android.play.core.install.InstallStateUpdatedListener
//import com.google.android.play.core.install.model.ActivityResult
//import com.google.android.play.core.install.model.AppUpdateType
//import com.google.android.play.core.install.model.InstallStatus
//import com.google.android.play.core.install.model.UpdateAvailability
//import com.google.android.play.core.review.ReviewInfo
//import com.google.android.play.core.review.ReviewManagerFactory
//import com.google.android.play.core.tasks.OnCompleteListener
//import com.google.android.play.core.tasks.Task
import com.google.gson.Gson
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ItemLanguageStartBinding
import com.nhm.distributor.databinding.SettingsBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.genericAdapter.GenericAdapter
import com.nhm.distributor.models.Login
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.getBitmapFromVectorDrawable
import com.nhm.distributor.utils.loadHtml
import com.nhm.distributor.utils.mainThread
import com.nhm.distributor.utils.saveImageInQ
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody


@AndroidEntryPoint
class Settings : Fragment() {
    private val viewModel: SettingsVM by viewModels()
    private var _binding: SettingsBinding? = null
    private val binding get() = _binding!!

//    private lateinit var appUpdateManager: AppUpdateManager

    var languageAlert: BottomSheetDialog? = null
    var notificationAlert: AlertDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsBinding.inflate(inflater)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)

        binding.apply {
//            appUpdateManager = AppUpdateManagerFactory.create(requireContext())

            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.settings)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE


            val manager = requireContext().packageManager
            val info = manager?.getPackageInfo(requireContext().packageName, 0)
            val versionName = info?.versionName
            textAppVersionTxt.text = getString(R.string.app_version_1_0, versionName)

//            val appUpdateManager = AppUpdateManagerFactory.create(requireContext())
//            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
////                Log.e("TAG", "AAAAAA")
//                val currentVersion = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).longVersionCode
//                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                ) {
////                    Log.e("TAG", "BBBBBB")
//                    layout5.visibility = View.VISIBLE
//                } else {
////                    Log.e("TAG", "CCCCCC")
//                    layout5.visibility = View.GONE
//                }
//            }



            val lastSplit = versionName!!.split(".")[0]+"."+ versionName!!.split(".")[1]+"."+(versionName!!.split(".")[2].toInt()+1)
            textAboutUsNewVersionTxt.setText(getString(R.string.new_version_available_1_0, lastSplit))

            viewModel.appLanguage.observe(viewLifecycleOwner, Observer {
                editTextSelectLanguage.setText(it)
            })


            editTextSelectLanguage.singleClick {
                if (languageAlert?.isShowing == true) {
                    return@singleClick
                }
                val dialogView: View = LayoutInflater.from(requireContext())
                    .inflate(R.layout.dialog_bottom_your_booking2, null)
                languageAlert = BottomSheetDialog(requireContext())
                languageAlert?.setContentView(dialogView)
                languageAlert?.let {
                    languageAlert?.show()
                }
                val window = languageAlert?.window
                window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                window?.setBackgroundDrawableResource(android.R.color.transparent)

                val pastBookingAdapter =
                    object : GenericAdapter<ItemLanguageStartBinding, SettingsVM.Item>() {
                        override fun onCreateView(
                            inflater: LayoutInflater,
                            parent: ViewGroup,
                            viewType: Int
                        ) = ItemLanguageStartBinding.inflate(inflater, parent, false)

                        @SuppressLint("NotifyDataSetChanged")
                        override fun onBindHolder(
                            binding: ItemLanguageStartBinding,
                            dataClass: SettingsVM.Item,
                            position: Int
                        ) {
                            binding.btImage.setImageDrawable(
                                ContextCompat.getDrawable(
                                    binding.root.context,
                                    if (dataClass.isSelected == true) R.drawable.radio_sec_filled else R.drawable.radio_sec_empty
                                )
                            );
                            binding.btLanguage.text = dataClass.name
                            binding.btLanguage.singleClick {
                                val list = currentList
                                list.forEach {
                                    it.isSelected = dataClass == it
                                }
                                notifyDataSetChanged()
                                callLanguageApi(dataClass.locale, 1)
                                Handler(Looper.getMainLooper()).postDelayed(Thread {
                                    MainActivity.mainActivity.get()?.runOnUiThread {
                                        languageAlert?.dismiss()
                                    }
                                }, 100)
                            }

                            binding.mainContainer.singleClick {
                                val list = currentList
                                list.forEach {
                                    it.isSelected = dataClass == it
                                }
                                notifyDataSetChanged()
                                callLanguageApi(dataClass.locale, 1)
                                Handler(Looper.getMainLooper()).postDelayed(Thread {
                                    MainActivity.mainActivity.get()?.runOnUiThread {
                                        languageAlert?.dismiss()
                                    }
                                }, 100)
                            }
                        }
                    }
                val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvList)

                pastBookingAdapter.submitList(viewModel.itemMain)
                recyclerView.adapter = pastBookingAdapter
            }


//            readData(LOGIN_DATA) { loginUser ->
//                if (loginUser != null) {
//                    val user = Gson().fromJson(loginUser, Login::class.java)
////                    switchNotifications.isChecked = if (user.notification == "Yes") true else false
//
//                    if(user!!.user_role == "member"){
//                        editTextGeoLocation.visibility = View.VISIBLE
//                    }
//                    if(user!!.user_role == "controller"){
//                        editTextGeoLocation.visibility = View.GONE
//                    }
//                }
//            }



            switchNotifications.singleClick {
                if (notificationAlert?.isShowing == true) {
                    return@singleClick
                }

                readData(LOGIN_DATA) { loginUser ->
                    val user = Gson().fromJson(loginUser, Login::class.java)
                    if (loginUser != null) {
                        notificationAlert =
                            MaterialAlertDialogBuilder(requireContext(), R.style.LogoutDialogTheme)
                                .setTitle(resources.getString(R.string.app_name))
                                .setMessage(
                                    if (user.notification == "Yes") resources.getString(R.string.are_your_sure_want_to_turn_Off) else resources.getString(
                                        R.string.are_your_sure_want_to_turn_On
                                    )
                                )
                                .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                                    dialog.dismiss()
                                    val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart(user_type, USER_TYPE)
                                    requestBody.addFormDataPart(user_id, "" + user.id)
                                    if (user.notification == "Yes") {
                                        requestBody.addFormDataPart(notification, "No")
                                    } else {
                                        requestBody.addFormDataPart(notification, "Yes")
                                    }
                                    if (networkFailed) {
                                        viewModel.notificationUpdate(
                                            "" + user.id,
                                            requestBody.build(),
                                            0
                                        )
                                    } else {
                                        requireContext().callNetworkDialog()
                                    }
                                }
                                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                                    dialog.dismiss()
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        MainActivity.binding.drawerLayout.close()
                                    }, 500)
                                }
                                .setCancelable(false)
                                .show()
                        switchNotifications.isChecked =
                            if (user.notification == "Yes") true else false
                    }
                }


                viewModel.itemNotificationUpdateResult.value = false
                viewModel.itemNotificationUpdateResult.observe(requireActivity(), Observer {
                    readData(LOGIN_DATA) { loginUser ->
                        if (loginUser != null) {
                            val noti = Gson().fromJson(loginUser, Login::class.java).notification
                            switchNotifications.isChecked = if (noti == "Yes") true else false
                        }
                    }
                })
            }


            editTextChangeNumber.singleClick {
                view.findNavController().navigate(R.id.action_settings_to_changeMobile)
            }

            editTextGeoLocation.singleClick {
                view.findNavController().navigate(R.id.action_settings_to_setLocation)
            }


            editTextChangePassword.singleClick {
                view.findNavController().navigate(R.id.action_settings_to_changePassword)
            }


            btLogout.singleClick {
                MainActivity.mainActivity.get()!!.callLogoutDialog()
            }


            btDeleteAccount.singleClick {
                MainActivity.mainActivity.get()!!.callDeleteDialog()
            }


            textAboutUsTxt.singleClick {
                viewModel.show()
                mainThread {
                    openDialog(1)
                }
            }

            textPrivacyPolicyTxt.singleClick {
                viewModel.show()
                mainThread {
                    openDialog(2)
                }
            }

            textTermsConditionsTxt.singleClick {
                viewModel.show()
                mainThread {
                    openDialog(3)
                }
            }

            textRateOurAppTxt.singleClick {
//                showRatingUserInterface(requireActivity())
            }

            textShareOurAppTxt.singleClick { //
                val uriArrayList = ArrayList<Uri>()
                uriArrayList.add(saveImageInQ(requireContext(), getBitmapFromVectorDrawable(requireContext(), R.drawable.main_logo)))
                val shareContent: String = requireContext().resources.getString(R.string.share_content)
                val appLink: String = "https://play.google.com/store/apps/details?id="+requireContext().packageName
                val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND_MULTIPLE
                    shareIntent.setType("application/pdf/*|image/*|text/plain");
                    shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "$shareContent\n\n $appLink")
                    view.context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            "Share to"
                        )
                    )
            }



            textAboutUsNewVersionTxt.singleClick {
//                checkUpdate()
            }
        }
    }


//    private fun checkUpdate() {
//        appUpdateManager
//            .appUpdateInfo
//            .addOnSuccessListener { appUpdateInfo ->
//                val starter =
//                    IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
//                        val request = IntentSenderRequest.Builder(intent)
//                            .setFillInIntent(fillInIntent)
//                            .setFlags(flagsValues, flagsMask)
//                            .build()
//                        resultUpdate.launch(request)
//                    }
//
//                appUpdateManager.startUpdateFlowForResult(
//                    appUpdateInfo,
//                    AppUpdateType.FLEXIBLE,
//                    starter,
//                    123,
//                )
//            }
//    }
//
//    var resultUpdate =
//        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
////            Log.e("TAG", "result.resultCode " + result.resultCode)
//            if (result.resultCode == AppCompatActivity.RESULT_OK) {
//                // Handle successful app update
////                Log.e("TAG", "RESULT_OK")
//            } else if (result.resultCode == AppCompatActivity.RESULT_CANCELED) {
////                finish()
//            } else if (result.resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
////                finish()
//            } else {
////                finish()
//            }
//        }
//
//
//    private fun showRatingUserInterface(activity: Activity) {
//        val manager = ReviewManagerFactory.create(activity)
//        val request: Task<ReviewInfo> = manager.requestReviewFlow()
//        request.addOnCompleteListener(object : OnCompleteListener<ReviewInfo?> {
//            override fun onComplete(task: Task<ReviewInfo?>) {
////                Log.e("TAG", "onComplete1 "+task.result)
//                if (task.isSuccessful()) {
//                    val reviewInfo: ReviewInfo = task.getResult()
//                    val flow: Task<Void> = manager.launchReviewFlow(activity, reviewInfo)
//                    flow.addOnCompleteListener(object : OnCompleteListener<Void?> {
//                        override fun onComplete(task: Task<Void?>) {
////                            Log.e("TAG", "onComplete2 "+task.result)
//                        }
//                    })
//                }
//            }
//        })
//    }

    fun callLanguageApi(locale: String, value: Int) {
        readData(LOGIN_DATA) { loginUser ->
            if (loginUser != null) {
                val _id = Gson().fromJson(loginUser, Login::class.java).id
                val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(user_type, USER_TYPE)
                requestBody.addFormDataPart(user_id, "" + _id)
                requestBody.addFormDataPart(language, "/en/" + locale)
                if (networkFailed) {
                    viewModel.notificationUpdate("" + _id, requestBody.build(), value)
                } else {
                    requireContext().callNetworkDialog()
                }
            }
        }
    }


    private fun openDialog(type: Int) {
        val mybuilder = Dialog(requireActivity())
        mybuilder.setContentView(R.layout.dialog_load_html)
        mybuilder.show()
        val window = mybuilder.window
        window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(R.color._00000000)
        val yes = mybuilder.findViewById<AppCompatImageView>(R.id.imageCross)
        val title = mybuilder.findViewById<AppCompatTextView>(R.id.textTitleMain)
        val text = mybuilder.findViewById<AppCompatTextView>(R.id.textTitleText)
        when (type) {
            1 -> title.text = resources.getString(R.string.about_us)
            2 -> title.text = resources.getString(R.string.privacy_policy)
            3 -> title.text = resources.getString(R.string.terms_amp_conditions)
        }
        requireContext().loadHtml(type) {
            text.text = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        yes?.singleClick {
            mybuilder.dismiss()
        }
        viewModel.hide()
    }



//
//    val listener = InstallStateUpdatedListener { state ->
//        if (state.installStatus() == InstallStatus.DOWNLOADING) {
//            val bytesDownloaded = state.bytesDownloaded()
//            val totalBytesToDownload = state.totalBytesToDownload()
//        }
//        if (state.installStatus() == InstallStatus.DOWNLOADED) {
//            Snackbar.make(
//                requireView(),
//                "New app is ready",
//                Snackbar.LENGTH_INDEFINITE
//            ).setAction("Restart") {
//                appUpdateManager.completeUpdate()
//            }.show()
//        }
//    }



    override fun onResume() {
        super.onResume()
//        appUpdateManager.registerListener(listener)
    }

    override fun onDestroyView() {
        languageAlert?.let {
            languageAlert!!.cancel()
        }
        _binding = null
//        appUpdateManager.unregisterListener(listener)
        super.onDestroyView()
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