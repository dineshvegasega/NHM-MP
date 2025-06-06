package com.nhm.distributor.screens.main.schemes.liveSchemes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ItemLiveSchemesBinding
import com.nhm.distributor.databinding.ItemLoadingBinding
import com.nhm.distributor.models.ItemLiveScheme
import com.nhm.distributor.BR


class LiveSchemesAdapter(liveSchemesVM: LiveSchemesVM) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var viewModel = liveSchemesVM
    private val item: Int = 0
    private val loading: Int = 1

    private var isLoadingAdded: Boolean = false
    private var retryPageLoad: Boolean = false

    private var itemModels: MutableList<ItemLiveScheme> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if(viewType == item){
            val binding: ItemLiveSchemesBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_live_schemes, parent, false)
            TopMoviesVH(binding)
        }else{
            val binding: ItemLoadingBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_loading, parent, false)
            LoadingVH(binding)
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = itemModels[position]
        if(getItemViewType(position) == item){
            val myOrderVH: TopMoviesVH = holder as TopMoviesVH
            myOrderVH.bind(model, viewModel, position)
        }else{
            val loadingVH: LoadingVH = holder as LoadingVH
            if (retryPageLoad) {
                loadingVH.itemRowBinding.loadmoreProgress.visibility = View.GONE
            } else {
                loadingVH.itemRowBinding.loadmoreProgress.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return if (itemModels.size > 0) itemModels.size else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0){
            item
        }else {
            if (position == itemModels.size - 1 && isLoadingAdded) {
                loading
            } else {
                item
            }
        }
    }


    class TopMoviesVH(binding: ItemLiveSchemesBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemLiveSchemesBinding = binding
        @SuppressLint("ResourceAsColor")
        fun bind(obj: Any?, viewModel: LiveSchemesVM, position: Int) {
            itemRowBinding.setVariable(BR.model, obj)
            itemRowBinding.executePendingBindings()
            var dataClass = obj as ItemLiveScheme
            itemRowBinding.apply {
//                dataClass.scheme_image?.url?.glideImage(itemRowBinding.root.context, ivMap)
//                textTitle.setText(dataClass.name)
//                textDesc.setText(dataClass.description)
//
//                textHeaderTxt4.setText(if (dataClass.status == "Active") root.context.resources.getString(R.string.live) else root.context.resources.getString(R.string.not_live))
//                textHeaderTxt4.backgroundTintList = (if(dataClass.status == "Active") ContextCompat.getColorStateList(root.context,R.color._138808) else ContextCompat.getColorStateList(root.context,R.color._F02A2A))
//
//                textHeaderStatusTxt4.setText(if (dataClass.user_scheme_status == "applied") root.context.resources.getString(R.string.applied) else root.context.resources.getString(R.string.not_applied))
//                textHeaderStatusTxt4.visibility = if (dataClass.user_scheme_status == "applied") View.VISIBLE else View.GONE
//
//
//                root.singleClick {
//                    if(networkFailed) {
//                        if (dataClass.user_scheme_status == "applied"){
//                            viewModel.viewDetail(dataClass, position = position, root, 1)
//                        }else{
//                            viewModel.viewDetail(dataClass, position = position, root, 2)
//                        }
//                    } else {
//                        root.context.callNetworkDialog()
//                    }
//                }
            }
        }


    }

    class LoadingVH(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemLoadingBinding = binding
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAllSearch(movies: MutableList<ItemLiveScheme>) {
        itemModels.clear()
        itemModels.addAll(movies)
        notifyDataSetChanged()
    }

//    fun addAll(movies: MutableList<ItemLiveScheme>) {
//        for(movie in movies){
//            add(movie)
//        }
//    }
//
//    fun add(moive: ItemLiveScheme) {
//        itemModels.add(moive)
//        notifyItemInserted(itemModels.size - 1)
//    }

    fun addLoadingFooter() {
        isLoadingAdded = true
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
//        val position: Int = itemModels.size -1
//        val movie: ItemLiveScheme = itemModels[position]
//        if(movie != null){
//            itemModels.removeAt(position)
//            notifyItemRemoved(position)
//        }
    }
}