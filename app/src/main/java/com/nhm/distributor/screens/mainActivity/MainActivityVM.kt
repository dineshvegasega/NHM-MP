package com.nhm.distributor.screens.mainActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nhm.distributor.networking.ApiInterface
import com.nhm.distributor.networking.CallHandler
import com.nhm.distributor.networking.Repository
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.nhm.distributor.R
import com.nhm.distributor.databinding.ItemMenuBinding
import com.nhm.distributor.datastore.DataStoreKeys.LOGIN_DATA
import com.nhm.distributor.datastore.DataStoreUtil.readData
import com.nhm.distributor.genericAdapter.GenericAdapter
import com.nhm.distributor.models.BaseResponseDC
import com.nhm.distributor.models.Login
import com.nhm.distributor.models.ItemAds
import com.nhm.distributor.networking.USER_TYPE
import com.nhm.distributor.networking.USER_TYPE_ADMIN
import com.nhm.distributor.screens.main.dashboard.Dashboard
import com.nhm.distributor.screens.main.members.MemberList
import com.nhm.distributor.screens.main.NBPA.NBPAList
import com.nhm.distributor.screens.main.NBPA.checkDetails.CheckDetails
import com.nhm.distributor.screens.main.profiles.Profiles
import com.nhm.distributor.screens.main.settings.Settings
import com.nhm.distributor.screens.mainActivity.MainActivity.Companion.navHostFragment
import com.nhm.distributor.screens.mainActivity.menu.ItemChildMenuModel
import com.nhm.distributor.screens.mainActivity.menu.ItemMenuModel
import com.nhm.distributor.utils.showSnackBar
import com.nhm.distributor.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class MainActivityVM @Inject constructor(private val repository: Repository) : ViewModel() {

    val bannerAdapter by lazy { BannerViewPagerAdapter() }

    companion object {
        @JvmStatic
        var locale: Locale = Locale.getDefault()

        var userIdForGlobal = ""
        var userType = ""

            var isProductLoad = false
            var isFilterLoad = false

        var isProductLoadMember = false
        var isFilterLoadMember = false

    }

    var itemMain: List<ItemMenuModel>? = ArrayList()

    init {
        locale = Locale.getDefault()

//        CoroutineScope(Main).launch {
//            runBlocking (Main){
//                readData(LOGIN_DATA) { loginUser ->
//                    if (loginUser != null) {
//                        val data = Gson().fromJson(loginUser, Login::class.java).user_role
//                        itemMain = JsonHelper(MainActivity.context.get()!!).getMenuData(locale, data)
//                    }
//                }
//            }
//        }


    }

    var selectedPosition = -1
    var selectedColorPosition = 0

    val menuAdapter = object : GenericAdapter<ItemMenuBinding, ItemMenuModel>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemMenuBinding.inflate(inflater, parent, false)

        @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
        override fun onBindHolder(
            binding: ItemMenuBinding,
            dataClass: ItemMenuModel,
            position: Int
        ) {

            binding.apply {
                if (selectedPosition == position) {
                    ivArrow.setImageResource(if (dataClass.isExpanded == true) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)
                    recyclerViewChild.visibility =
                        if (dataClass.isExpanded == true) View.VISIBLE else View.GONE
                } else {
                    ivArrow.setImageResource(R.drawable.ic_arrow_down)
                    recyclerViewChild.visibility = View.GONE
                    dataClass.apply {
                        isExpanded = false
                    }
                }


                if (selectedColorPosition == position) {
                    header.setBackgroundTintList(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                root.context,
                                R.color._EDB678
                            )
                        )
                    )
                } else {
                    header.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.white))
                    )
                }


                title.text = dataClass.title
                if (dataClass.titleChildArray!!.isEmpty()) {
                    ivArrow.visibility = View.GONE
                } else {
                    ivArrow.visibility = View.VISIBLE
                }


                recyclerViewChild.setHasFixedSize(true)
                val headlineAdapter =
                    ChildMenuAdapter(binding.root.context, dataClass.titleChildArray, position)
                recyclerViewChild.adapter = headlineAdapter
                recyclerViewChild.layoutManager = LinearLayoutManager(binding.root.context)


//                ivArrow.singleClick {
//                    selectedPosition = position
//                    dataClass.isExpanded = !dataClass.isExpanded!!
//                    selectedColorPosition = position
//                    notifyDataSetChanged()
//                }


                root.singleClick {
                    selectedColorPosition = position
                    if (dataClass.titleChildArray!!.isEmpty()) {
                        val fragmentInFrame =
                            navHostFragment!!.getChildFragmentManager().getFragments().get(0)
                        readData(LOGIN_DATA) { loginUser ->
                            if (loginUser != null) {
                                val data = Gson().fromJson(loginUser, Login::class.java)

                                if(data.user_role == USER_TYPE){
                                    when (position) {
                                        0 -> {
                                            if (fragmentInFrame !is Dashboard) {
                                                navHostFragment?.navController?.navigate(
                                                    R.id.dashboard
                                                )
                                            }
                                        }

                                        1 -> {
                                            if (fragmentInFrame !is Profiles) {
                                                navHostFragment?.navController?.navigate(
                                                    R.id.profiles
                                                )
                                            }
                                        }

                                        2 -> {
                                            when (data.status) {
                                                "approved" -> {
                                                    if (fragmentInFrame !is NBPAList) {
                                                        isProductLoad = true
                                                        navHostFragment?.navController?.navigate(R.id.nbpaList)
                                                    }
                                                }
                                                "unverified" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                                "pending" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                                "rejected" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                            }
                                        }

                                        3 -> {
                                            when (data.status) {
                                                "approved" -> {
                                                    if (fragmentInFrame !is Settings) {
                                                        navHostFragment?.navController?.navigate(
                                                            R.id.settings
                                                        )
                                                    }
                                                }
                                                "unverified" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                                "pending" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                                "rejected" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                            }
                                        }
                                    }
                                }


                                if(data.user_role == USER_TYPE_ADMIN){
                                    when (position) {
                                        0 -> {
                                            if (fragmentInFrame !is Dashboard) {
                                                navHostFragment?.navController?.navigate(
                                                    R.id.dashboard
                                                )
                                            }
                                        }

                                        1 -> {
                                            if (fragmentInFrame !is Profiles) {
                                                navHostFragment?.navController?.navigate(
                                                    R.id.profiles
                                                )
                                            }
                                        }

                                        2 -> {
                                            when (data.status) {
                                                "approved" -> {
                                                    if (fragmentInFrame !is NBPAList) {
                                                        isProductLoad = true
//                                                        isProductLoadMember = true
                                                        navHostFragment?.navController?.navigate(R.id.nbpaList)
                                                    }
                                                }
                                                "unverified" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                                "pending" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                                "rejected" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                            }
                                        }

                                        3 -> {
                                            when (data.status) {
                                                "approved" -> {
                                                    if (fragmentInFrame !is MemberList) {
//                                                        isProductLoad = true
                                                        isProductLoadMember = true
                                                        navHostFragment?.navController?.navigate(
                                                            R.id.memberList
                                                        )
                                                    }
                                                }
                                                "unverified" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                                "pending" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                                "rejected" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                            }
                                        }

                                       4 -> {
                                            when (data.status) {
                                                "approved" -> {
                                                    if (fragmentInFrame !is Settings) {
                                                        navHostFragment?.navController?.navigate(
                                                            R.id.settings
                                                        )
                                                    }
                                                }
                                                "unverified" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                                "pending" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                                "rejected" -> {
                                                    showSnackBar(root.resources.getString(R.string.registration_processed))
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                        MainActivity.binding.drawerLayout.close()
                    }

                    selectedPosition = position
                    dataClass.isExpanded = !dataClass.isExpanded!!
                    selectedColorPosition = position
                    notifyDataSetChanged()
                }


            }
        }
    }


    class ChildMenuAdapter(context: Context, data: List<ItemChildMenuModel>?, mainPosition: Int) :
        RecyclerView.Adapter<ChildMenuAdapter.ChildViewHolder>() {
        var mainContext: Context = context
        private var items: List<ItemChildMenuModel>? = data
        private var inflater: LayoutInflater = LayoutInflater.from(context)
        private var parentPosition: Int = mainPosition

        var selectedChildColorPosition = -1

        override
        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
            val view = inflater.inflate(R.layout.item_child_menu, parent, false)
            return ChildViewHolder(view)
        }

        @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
        override
        fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
            val item = items?.get(position)
            holder.tvTitle.text = item?.title

            if (selectedChildColorPosition == position) {
                holder.child.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(mainContext, R.color._f6dbbb))
                )
            } else {
                holder.child.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(mainContext, R.color.white))
                )
            }

            holder.itemView.singleClick {
                selectedChildColorPosition = position
                notifyDataSetChanged()

                val fragmentInFrame =
                    navHostFragment!!.getChildFragmentManager().getFragments().get(0)
                readData(LOGIN_DATA) { loginUser ->
                    if (loginUser != null) {
                        val data = Gson().fromJson(loginUser, Login::class.java)
//                        when (data.status) {
//                            "approved" -> {
                                when (parentPosition) {
                                    2 -> when (position) {
                                        0 -> {
                                            when (data.status) {
                                                "approved" -> {
                                                    if (fragmentInFrame !is CheckDetails) {
                                                        navHostFragment?.navController?.navigate(R.id.checkDetails)
                                                    }
                                                }
                                                "unverified" -> {
                                                    showSnackBar(holder.itemView.resources.getString(R.string.registration_processed))
                                                }
                                                "pending" -> {
                                                    showSnackBar(holder.itemView.resources.getString(R.string.registration_processed))
                                                }
                                                "rejected" -> {
                                                    showSnackBar(holder.itemView.resources.getString(R.string.registration_processed))
                                                }
                                            }
                                        }

                                        1 -> {
                                            when (data.status) {
                                                "approved" -> {
                                                    if (fragmentInFrame !is NBPAList) {
                                                        isProductLoad = true
//                                                        isProductLoadMember = true
                                                        navHostFragment?.navController?.navigate(R.id.nbpaList)
                                                    }
                                                }
                                                "unverified" -> {
                                                    showSnackBar(holder.itemView.resources.getString(R.string.registration_processed))
                                                }
                                                "pending" -> {
                                                    showSnackBar(holder.itemView.resources.getString(R.string.registration_processed))
                                                }
                                                "rejected" -> {
                                                    showSnackBar(holder.itemView.resources.getString(R.string.registration_processed))
                                                }
                                            }
                                        }
                                    }

//                                    5 -> when (position) {
//                                        0 -> {
//                                            if (fragmentInFrame !is LiveNotices) {
//                                                when(data.subscription_status) {
//                                                    null -> navHostFragment?.navController?.navigate(R.id.liveNotices)
//                                                    "trial" -> navHostFragment?.navController?.navigate(R.id.liveNotices)
//                                                    "active" -> navHostFragment?.navController?.navigate(R.id.liveNotices)
//                                                    "expired" -> showSnackBar(mainContext.resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
//                                                }
//                                            }
//                                        }
//
//                                        1 -> {
//                                            if (fragmentInFrame !is AllNotices) {
//                                                when(data.subscription_status) {
//                                                    null -> navHostFragment?.navController?.navigate(R.id.allNotices)
//                                                    "trial" -> navHostFragment?.navController?.navigate(R.id.allNotices)
//                                                    "active" -> navHostFragment?.navController?.navigate(R.id.allNotices)
//                                                    "expired" -> showSnackBar(mainContext.resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    6 -> when (position) {
//                                        0 -> {
//                                            if (fragmentInFrame !is LiveTraining) {
//                                                when(data.subscription_status) {
//                                                    null -> navHostFragment?.navController?.navigate(R.id.liveTraining)
//                                                    "trial" -> navHostFragment?.navController?.navigate(R.id.liveTraining)
//                                                    "active" -> navHostFragment?.navController?.navigate(R.id.liveTraining)
//                                                    "expired" -> showSnackBar(mainContext.resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
//                                                }
//                                            }
//                                        }
//
//                                        1 -> {
//                                            if (fragmentInFrame !is AllTraining) {
//                                                when(data.subscription_status) {
//                                                    null -> navHostFragment?.navController?.navigate(R.id.allTraining)
//                                                    "trial" -> navHostFragment?.navController?.navigate(R.id.allTraining)
//                                                    "active" -> navHostFragment?.navController?.navigate(R.id.allTraining)
//                                                    "expired" -> showSnackBar(mainContext.resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    7 -> when (position) {
//                                        0 -> {
//                                            if (fragmentInFrame !is CreateNew) {
//                                                when(data.subscription_status) {
//                                                    null -> navHostFragment?.navController?.navigate(R.id.createNew)
//                                                    "trial" -> navHostFragment?.navController?.navigate(R.id.createNew)
//                                                    "active" -> navHostFragment?.navController?.navigate(R.id.createNew)
//                                                    "expired" -> showSnackBar(mainContext.resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
//                                                }
//                                            }
//                                        }
//
//                                        1 -> {
//                                            if (fragmentInFrame !is History) {
//                                                when(data.subscription_status) {
//                                                    null -> navHostFragment?.navController?.navigate(R.id.history)
//                                                    "trial" -> navHostFragment?.navController?.navigate(R.id.history)
//                                                    "active" -> navHostFragment?.navController?.navigate(R.id.history)
//                                                    "expired" -> showSnackBar(mainContext.resources.getString(R.string.expired_account_message), type = 2, navHostFragment?.navController)
//                                                }
//                                            }
//                                        }
//                                    }
                                }
//                            }
//
//                            "unverified" -> {
//                                showSnackBar(mainContext.resources.getString(R.string.registration_processed))
//                            }
//
//                            "pending" -> {
//                                showSnackBar(mainContext.resources.getString(R.string.registration_processed))
//                            }
//
//                            "rejected" -> {
//                                showSnackBar(mainContext.resources.getString(R.string.registration_processed))
//                            }
//                        }
                    }
                }
                MainActivity.binding.drawerLayout.close()
            }
        }

        override
        fun getItemCount(): Int {
            return items?.size ?: 0
        }

        class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tvTitle: AppCompatTextView = itemView.findViewById(R.id.titleChild)
            var child: ConstraintLayout = itemView.findViewById(R.id.child)
        }
    }


    private var itemAdsResult = MutableLiveData<ArrayList<ItemAds>>()
    val itemAds: LiveData<ArrayList<ItemAds>> get() = itemAdsResult
    fun adsList() = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemAds>>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.adsList()

                override fun success(response: Response<BaseResponseDC<List<ItemAds>>>) {
                    if (response.isSuccessful) {
                        val adsList: ArrayList<ItemAds> = ArrayList()
                        val ads = response.body()?.data as ArrayList<ItemAds>
                        ads.map {
                            when (it.ad_sr_no) {
                                in 3..4 -> {
                                    adsList.add(it)
                                }

                                else -> {}
                            }
                        }
                        itemAdsResult.value = adsList

                    }
                }

                override fun error(message: String) {
//                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    var itemDeleteResult = MutableLiveData<Boolean>(false)
    fun deleteAccount(hashMap: RequestBody) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.saveSettings(hashMap)

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        itemDeleteResult.value = true
                        // itemAdsResult.value = response.body()?.data as ArrayList<ItemAds>
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    var itemLogoutResult = MutableLiveData<Boolean>(false)
    fun logoutAccount(hashMap: RequestBody) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.logout(hashMap)

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        itemLogoutResult.value = true
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


}