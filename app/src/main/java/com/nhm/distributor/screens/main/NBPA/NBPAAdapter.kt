package com.nhm.distributor.screens.main.NBPA

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.nhm.distributor.BR
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ItemAllSchemesBinding
//import com.nhm.distributor.databinding.ItemProductBinding
//import com.nhm.distributor.datastore.db.CartModel
//import com.nhm.distributor.models.products.ItemProduct
//import com.nhm.distributor.networking.IMAGE_URL
//import com.nhm.distributor.ui.mainActivity.MainActivity.Companion.db
//import com.nhm.distributor.ui.mainActivity.MainActivityVM.Companion.cartItemLiveData
//import com.nhm.distributor.utils.getPatternFormat
import com.nhm.distributor.databinding.ItemLoadingBinding
import com.nhm.distributor.models.ItemNBPAForm
import com.nhm.distributor.utils.changeDateFormat
import com.nhm.distributor.utils.loadImage
import java.lang.Exception


class NBPAAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var counter = 0

    var itemModels: MutableList<ItemNBPAForm> = ArrayList()


    lateinit var itemRowBinding2: ItemAllSchemesBinding


    private val item: Int = 0
    private val loading: Int = 1

    private var isLoadingAdded: Boolean = false
    private var retryPageLoad: Boolean = false

    private var errorMsg: String? = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if(viewType == item){
            val binding: ItemAllSchemesBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_all_schemes, parent, false)
            itemRowBinding2 = binding
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
            myOrderVH.bind(model, position)
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


    inner class TopMoviesVH(binding: ItemAllSchemesBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemAllSchemesBinding = binding

        @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "ClickableViewAccessibility")
        fun bind(obj: Any?, position: Int) {
            itemRowBinding.setVariable(BR._all, obj)
            itemRowBinding.executePendingBindings()
            val model = obj as ItemNBPAForm

            itemRowBinding.apply {
//                model.foodIdentityImage?.url?.glideImagePortrait(root.context, ivIcon)
                ivIcon.loadImage(type = 1, url = { if (model.foodIdentityImageTwo.size > 0) model.foodIdentityImageTwo[0].url else "" })
                textTitle.setText(model.name)
                try {
                    textDesc.setText(HtmlCompat.fromHtml("<b>"+root.context.resources.getString(R.string.address_)+"</b> "+if (model.address == null) "" else model.address.replace("\n"," "), HtmlCompat.FROM_HTML_MODE_LEGACY))
                }catch ( e: Exception){
                    textDesc.setText(HtmlCompat.fromHtml("<b>"+root.context.resources.getString(R.string.address_)+"</b> ", HtmlCompat.FROM_HTML_MODE_LEGACY))
                }
                try {
                    textMobile.setText(HtmlCompat.fromHtml("<b>"+root.context.resources.getString(R.string.mobile_no_per)+"</b> "+if (model.mobileNumber == null) "" else model.mobileNumber, HtmlCompat.FROM_HTML_MODE_LEGACY))
                }catch (e : Exception){
                    textMobile.setText(HtmlCompat.fromHtml("<b>"+root.context.resources.getString(R.string.mobile_no_per)+"</b> ", HtmlCompat.FROM_HTML_MODE_LEGACY))
                }
                model.created_at?.let {
                    textValidDateValue.text = "${model.created_at.changeDateFormat("yyyy-MM-dd", "dd MMM, yyyy")}"
                }

                itemRowBinding.root.setOnClickListener {
                    itemRowBinding.root.findNavController().navigate(R.id.action_nbpaList_to_nbpaView, Bundle().apply {
                        putParcelable("key", model)
                    })
                }


                btEdit.setOnClickListener {
                    itemRowBinding.root.findNavController().navigate(R.id.action_nbpaList_to_nbpaEdit, Bundle().apply {
                        putParcelable("key", model)
                    })
                }
            }
        }
    }

    inner class LoadingVH(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemLoadingBinding = binding
    }

    fun showRetry(show: Boolean, errorMsg: String) {
        retryPageLoad = show
        notifyItemChanged(itemModels.size - 1)
        this.errorMsg = errorMsg
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAllSearch(movies: MutableList<ItemNBPAForm>) {
        itemModels.clear()
        itemModels.addAll(movies)
//        for(movie in movies){
//            add(movie)
//        }
        notifyDataSetChanged()
    }

    fun addAll(movies: MutableList<ItemNBPAForm>) {
        for(movie in movies){
            add(movie)
        }
    }

    fun add(moive: ItemNBPAForm) {
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


    fun submitData(itemMainArray: ArrayList<ItemNBPAForm>) {
        itemModels = itemMainArray
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updatePosition(position: Int) {
        counter = position
        Log.e("TAG", "updatePosition " + position)
//        itemRowBinding2.apply {
//            if (isHide) {
//                baseButtons.visibility = View.GONE
//                group.visibility = View.VISIBLE
//            } else {
//                baseButtons.visibility = View.VISIBLE
//                group.visibility = View.GONE
//            }
//        }
//        notifyItemChanged(position)

    }



}