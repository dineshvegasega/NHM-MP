package com.nhm.distributor.screens.main.complaintsFeedback.historyDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ItemChatLeftBinding
import com.nhm.distributor.databinding.ItemChatRightBinding
import com.nhm.distributor.databinding.ItemLoadingBinding
import com.nhm.distributor.models.DataX
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.locale
import com.nhm.distributor.utils.changeDateFormat
import com.nhm.distributor.utils.glideImage
import com.nhm.distributor.utils.imageZoom
import com.nhm.distributor.utils.singleClick


class HistoryDetailAdapter () :
    ListAdapter<DataX, RecyclerView.ViewHolder>(
        DELIVERY_ITEM_COMPARATOR
    ) {
    private val LAYOUT_ONE=0
    private val LAYOUT_TWO=1
    private val LAYOUT_THREE=2

    private var isLoadingAdded: Boolean = false

//    var locale: Locale = Locale.getDefault()

    inner class LeftMessagesViewHolder(private val binding: ItemChatLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: DataX) {
            binding.apply {
                if (model.reply != "null" && model.reply != null){
                    tvMessage.text = model.reply
                    tvMessage.visibility = View.VISIBLE
                } else {
                    tvMessage.visibility = View.GONE
                }

                if (model.media?.name != "null" && model.media?.name != null){
                    model.media?.url?.glideImage(binding.root.context, ivMap)

                    ivMap.visibility = View.VISIBLE

                    ivMap.singleClick {
                        model.media?.let {
                            arrayListOf(it.url).imageZoom(ivMap, 1)
                        }
                    }
                } else {
                    ivMap.visibility = View.GONE
                }

                model.reply_date?.let {
                    tvTime.text = "${model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "hh:mm a")?.uppercase()}"
                }

                model.reply_date?.let {
                    ivDate.text = "${model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy")?.uppercase()}"
                }



                if(model.status == "resolved") {
                    group.visibility = View.GONE
                    ivOpenClose.visibility = View.VISIBLE
                    ivDate.visibility = View.GONE
                    if (root.context.getString(R.string.hindiVal) == "" + locale ) {
                        ivOpenClose.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin_close_hi, "<b>"+binding.root.resources.getString(R.string.admin_txt)+"</b>" , "<b>"+binding.root.resources.getString(R.string.close_info)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    } else {
                        ivOpenClose.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin, "<b>"+binding.root.resources.getString(R.string.close_info)+"</b>" , "<b>"+binding.root.resources.getString(R.string.admin_txt)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }
                } else if(model.status == "re-open") {
                    group.visibility = View.GONE
                    ivOpenClose.visibility = View.VISIBLE
                    ivDate.visibility = View.GONE
                    if (root.context.getString(R.string.hindiVal) == "" + locale ) {
                        ivOpenClose.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin_open_hi, "<b>"+binding.root.resources.getString(R.string.admin_txt)+"</b>" , "<b>"+binding.root.resources.getString(R.string.re_open_info)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    } else {
                        ivOpenClose.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin, "<b>"+binding.root.resources.getString(R.string.re_open_info)+"</b>" , "<b>"+binding.root.resources.getString(R.string.admin_txt)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }
                }else if(model.status == "closed") {
                    group.visibility = View.GONE
                    ivOpenClose.visibility = View.VISIBLE
                    ivDate.visibility = View.GONE
                    if (root.context.getString(R.string.hindiVal) == "" + locale ) {
                        ivOpenClose.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin_close_hi, "<b>"+binding.root.resources.getString(R.string.admin_txt)+"</b>" , "<b>"+binding.root.resources.getString(R.string.close_info)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    } else {
                        ivOpenClose.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin, "<b>"+binding.root.resources.getString(R.string.close_info)+"</b>" , "<b>"+binding.root.resources.getString(R.string.admin_txt)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }
                }else if(model.status == "in-progress") {
                    group.visibility = View.VISIBLE
                    ivOpenClose.visibility = View.GONE
                    ivDate.visibility = if (model.dateShow == true) View.VISIBLE else View.GONE
                }else if(model.status == "Pending" || model.status == "pending") {
                    group.visibility = View.VISIBLE
                    ivOpenClose.visibility = View.GONE
                    ivDate.visibility = if (model.dateShow == true) View.VISIBLE else View.GONE
                }

            }
        }
    }

    inner class RightMessagesViewHolder(private val binding: ItemChatRightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: DataX) {
            binding.apply {
                if (model.reply != "null" && model.reply != null){
                    tvMessage.text = model.reply
                    tvMessage.visibility = View.VISIBLE
                } else {
                    tvMessage.visibility = View.GONE
                }

                if (model.media?.name != "null" && model.media?.name != null){
                    model.media?.url?.glideImage(binding.root.context, ivMap)
                    ivMap.visibility = View.VISIBLE

                    ivMap.singleClick {
                        model.media?.let {
                            arrayListOf(it.url).imageZoom(ivMap, 1)
                        }
                    }
                } else {
                    ivMap.visibility = View.GONE
                }

                model.reply_date?.let {
                    tvTime.text = "${model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "hh:mm a")?.uppercase()}"
                }

                model.reply_date?.let {
                    ivDate.text = "${model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM yyyy")?.uppercase()}"
                }


                if(model.status == "resolved") {
                    group.visibility = View.GONE
                    ivOpenClose.visibility = View.VISIBLE
                    ivDate.visibility = View.GONE
                    if (root.context.getString(R.string.hindiVal) == "" + locale ) {
                        ivOpenClose.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin_close_hi, "<b>"+binding.root.resources.getString(R.string.member_txt)+"</b>" , "<b>"+binding.root.resources.getString(R.string.close_info)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    } else {
                        ivOpenClose.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin, "<b>"+binding.root.resources.getString(R.string.close_info)+"</b>" , "<b>"+binding.root.resources.getString(R.string.member_txt)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }
                } else if(model.status == "re-open") {
                    group.visibility = View.GONE
                    ivOpenClose.visibility = View.VISIBLE
                    ivDate.visibility = View.GONE
                    if (root.context.getString(R.string.hindiVal) == "" + locale ) {
                        ivOpenClose.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin_open_hi, "<b>"+binding.root.resources.getString(R.string.member_txt)+"</b>" , "<b>"+binding.root.resources.getString(R.string.re_open_info)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    } else {
                        ivOpenClose.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin, "<b>"+binding.root.resources.getString(R.string.re_open_info)+"</b>" , "<b>"+binding.root.resources.getString(R.string.member_txt)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }
                }else if(model.status == "closed") {
                    group.visibility = View.GONE
                    ivOpenClose.visibility = View.VISIBLE
                    ivDate.visibility = View.GONE
                    if (root.context.getString(R.string.hindiVal) == "" + locale ) {
                        ivOpenClose.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin_close_hi, "<b>"+binding.root.resources.getString(R.string.member_txt)+"</b>" , "<b>"+binding.root.resources.getString(R.string.close_info)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    } else {
                        ivOpenClose.text = HtmlCompat.fromHtml(binding.root.resources.getString(R.string.conversation_marked_admin, "<b>"+binding.root.resources.getString(R.string.close_info)+"</b>" , "<b>"+binding.root.resources.getString(R.string.member_txt)+"</b>")+" "+model.reply_date.changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd MMM, yyyy hh:mm a"), HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }
                }else if(model.status == "in-progress") {
                    group.visibility = View.VISIBLE
                    ivOpenClose.visibility = View.GONE
                    ivDate.visibility = if (model.dateShow == true) View.VISIBLE else View.GONE
                }else if(model.status == "Pending" || model.status == "pending") {
                    group.visibility = View.VISIBLE
                    ivOpenClose.visibility = View.GONE
                    ivDate.visibility = if (model.dateShow == true) View.VISIBLE else View.GONE
                }
            }
        }
    }

    inner class LoaderViewHolder(private val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: DataX) {

        }
    }


    override fun getItemViewType(position: Int): Int {
        val list = currentList
        if(position == 0){
            if(getItem(position).user_type == "admin"){
                return LAYOUT_ONE
            }else if(getItem(position).user_type == "member"){
                return LAYOUT_TWO
            }
        } else {
            if (position == list.size - 1 && isLoadingAdded) {
                return LAYOUT_THREE
            } else {
                if(getItem(position).user_type == "admin"){
                    return LAYOUT_ONE
                }else if(getItem(position).user_type == "member"){
                    return LAYOUT_TWO
                }
            }
        }
        return -1
    }



    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val binding=
                    ItemChatLeftBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return LeftMessagesViewHolder(binding)
            }
            1 -> {
                val binding=
                    ItemChatRightBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return RightMessagesViewHolder(binding)
            }
            else -> {
                val binding=
                    ItemLoadingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                return LoaderViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.getItemViewType() == LAYOUT_ONE) {
            val currentItem=getItem(position)
            (holder as LeftMessagesViewHolder).bind(currentItem)
        }

        if(holder.getItemViewType() == LAYOUT_TWO) {
            val currentItem=getItem(position)
            (holder as RightMessagesViewHolder).bind(currentItem)
        }

        if(holder.getItemViewType() == LAYOUT_THREE) {
            val currentItem=getItem(position)
            (holder as LoaderViewHolder).bind(currentItem)
        }
    }

    companion object {
        private val DELIVERY_ITEM_COMPARATOR = object : DiffUtil.ItemCallback<DataX>() {
            override fun areItemsTheSame(
                oldItem: DataX,
                newItem: DataX
            ): Boolean {
                return false
            }

            override fun areContentsTheSame(
                oldItem: DataX,
                newItem: DataX
            ): Boolean {
                return false
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(model: DataX)
    }


    fun addLoadingFooter() {
        isLoadingAdded = true
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
    }
}