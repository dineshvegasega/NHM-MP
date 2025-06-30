package com.nhm.distributor.screens.main.NBPA.addForms

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.gcacace.signaturepad.views.SignaturePad
import com.nhm.distributor.R
import com.nhm.distributor.databinding.Form4Binding
import com.nhm.distributor.screens.interfaces.CallBackListener
import com.nhm.distributor.screens.main.NBPA.NBPAViewModel
import com.nhm.distributor.screens.main.NBPA.addForms.NBPA_Form3.Companion.formFill3
import com.nhm.distributor.utils.callPermissionDialog
import com.nhm.distributor.utils.getImageName
import com.nhm.distributor.utils.mainThread
import com.nhm.distributor.utils.showDropDownDialog
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class NBPA_Form4 : Fragment() , CallBackListener {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: Form4Binding? = null
    private val binding get() = _binding!!

    companion object {
        var callBackListener: CallBackListener? = null
        var tabPosition = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Form4Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
        callBackListener = this
        binding.apply {

            ivMenu.singleClick {
                NBPA.callBackListener!!.onCallBack(1002)
            }

            editTextDietDate.singleClick {
                requireActivity().showDropDownDialog(type = 20){
                    editTextDietDate.setText(title)
                    viewModel.dietChartDate = title
                }
            }

            editTextHomeDate.singleClick {
                requireActivity().showDropDownDialog(type = 20){
                    editTextHomeDate.setText(title)
                    viewModel.homeVisitDate = title
                }
            }



            signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
                override fun onStartSigning() {
                    //Event triggered when the pad is touched
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }

                override fun onSigned() {
                    //Event triggered when the pad is signed
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }

                override fun onClear() {
                    //Event triggered when the pad is cleared
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }
            })

            btComplete.singleClick {
                ivSignature.setImageBitmap(signaturePad.signatureBitmap)
                ivSignature.visibility = View.VISIBLE
                signaturePad.visibility = View.GONE
                btTryAgain.visibility = View.VISIBLE
                btComplete.visibility = View.GONE
                btClear.visibility = View.INVISIBLE

                callMediaPermissions()
            }

            btTryAgain.singleClick {
                signaturePad.clear()
                ivSignature.visibility = View.GONE
                signaturePad.visibility = View.VISIBLE
                btTryAgain.visibility = View.GONE
                btComplete.visibility = View.VISIBLE
                btClear.visibility = View.VISIBLE
                viewModel.homeVisitSignature = ""
            }

            btClear.singleClick {
                signaturePad.clear()
                viewModel.homeVisitSignature = ""
            }



            btSignIn.singleClick {
                Log.e("TAG", "formFill3 "+formFill3)
                if(formFill3){
                    getData(true)
                } else {
                    showSnackBar(getString(R.string.please_fill_required_entries))
                }
            }
        }
    }






    private fun callMediaPermissions() {
        try {
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
        } catch (e : Exception){

        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            try {
                if(!permissions.entries.toString().contains("false")){
                    mainThread {
                        dispatchTakePictureIntent(binding.ivSignature){
                            viewModel.homeVisitSignature = this
                            Log.e("TAG", "viewModel.foodSignature "+viewModel.homeVisitSignature)
                        }
                    }
                } else {
                    requireActivity().callPermissionDialog{
                        someActivityResultLauncher.launch(this)
                    }
                }
            } catch (e : Exception){

            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            callMediaPermissions()
        } catch (e : Exception){

        }
    }



    private fun dispatchTakePictureIntent(imageView: View, callBack: String.() -> Unit) {
        val bitmap: Bitmap = getBitmapFromView(imageView)
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val path = requireActivity().externalCacheDir ?: Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        // val filename = System.currentTimeMillis().toString() + "." + "png" // change png/pdf
        val file = File(path, getImageName())
        try {
            if (!path.exists()) path.mkdirs()
            if (!file.exists()) file.createNewFile()
            val ostream: FileOutputStream = FileOutputStream(file)
            bitmap.compress(CompressFormat.PNG, 10, ostream)
            ostream.close()
            val data = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file)
            }else{
                val imagePath: File = File(file.absolutePath)
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", imagePath)
            }
//            Log.e("TAG", "viewModel.foodSignature "+viewModel.foodSignature)
            callBack(file.toString())

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



    override fun onCallBack(pos: Int) {
        Log.e("TAG", "onCallBackNo4 "+pos)
        getData(false)
    }




    private fun getData(isButton: Boolean) {
        binding.apply {
            viewModel.dietChartEvaluation = editTextDietChartEvaluation.text.toString()
            viewModel.dietChartSuggestion = editTextSuggestion.text.toString()
            viewModel.dietChartServiceProvider = editTextServiceProvider.text.toString()
            viewModel.homeVisitWeight = editTextHeight.text.toString()
            viewModel.homeVisitRemark = editTextComment.text.toString()

            if (isButton) {
//                if (!formFill3) {
//                    showSnackBar(getString(R.string.please_fill_required_entries))
//                } else {
                    NBPA.callBackListener!!.onCallBack(1004)
//                }
            } else {

            }
        }
    }
}