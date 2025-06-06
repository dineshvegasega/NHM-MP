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
import com.nhm.distributor.databinding.EditForm4Binding
import com.nhm.distributor.screens.main.NBPA.NBPAViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NBPAView_Form4 : Fragment() {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: EditForm4Binding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = EditForm4Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
        binding.apply {
            Handler(Looper.getMainLooper()).postDelayed({
                var model = viewModel.editDataNew!!.schemeDetail
                recyclerView.setHasFixedSize(true)
                if (model.size != 0){
                    recyclerView.visibility = View.VISIBLE
                    recyclerView.adapter = viewModel.viewForm4Adapter
                    viewModel.viewForm4Adapter.notifyDataSetChanged()
                    viewModel.viewForm4Adapter.submitList(model)
                }else{
                    recyclerView.visibility = View.GONE
                }
            }, 100)
        }
    }
}