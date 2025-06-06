package com.nhm.distributor.screens.main.NBPA.editForms

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nhm.distributor.databinding.EditForm4Binding
import com.nhm.distributor.screens.main.NBPA.NBPAViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NBPAEdit_Form4 : Fragment() {
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
            var model = viewModel.editDataNew!!.schemeDetail
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = viewModel.viewForm4Adapter
            viewModel.viewForm4Adapter.notifyDataSetChanged()
            viewModel.viewForm4Adapter.submitList(model)
        }
    }
}