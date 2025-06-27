package com.nhm.distributor.screens.onboarding.register

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.model.LatLng
import com.nhm.distributor.networking.*
import com.nhm.distributor.R
import com.nhm.distributor.databinding.RegisterBinding
import com.nhm.distributor.screens.interfaces.CallBackListener
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.getAddress
import com.nhm.distributor.utils.getImageName
import com.nhm.distributor.utils.mainThread
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import com.nhm.distributor.utils.updatePagerHeightForChild
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class Register : Fragment(), CallBackListener {
    private var _binding: RegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterVM by activityViewModels()


    companion object {
        var callBackListener: CallBackListener? = null
        var imagePath : String = ""
        var latLong : LatLng = LatLng(0.0,0.0)
        var dateTime : String = ""
        var returnImagePath : String = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = RegisterBinding.inflate(inflater)
        return binding.root
    }

    var tabPosition: Int = 0;

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)
        callBackListener = this

        binding.apply {
            val adapter = RegisterAdapter(requireActivity())
            adapter.notifyDataSetChanged()
            introViewPager.adapter = adapter
            introViewPager.setUserInputEnabled(false);

//            viewModel.isAgree.value = false
            viewModel.isAgree.observe(viewLifecycleOwner, Observer {
                if (tabPosition == 2) {
                    if (it == true) {
                        btSignIn.setEnabled(true)
                        btSignIn.setBackgroundTintList(
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    getResources(), R.color._E79D46, null
                                )
                            )
                        )
                    } else {
                        btSignIn.setEnabled(false)
                        btSignIn.setBackgroundTintList(
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    getResources(), R.color._999999, null
                                )
                            )
                        )
                    }
                }

            })



            loadProgress(tabPosition)

            btSignIn.singleClick {
                if (tabPosition == 0) {
                    Register1.callBackListener!!.onCallBack(1)
                    btSignIn.setText(getString(R.string.continues))
                } else if (tabPosition == 1) {
                    Register2.callBackListener!!.onCallBack(3)
                    btSignIn.setText(getString(R.string.RegisterNow))
                } else if (tabPosition == 2) {
                    Register3.callBackListener!!.onCallBack(5)
                }
                loadProgress(tabPosition)


            }

            textBack.singleClick {
                if (tabPosition == 0) {
                    view.findNavController().navigateUp()
                } else if (tabPosition == 1) {
                    introViewPager.setCurrentItem(0, false)
                    btSignIn.setText(getString(R.string.continues))
                    btSignIn.setEnabled(true)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._E79D46, null
                            )
                        )
                    )
                } else if (tabPosition == 2) {
                    introViewPager.setCurrentItem(1, false)
                    btSignIn.setText(getString(R.string.continues))
                    btSignIn.setEnabled(true)
                    btSignIn.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ResourcesCompat.getColor(
                                getResources(), R.color._E79D46, null
                            )
                        )
                    )
                }
                loadProgress(tabPosition)
            }

            introViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabPosition = position
                    if (position == 2) {
                        btSignIn.setEnabled(false)
                        btSignIn.setBackgroundTintList(
                            ColorStateList.valueOf(
                                ResourcesCompat.getColor(
                                    getResources(), R.color._999999, null
                                )
                            )
                        )
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })

            introViewPager.setPageTransformer { page, position ->
                introViewPager.updatePagerHeightForChild(page)
            }
        }

    }

    private fun loadProgress(tabPosition: Int) {
        if (tabPosition == 0) {
            binding.view1.setBackgroundTintList(
                ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        getResources(), R.color._E79D46, null
                    )
                )
            )
            binding.view2.setBackgroundTintList(
                ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        getResources(), R.color._D9D9D9, null
                    )
                )
            )
            binding.view3.setBackgroundTintList(
                ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        getResources(), R.color._D9D9D9, null
                    )
                )
            )
        } else if (tabPosition == 1) {
            binding.view1.setBackgroundTintList(
                ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        getResources(), R.color._E79D46, null
                    )
                )
            )
            binding.view2.setBackgroundTintList(
                ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        getResources(), R.color._E79D46, null
                    )
                )
            )
            binding.view3.setBackgroundTintList(
                ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        getResources(), R.color._D9D9D9, null
                    )
                )
            )
        } else if (tabPosition == 2) {
            binding.view1.setBackgroundTintList(
                ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        getResources(), R.color._E79D46, null
                    )
                )
            )
            binding.view2.setBackgroundTintList(
                ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        getResources(), R.color._E79D46, null
                    )
                )
            )
            binding.view3.setBackgroundTintList(
                ColorStateList.valueOf(
                    ResourcesCompat.getColor(
                        getResources(), R.color._E79D46, null
                    )
                )
            )
        }
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCallBack(pos: Int) {
        binding.apply {
            if (pos == 1001) {
                Log.e("TAG", "onCallBack: " + pos)
                binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        binding.textAddressTxt.text = requireActivity().getAddress(latLong)
                    }
                }
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                val current = LocalDateTime.now().format(formatter)
                binding.textTimeTxt.text = current
                mainThread {
                    dispatchTakePictureIntent()
                    Register1.callBackListener!!.onCallBack(2001)
                }
            } else if (pos == 1002) {
                binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        binding.textAddressTxt.text = requireActivity().getAddress(latLong)
                    }
                }
//                binding.textAddressTxt.text = requireActivity().getAddress(latLong)
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                val current = LocalDateTime.now().format(formatter)
                binding.textTimeTxt.text = current
                mainThread {
                    dispatchTakePictureIntent()
                    Register1.callBackListener!!.onCallBack(2002)
                }
            } else if (pos == 21) {
                btSignIn.setEnabled(false)
                btSignIn.setBackgroundTintList(
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            getResources(), R.color._999999, null
                        )
                    )
                )
            } else if (pos == 2) {
                introViewPager.setCurrentItem(1, false)
                btSignIn.setText(getString(R.string.continues))
            } else if (pos == 4) {
                introViewPager.setCurrentItem(2, false)
                btSignIn.setText(getString(R.string.RegisterNow))
            } else if (pos == 6) {
                val docs = StringBuffer()
                if (viewModel.data.ImageUploadCOVBoolean == true) {
                    docs.append(getString(R.string.COVText) + " ")
                }
                if (viewModel.data.ImageUploadLORBoolean == true) {
                    docs.append(getString(R.string.LORText) + " ")
                }
                if (viewModel.data.UploadSurveyReceiptBoolean == true) {
                    docs.append(getString(R.string.Survery_ReceiptText) + " ")
                }
                if (viewModel.data.UploadChallanBoolean == true) {
                    docs.append(getString(R.string.ChallanText) + " ")
                }
                if (viewModel.data.UploadApprovalLetterBoolean == true) {
                    docs.append(getString(R.string.Approval_LetterText) + " ")
                }

                val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(user_role, USER_TYPE)
                if (viewModel.data.vendor_first_name != null) {
                    requestBody.addFormDataPart(
                        vendor_first_name,
                        viewModel.data.vendor_first_name!!
                    )
                }
                if (viewModel.data.vendor_last_name != null) {
                    requestBody.addFormDataPart(vendor_last_name, viewModel.data.vendor_last_name!!)
                }
                if (viewModel.data.parent_first_name != null) {
                    requestBody.addFormDataPart(
                        parent_first_name,
                        viewModel.data.parent_first_name!!
                    )
                }
                if (viewModel.data.parent_last_name != null) {
                    requestBody.addFormDataPart(parent_last_name, viewModel.data.parent_last_name!!)
                }
                if (viewModel.data.gender != null) {
                    requestBody.addFormDataPart(gender, viewModel.data.gender!!)
                }
                if (viewModel.data.date_of_birth != null) {
                    requestBody.addFormDataPart(date_of_birth, viewModel.data.date_of_birth!!)
                }
                if (viewModel.data.social_category != null) {
                    requestBody.addFormDataPart(social_category, viewModel.data.social_category!!)
                }
                if (viewModel.data.education_qualification != null) {
                    requestBody.addFormDataPart(
                        education_qualification,
                        viewModel.data.education_qualification!!
                    )
                }
                if (viewModel.data.marital_status != null) {
                    requestBody.addFormDataPart(marital_status, viewModel.data.marital_status!!)
                }
                if (viewModel.data.spouse_name != null) {
                    requestBody.addFormDataPart(spouse_name, viewModel.data.spouse_name!!)
                }
                if (viewModel.data.current_state != null) {
                    requestBody.addFormDataPart(residential_state, viewModel.data.current_state!!)
                }
                if (viewModel.data.current_district != null) {
                    requestBody.addFormDataPart(
                        residential_district,
                        viewModel.data.current_district!!
                    )
                }
                if (viewModel.data.municipality_panchayat_current != null) {
                    requestBody.addFormDataPart(
                        residential_municipality_panchayat,
                        viewModel.data.municipality_panchayat_current!!
                    )
                }
                if (viewModel.data.current_pincode != null) {
                    requestBody.addFormDataPart(
                        residential_pincode,
                        viewModel.data.current_pincode!!
                    )
                }
                if (viewModel.data.current_address != null) {
                    requestBody.addFormDataPart(
                        residential_address,
                        viewModel.data.current_address!!
                    )
                }

                if (viewModel.data.type_of_marketplace != null) {
                    requestBody.addFormDataPart(
                        type_of_marketplace,
                        viewModel.data.type_of_marketplace!!
                    )
                }
                if (viewModel.data.marketpalce_others != null) {
                    requestBody.addFormDataPart(
                        marketpalce_others,
                        viewModel.data.marketpalce_others!!
                    )
                }
                if (viewModel.data.type_of_vending != null) {
                    requestBody.addFormDataPart(type_of_vending, viewModel.data.type_of_vending!!)
                }
                if (viewModel.data.vending_others != null) {
                    requestBody.addFormDataPart(vending_others, viewModel.data.vending_others!!)
                }

                if (viewModel.data.total_years_of_business != null) {
                    requestBody.addFormDataPart(
                        total_years_of_business,
                        viewModel.data.total_years_of_business!!
                    )
                }
                if (viewModel.data.open != null) {
                    requestBody.addFormDataPart(vending_time_from, viewModel.data.open!!)
                }
                if (viewModel.data.close != null) {
                    requestBody.addFormDataPart(vending_time_to, viewModel.data.close!!)
                }

                if (viewModel.data.vending_state != null) {
                    requestBody.addFormDataPart(vending_state, viewModel.data.vending_state!!)
                }
                if (viewModel.data.vending_district != null) {
                    requestBody.addFormDataPart(vending_district, viewModel.data.vending_district!!)
                }
                if (viewModel.data.vending_municipality_panchayat != null) {
                    requestBody.addFormDataPart(
                        vending_municipality_panchayat,
                        viewModel.data.vending_municipality_panchayat!!
                    )
                }
                if (viewModel.data.vending_pincode != null) {
                    requestBody.addFormDataPart(vending_pincode, viewModel.data.vending_pincode!!)
                }
                if (viewModel.data.vending_address != null) {
                    requestBody.addFormDataPart(vending_address, viewModel.data.vending_address!!)
                }
                if (viewModel.data.localOrganisation != null) {
                    requestBody.addFormDataPart(
                        local_organisation,
                        viewModel.data.localOrganisation!!
                    )
                }
                if (!docs.toString().isEmpty()) {
                    requestBody.addFormDataPart(vending_documents, docs.toString())
                } else {
                    requestBody.addFormDataPart(vending_documents, "null")
                }

                if (!viewModel.data.schemeName!!.isEmpty()) {
                    requestBody.addFormDataPart(availed_scheme, viewModel.data.schemeName!!)
                } else {
                    requestBody.addFormDataPart(availed_scheme, "null")
                }

                if (viewModel.data.mobile_no != null) {
                    requestBody.addFormDataPart(mobile_no, viewModel.data.mobile_no!!)
                }
                if (viewModel.data.password != null) {
                    requestBody.addFormDataPart(password, viewModel.data.password!!)
                }

                if (viewModel.data.passportSizeImage != null) {
                    requestBody.addFormDataPart(
                        profile_image_name,
                        File(viewModel.data.passportSizeImage!!).name,
                        File(viewModel.data.passportSizeImage!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.identificationImage != null) {
                    requestBody.addFormDataPart(
                        identity_image_name,
                        File(viewModel.data.identificationImage!!).name,
                        File(viewModel.data.identificationImage!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.shopImage != null) {
                    requestBody.addFormDataPart(
                        shop_image,
                        File(viewModel.data.shopImage!!).name,
                        File(viewModel.data.shopImage!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.ImageUploadCOV != null) {
                    requestBody.addFormDataPart(
                        cov_image,
                        File(viewModel.data.ImageUploadCOV!!).name,
                        File(viewModel.data.ImageUploadCOV!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.ImageUploadLOR != null) {
                    requestBody.addFormDataPart(
                        lor_image,
                        File(viewModel.data.ImageUploadLOR!!).name,
                        File(viewModel.data.ImageUploadLOR!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.UploadSurveyReceipt != null) {
                    requestBody.addFormDataPart(
                        survey_receipt_image,
                        File(viewModel.data.UploadSurveyReceipt!!).name,
                        File(viewModel.data.UploadSurveyReceipt!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.UploadChallan != null) {
                    requestBody.addFormDataPart(
                        challan_image,
                        File(viewModel.data.UploadChallan!!).name,
                        File(viewModel.data.UploadChallan!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }

                if (viewModel.data.UploadApprovalLetter != null) {
                    requestBody.addFormDataPart(
                        approval_letter_image,
                        File(viewModel.data.UploadApprovalLetter!!).name,
                        File(viewModel.data.UploadApprovalLetter!!).asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }
                if (networkFailed) {
                    viewModel.registerWithFiles(
                        view = requireView(),
                        requestBody.build(),
                        "" + viewModel.data.vendor_first_name!!
                    )
                } else {
                    requireContext().callNetworkDialog()
                }
            }
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }










    private fun dispatchTakePictureIntent() {
        val bitmap: Bitmap = getBitmapFromView(binding.layoutMainCapture)
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val path = requireActivity().externalCacheDir ?: Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        // val filename = System.currentTimeMillis().toString() + "." + "png" // change png/pdf
        val file = File(path, getImageName())
        try {
            if (!path.exists()) path.mkdirs()
            if (!file.exists()) file.createNewFile()
            val ostream: FileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream)
            ostream.close()

//            val pdfDocument = PdfDocument()
//            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
//            val page = pdfDocument.startPage(pageInfo)
//            page.canvas.drawBitmap(bitmap, 0F, 0F, null)
//            pdfDocument.finishPage(page)
//            val ostream: FileOutputStream = FileOutputStream(file)
//            pdfDocument.writeTo(ostream)
//            ostream.close()
//            pdfDocument.close()

            val data = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file)
            }else{
                val imagePath: File = File(file.absolutePath)
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", imagePath)
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(data, "image/*")  // application/pdf   image/*
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)

            showSnackBar(getString(R.string.successfully_downloaded))
            returnImagePath = file.toString()
        } catch (e: IOException) {
            Log.w("ExternalStorage", "Error writing $file", e)
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