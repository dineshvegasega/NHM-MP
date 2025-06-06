package com.nhm.distributor.screens.onboarding.start

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nhm.distributor.networking.Repository
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.R
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StartVM @Inject constructor(private val repository: Repository): ViewModel() {

    var itemMain : ArrayList<Item> ?= ArrayList()
    val locale: Locale = Locale.getDefault()

    var appLanguage = MutableLiveData<String>("")

    init {
        itemMain?.add(Item(MainActivity.context.get()!!.getString(R.string.english), MainActivity.context.get()!!.getString(R.string.englishVal),false))
//        itemMain?.add(Item(MainActivity.context.get()!!.getString(R.string.bengali), MainActivity.context.get()!!.getString(R.string.bengaliVal),false))
//        itemMain?.add(Item(MainActivity.context.get()!!.getString(R.string.gujarati), MainActivity.context.get()!!.getString(R.string.gujaratiVal),false))
        itemMain?.add(Item(MainActivity.context.get()!!.getString(R.string.hindi), MainActivity.context.get()!!.getString(R.string.hindiVal),false))
//        itemMain?.add(Item(MainActivity.context.get()!!.getString(R.string.kannada), MainActivity.context.get()!!.getString(R.string.kannadaVal),false))
//        itemMain?.add(Item(MainActivity.context.get()!!.getString(R.string.malayalam), MainActivity.context.get()!!.getString(R.string.malayalamVal),false))
//        itemMain?.add(Item(MainActivity.context.get()!!.getString(R.string.marathi), MainActivity.context.get()!!.getString(R.string.marathiVal),false))
//        itemMain?.add(Item(MainActivity.context.get()!!.getString(R.string.punjabi), MainActivity.context.get()!!.getString(R.string.punjabiVal),false))
//        itemMain?.add(Item(MainActivity.context.get()!!.getString(R.string.tamil), MainActivity.context.get()!!.getString(R.string.tamilVal),false))
//        itemMain?.add(Item(MainActivity.context.get()!!.getString(R.string.telugu), MainActivity.context.get()!!.getString(R.string.teluguVal),false))
//        itemMain?.add(Item(MainActivity.context.get()!!.getString(R.string.assamese), MainActivity.context.get()!!.getString(R.string.assameseVal),false))
//        itemMain?.add(Item(MainActivity.context.get()!!.getString(R.string.urdu), MainActivity.context.get()!!.getString(R.string.urduVal),false))


        for (item in itemMain!!.iterator()) {
            if(item.locale == ""+locale){
                item.apply {
                    item.isSelected = true
                }
                appLanguage.value = item.name
            }
        }


    }

    data class Item (
        var name: String = "",
        var locale: String = "",
        var isSelected: Boolean? = false
    )


}