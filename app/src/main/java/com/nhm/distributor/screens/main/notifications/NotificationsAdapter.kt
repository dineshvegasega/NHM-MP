package com.nhm.distributor.screens.main.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.nhm.distributor.BR
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ItemLoadingBinding
import com.nhm.distributor.databinding.ItemNotificationsBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.models.Login
import com.nhm.distributor.models.ItemNotification
import com.nhm.distributor.screens.main.notifications.NotificationsVM.Companion.isNotificationNext
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.networkFailed
import com.nhm.distributor.utils.callNetworkDialog
import com.nhm.distributor.utils.changeDateFormat
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import org.json.JSONObject

class NotificationsAdapter (liveSchemesVM: NotificationsVM) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var viewModel = liveSchemesVM
    private val item: Int = 0
    private val loading: Int = 1

    private var isLoadingAdded: Boolean = false
    private var retryPageLoad: Boolean = false

    private var errorMsg: String? = ""

    private var itemModels: MutableList<ItemNotification> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if(viewType == item){
            val binding: ItemNotificationsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_notifications, parent, false)
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



    class TopMoviesVH(binding: ItemNotificationsBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemNotificationsBinding = binding
        @SuppressLint("SetTextI18n")
        fun bind(obj: Any?, viewModel: NotificationsVM, position: Int) {
            itemRowBinding.setVariable(BR.model, obj)
            itemRowBinding.executePendingBindings()
            val dataClass = obj as ItemNotification
//            dataClass.apply {
//                this.position = position
//            }
            itemRowBinding.apply {
//                textTitle.setText(dataClass.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
//                    Locale.getDefault()) else it.toString() } +" "+ dataClass.sent_at.changeDateFormat("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm a"))

               val type = if(dataClass.type == "scheme"){
                   root.resources.getString(R.string.scheme_type)+" "+ dataClass.sent_at.changeDateFormat("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm a")
                } else if(dataClass.type == "notice"){
                   root.resources.getString(R.string.notice_type)+" "+ dataClass.sent_at.changeDateFormat("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm a")
               } else if(dataClass.type == "training"){
                   root.resources.getString(R.string.training_type)+" "+ dataClass.sent_at.changeDateFormat("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm a")
               } else if(dataClass.type == "Vendor Details" || dataClass.type == "VendorDetails"){
                   root.resources.getString(R.string.vendor_details_type)+" "+ dataClass.sent_at.changeDateFormat("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm a")
               } else if(dataClass.type.contains("information")){
                   root.resources.getString(R.string.information_type)+" "+ dataClass.sent_at.changeDateFormat("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm a")
               } else if(dataClass.title.contains("Feedback")){
                   root.resources.getString(R.string.feedback_type)+" "+ dataClass.sent_at.changeDateFormat("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm a")
               } else if(dataClass.title.contains("Complaint")){
                   root.resources.getString(R.string.complaint_type)+" "+ dataClass.sent_at.changeDateFormat("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm a")
               } else if(dataClass.type == "membership"){
                   root.resources.getString(R.string.subscription_type)+" "+ dataClass.sent_at.changeDateFormat("yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd hh:mm a")
               } else {
                   ""
               }
                textTitle.setText(type)

                val title = if(dataClass.type == "scheme"){
                    root.resources.getString(R.string.scheme_title)
                } else if(dataClass.type == "notice"){
                    root.resources.getString(R.string.notice_title)
                } else if(dataClass.type == "training"){
                    root.resources.getString(R.string.training_title)
                } else if(dataClass.type == "Vendor Details" || dataClass.type == "VendorDetails"){
                    root.resources.getString(R.string.vendor_details_title)
                } else if(dataClass.type == "information"){
                    root.resources.getString(R.string.information_title)
                } else if(dataClass.title.contains("Feedback")){
                    root.resources.getString(R.string.feedback_title)
                } else if(dataClass.title.contains("Complaint")){
                    root.resources.getString(R.string.complaint_title)
                } else if(dataClass.type == "membership"){
                    root.resources.getString(R.string.membership_title)
                } else {
                    ""
                }
                textDesc.text = title



                root.singleClick {
                    isNotificationNext = true
                    readData(LOGIN_DATA) { loginUser ->
                        if (loginUser != null) {
                            val user = Gson().fromJson(loginUser, Login::class.java)
                            if(networkFailed) {
                                val obj: JSONObject = JSONObject().apply {
                                    put("is_read", true)
                                    put("notification_id", ""+dataClass.notification_id)
                                    put("user_id", user.id)
                                }
                                viewModel.updateNotification(obj, position)
                                when(dataClass.type){
                                    "Vendor Details" -> root.findNavController().navigate(R.id.action_notifications_to_profile)
                                    "membership" -> root.findNavController().navigate(R.id.action_notifications_to_subscription)
                                    "scheme" -> {
                                        if(user.status == "approved"){
                                            when(user.subscription_status) {
                                                null -> root.findNavController().navigate(R.id.action_notifications_to_liveSchemes)
                                                "trial" -> root.findNavController().navigate(R.id.action_notifications_to_liveSchemes)
                                                "active" -> root.findNavController().navigate(R.id.action_notifications_to_liveSchemes)
                                                "expired" -> showSnackBar(root.resources.getString(R.string.expired_account_message), type = 2, MainActivity.navHostFragment?.navController)
                                            }
                                        } else {
                                            showSnackBar(root.resources.getString(R.string.registration_processed))
                                        }
                                    }
                                    "notice" -> {
                                        if(user.status == "approved"){
                                            when(user.subscription_status) {
                                                null -> root.findNavController().navigate(R.id.action_notifications_to_liveNotices)
                                                "trial" -> root.findNavController().navigate(R.id.action_notifications_to_liveNotices)
                                                "active" -> root.findNavController().navigate(R.id.action_notifications_to_liveNotices)
                                                "expired" -> showSnackBar(root.resources.getString(R.string.expired_account_message), type = 2, MainActivity.navHostFragment?.navController)
                                            }
                                        } else {
                                            showSnackBar(root.resources.getString(R.string.registration_processed))
                                        }
                                    }
                                    "training" -> {
                                        if(user.status == "approved"){
                                            when(user.subscription_status) {
                                                null -> root.findNavController().navigate(R.id.action_notifications_to_liveTraining)
                                                "trial" -> root.findNavController().navigate(R.id.action_notifications_to_liveTraining)
                                                "active" -> root.findNavController().navigate(R.id.action_notifications_to_liveTraining)
                                                "expired" -> showSnackBar(root.resources.getString(R.string.expired_account_message), type = 2, MainActivity.navHostFragment?.navController)
                                            }
                                        } else {
                                            showSnackBar(root.resources.getString(R.string.registration_processed))
                                        }
                                    }
                                    "information" -> {
                                        if(user.status == "approved"){
                                            when(user.subscription_status) {
                                                null -> root.findNavController().navigate(R.id.action_notifications_to_informationCenter)
                                                "trial" -> root.findNavController().navigate(R.id.action_notifications_to_informationCenter)
                                                "active" -> root.findNavController().navigate(R.id.action_notifications_to_informationCenter)
                                                "expired" -> showSnackBar(root.resources.getString(R.string.expired_account_message), type = 2, MainActivity.navHostFragment?.navController)
                                            }
                                        } else {
                                            showSnackBar(root.resources.getString(R.string.registration_processed))
                                        }
                                    }
                                    "Feedback" -> {
                                        if(user.status == "approved"){
                                            when(user.subscription_status) {
                                                null -> root.findNavController().navigate(R.id.action_notifications_to_historyDetail, Bundle().apply {
                                                    putString("key", ""+dataClass.type_id)
                                                })
                                                "trial" -> root.findNavController().navigate(R.id.action_notifications_to_historyDetail, Bundle().apply {
                                                    putString("key", ""+dataClass.type_id)
                                                })
                                                "active" -> root.findNavController().navigate(R.id.action_notifications_to_historyDetail, Bundle().apply {
                                                    putString("key", ""+dataClass.type_id)
                                                })
                                                "expired" -> showSnackBar(root.resources.getString(R.string.expired_account_message), type = 2, MainActivity.navHostFragment?.navController)
                                            }
                                        } else {
                                            showSnackBar(root.resources.getString(R.string.registration_processed))
                                        }
                                    }
                                }
                            } else {
                                root.context.callNetworkDialog()
                            }
                        }
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
    fun addAllSearch(movies: MutableList<ItemNotification>) {
        itemModels.clear()
        itemModels.addAll(movies)
//        for(movie in movies){
//            add(movie)
//        }
        notifyDataSetChanged()
    }

    fun addAll(movies: MutableList<ItemNotification>) {
        for(movie in movies){
            add(movie)
        }
    }

    fun add(moive: ItemNotification) {
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