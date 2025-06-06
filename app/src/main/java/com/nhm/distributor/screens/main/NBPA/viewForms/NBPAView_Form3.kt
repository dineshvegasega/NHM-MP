package com.nhm.distributor.screens.main.NBPA.viewForms

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nhm.distributor.databinding.EditForm3Binding
import com.nhm.distributor.screens.main.NBPA.NBPAViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NBPAView_Form3 : Fragment() {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: EditForm3Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = EditForm3Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
        binding.apply {
            Handler(Looper.getMainLooper()).postDelayed({
                var model = viewModel.editDataNew!!.schemeDetail
                recyclerView.setHasFixedSize(true)
                if (model.size != 0){
                    recyclerView.visibility = View.VISIBLE
                    recyclerView.adapter = viewModel.viewForm3Adapter
                    viewModel.viewForm3Adapter.notifyDataSetChanged()
                    viewModel.viewForm3Adapter.submitList(model)
                }else{
                    recyclerView.visibility = View.GONE
                }
            }, 100)






//            ivMenu.singleClick {
//                NBPAEdit.callBackListener!!.onCallBack(1001)
//            }
//            editTextMonth.setText(""+viewModel.editData!!.foodMonth)
//            editTextDate.setText(""+viewModel.editData!!.foodDate)
//            editTextHeight.setText(""+viewModel.editData!!.foodHeight)
//            viewModel.editData!!.foodSignatureImage?.url?.glideImagePortraitForImage(ivSignature.context, ivSignature)
//            viewModel.editData!!.foodItemImage?.url?.glideImagePortraitForImage(ivImagePassportsizeImage.context, ivImagePassportsizeImage)
//            viewModel.editData!!.foodIdentityImage?.url?.glideImagePortraitForImage(ivImageIdentityImage.context, ivImageIdentityImage)
//            ivSignature.singleClick {
//                viewModel.editData!!.foodSignatureImage?.url?.let {
//                    arrayListOf(it).imageZoom(ivSignature, 2)
//                }
//            }
//            ivImagePassportsizeImage.singleClick {
//                viewModel.editData!!.foodItemImage?.url?.let {
//                    arrayListOf(it).imageZoom(ivImagePassportsizeImage, 2)
//                }
//            }
//            ivImageIdentityImage.singleClick {
//                viewModel.editData!!.foodIdentityImage?.url?.let {
//                    arrayListOf(it).imageZoom(ivImageIdentityImage, 2)
//                }
//            }

        }
    }
}