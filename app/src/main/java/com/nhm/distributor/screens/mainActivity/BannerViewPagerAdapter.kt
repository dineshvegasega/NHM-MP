package com.nhm.distributor.screens.mainActivity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.nhm.distributor.R
import com.nhm.distributor.models.ItemAds
import com.nhm.distributor.utils.loadImage
import com.nhm.distributor.utils.singleClick

class BannerViewPagerAdapter() : PagerAdapter() {

    private var inflater: LayoutInflater? = null
    var itemMain : ArrayList<ItemAds> ?= ArrayList()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return itemMain!!.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = MainActivity.activity.get()?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater!!.inflate(R.layout.item_banner, null)
        val bannerItem = view.findViewById<AppCompatImageView>(R.id.bannerItem)
//        bannerItem.setImageResource(images[0])
        bannerItem.loadImage(type = 2, url = { itemMain?.get(position)!!.ad_media })
        view.singleClick {
            Handler(Looper.getMainLooper()).post(Thread {
                MainActivity.activity.get()?.runOnUiThread {
                    itemMain?.get(position)!!.ad_link?.let {
                            val webIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(itemMain?.get(position)!!.ad_link)
                            )
                            try {
                                MainActivity.activity.get()?.startActivity(webIntent)
                            } catch (ex: ActivityNotFoundException) {
                            }
                    }
                }
            })
        }
        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

    fun submitData(itemAds: ArrayList<ItemAds>) {
        itemMain!!.clear()
        itemMain!!.addAll(itemAds)
    }
}