package com.nhm.distributor.screens.main.complaintsFeedback.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nhm.distributor.BR
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ItemHistoryBinding
import com.nhm.distributor.databinding.ItemLoadingBinding
import com.nhm.distributor.models.ItemHistory
import com.nhm.distributor.utils.changeDateFormat
import com.nhm.distributor.utils.glideImagePortrait
import com.nhm.distributor.utils.singleClick


class HistoryAdapter(liveSchemesVM: HistoryVM) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var viewModel = liveSchemesVM
    private val item: Int = 0
    private val loading: Int = 1

    private var isLoadingAdded: Boolean = false
    private var retryPageLoad: Boolean = false

    private var errorMsg: String? = ""

    private var itemModels: MutableList<ItemHistory> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if(viewType == item){
            val binding: ItemHistoryBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_history, parent, false)
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
//            myOrderVH.itemRowBinding.movieProgress.visibility = View.VISIBLE
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



    class TopMoviesVH(binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemHistoryBinding = binding
        fun bind(obj: Any?, viewModel: HistoryVM, position: Int) {
            itemRowBinding.setVariable(BR.model, obj)
            itemRowBinding.executePendingBindings()
            val dataClass = obj as ItemHistory
            itemRowBinding.apply {
                dataClass.media?.url?.glideImagePortrait(itemRowBinding.root.context, ivIcon)
               val complaintfeedback = if (dataClass.type == "complaint"){
                    root.context.getString(R.string.complaint)
                } else {
                    root.context.getString(R.string.feedback)
                }
                textTitle.text = complaintfeedback

                textDesc.setText(dataClass.subject)
                textTrackValue.setText(""+dataClass.feedback_id)
//                textStatusValueTxt.setText(dataClass.status.titlecaseFirstCharIfItIsLowercase())

                textStatusValueTxt.setText(
                    if (dataClass.status == "in-progress") root.context.getString(R.string.in_progress)
                    else if (dataClass.status == "Pending" || dataClass.status == "pending") root.context.getString(R.string.pending)
                    else if (dataClass.status == "resolved") root.context.getString(R.string.resolved)
                    else if (dataClass.status == "re-open") root.context.getString(R.string.re_open)
                    else if (dataClass.status == "Closed") root.context.getString(R.string.closed)
                    else root.context.getString(R.string.pending))

                textStatusValueTxt.setTextColor(
                if (dataClass.status == "in-progress") ContextCompat.getColor(root.context, R.color._E79D46)
                else if (dataClass.status == "Pending" || dataClass.status == "pending") ContextCompat.getColor(root.context, R.color.black)
                else if (dataClass.status == "resolved") ContextCompat.getColor(root.context, R.color._138808)
                else if (dataClass.status == "re-open") ContextCompat.getColor(root.context, R.color._ED2525)
                else if (dataClass.status == "Closed") ContextCompat.getColor(root.context, R.color.black)
                else ContextCompat.getColor(root.context, R.color._ffffffff))

                dataClass.date?.let {
                    textValidDateValue.text = "${dataClass.date.changeDateFormat("dd-MM-yyyy", "dd MMM, yyyy")}"
                }
                root.singleClick {
                    view.findNavController().navigate(R.id.action_history_to_historyDetail, Bundle().apply {
                        putString("key", ""+dataClass.feedback_id)
                    })
                }

            }
        }
    }

    class LoadingVH(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemLoadingBinding = binding
    }

    fun showRetry(show: Boolean, errorMsg: String) {
        retryPageLoad = show
        notifyItemChanged(itemModels.size - 1)
        this.errorMsg = errorMsg
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAllSearch(movies: MutableList<ItemHistory>) {
        itemModels.clear()
        itemModels.addAll(movies)
//        for(movie in movies){
//            add(movie)
//        }
        notifyDataSetChanged()
    }

//    fun addAll(movies: MutableList<ItemHistory>) {
//        for(movie in movies){
//            add(movie)
//        }
//    }

//    fun add(moive: ItemHistory) {
//        itemModels.add(moive)
//        notifyItemInserted(itemModels.size - 1)
//    }

    fun addLoadingFooter() {
        isLoadingAdded = true
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
    }

}