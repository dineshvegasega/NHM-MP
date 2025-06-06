package com.nhm.distributor.screens.main.subscription

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.nhm.distributor.BR
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ItemLoadingBinding
import com.nhm.distributor.databinding.ItemSubscriptionHistoryBinding
import com.nhm.distributor.models.ItemTransactionHistory
import com.nhm.distributor.networking.*
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.singleClick
import org.json.JSONObject

class SubscriptionHistoryAdapter (subscriptionVM: SubscriptionVM) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var viewModel = subscriptionVM
    private val item: Int = 0
    private val loading: Int = 1

    private var isLoadingAdded: Boolean = false
    private var retryPageLoad: Boolean = false

    private var errorMsg: String? = ""

    private var itemModels: MutableList<ItemTransactionHistory> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if(viewType == item){
            val binding: ItemSubscriptionHistoryBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_subscription_history, parent, false)
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



    class TopMoviesVH(binding: ItemSubscriptionHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemSubscriptionHistoryBinding = binding
        @SuppressLint("SetTextI18n")
        fun bind(obj: Any?, viewModel: SubscriptionVM, position: Int) {
            itemRowBinding.setVariable(BR.model, obj)
            itemRowBinding.executePendingBindings()
            val dataClass = obj as ItemTransactionHistory
            itemRowBinding.apply {
                layoutMain.backgroundTintList =
                    (if (position % 2 == 0) ContextCompat.getColorStateList(
                        root.context,
                        R.color._f6dbbb
                    ) else ContextCompat.getColorStateList(root.context, R.color.white))
                textSno.setText(""+(position+1))
                textOrderId.setText(""+dataClass.order_id)
                textDate.setText(if (dataClass.date_time.contains(" ")) dataClass.date_time.split(" ")[0] +"\n"+dataClass.date_time.split(" ")[1] else dataClass.date_time.substring(0, dataClass.date_time.lastIndexOf("-")) +"\n"+dataClass.date_time.substring(dataClass.date_time.lastIndexOf("-")+1, dataClass.date_time.length))
                textTransactionId.setText(""+dataClass.transaction_id)

                ivIcon.singleClick {
                    if(MainActivity.networkFailed) {
                        val obj: JSONObject = JSONObject().apply {
                            put(transaction_id, dataClass.transaction_id)
                        }
                        viewModel.subscriptionDetail(obj, root)
                    } else {
                        root.context.callNetworkDialog()
                    }
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
    fun addAllSearch(movies: List<ItemTransactionHistory>) {
        itemModels.clear()
        itemModels.addAll(movies)
//        for(movie in movies){
//            add(movie)
//        }
        notifyDataSetChanged()
    }

    fun addAll(movies: List<ItemTransactionHistory>) {
        for(movie in movies){
            add(movie)
        }
    }

    fun add(moive: ItemTransactionHistory) {
        itemModels.add(moive)
        notifyItemInserted(itemModels.size - 1)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
    }

}