package com.nhm.distributor.screens.main.notices.allNotices

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.nhm.distributor.BR
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ItemAllNoticesBinding
import com.nhm.distributor.databinding.ItemLoadingBinding
import com.nhm.distributor.models.ItemLiveNotice
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.changeDateFormat
import com.nhm.distributor.utils.glideImagePortrait
import com.nhm.distributor.utils.singleClick

class AllNoticesAdapter(liveSchemesVM: AllNoticesVM) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var viewModel = liveSchemesVM
    private val item: Int = 0
    private val loading: Int = 1

    private var isLoadingAdded: Boolean = false
    private var retryPageLoad: Boolean = false

    private var errorMsg: String? = ""

    private var itemModels: MutableList<ItemLiveNotice> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if(viewType == item){
            val binding: ItemAllNoticesBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_all_notices, parent, false)
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



    class TopMoviesVH(binding: ItemAllNoticesBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemAllNoticesBinding = binding
        fun bind(obj: Any?, viewModel: AllNoticesVM, position: Int) {
            itemRowBinding.setVariable(BR.model, obj)
            itemRowBinding.executePendingBindings()
            val dataClass = obj as ItemLiveNotice
            itemRowBinding.apply {
                dataClass.notice_image?.url?.glideImagePortrait(itemRowBinding.root.context, ivIcon)
                textTitle.setText(dataClass.name)
                textDesc.setText(dataClass.description)

                dataClass.end_date?.let {
                    textValidDateValue.text = "${dataClass.end_date.changeDateFormat("yyyy-MM-dd", "dd MMM, yyyy")}"
                }
                root.singleClick {
//                    if (dataClass.user_scheme_status == "applied"){
                    if(networkFailed) {
                        viewModel.viewDetail(dataClass, position = position, root, 1)
                    } else {
                        root.context.callNetworkDialog()
                    }
//                    }else{
//                        viewModel.viewDetail(""+dataClass.scheme_id, position = position, root, 2)
//                    }
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
    fun addAllSearch(movies: MutableList<ItemLiveNotice>) {
        itemModels.clear()
        itemModels.addAll(movies)
//        for(movie in movies){
//            add(movie)
//        }
        notifyDataSetChanged()
    }

    fun addAll(movies: MutableList<ItemLiveNotice>) {
        for(movie in movies){
            add(movie)
        }
    }

    fun add(moive: ItemLiveNotice) {
        itemModels.add(moive)
        notifyItemInserted(itemModels.size - 1)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
//        add(ItemLiveScheme())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

//        val position: Int =itemModels.size -1
//        val movie: ItemLiveScheme = itemModels[position]
//
//        if(movie != null){
//            itemModels.removeAt(position)
//            notifyItemRemoved(position)
//        }
    }


}