package com.nhm.distributor.screens.mainActivity.menu

import android.annotation.SuppressLint
import android.content.Context
import org.json.JSONObject
import com.google.gson.Gson
import com.nhm.distributor.R
import com.nhm.distributor.networking.USER_TYPE
import com.nhm.distributor.networking.USER_TYPE_ADMIN
import com.nhm.distributor.screens.mainActivity.MainActivity
import java.util.Locale


open class JsonHelper(private var context: Context) {
    private var newspaperList: MutableList<ItemMenuModel>? = null

    @SuppressLint("SuspiciousIndentation")
    open fun getMenuData(locale: Locale, data: String): List<ItemMenuModel>? {

        if (newspaperList == null)
            newspaperList = ArrayList()
        try {
            var jsonObject : JSONObject = JSONObject()
            if (MainActivity.context.get()!!.getString(R.string.englishVal) == ""){
                if(data == USER_TYPE){
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_english.json")!!)
                } else if(data == USER_TYPE_ADMIN){
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_english_controller.json")!!)
                } else {
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_english.json")!!)
                }
            } else if (MainActivity.context.get()!!.getString(R.string.englishVal) == ""+locale){
                if(data == USER_TYPE){
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_english.json")!!)
                } else if(data == USER_TYPE_ADMIN){
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_english_controller.json")!!)
                } else {
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_english.json")!!)
                }
            } else if (MainActivity.context.get()!!.getString(R.string.bengaliVal) == ""+locale){
                jsonObject = JSONObject(getJSONFromAssets("json/menu_data_bengali.json")!!)
            } else if (MainActivity.context.get()!!.getString(R.string.gujaratiVal) == ""+locale){
                jsonObject = JSONObject(getJSONFromAssets("json/menu_data_gujarati.json")!!)
            } else if (MainActivity.context.get()!!.getString(R.string.hindiVal) == ""+locale){
                if(data == USER_TYPE){
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_hindi.json")!!)
                } else if(data == USER_TYPE_ADMIN){
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_hindi_controller.json")!!)
                } else {
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_hindi.json")!!)
                }
            } else if (MainActivity.context.get()!!.getString(R.string.kannadaVal) == ""+locale){
                jsonObject = JSONObject(getJSONFromAssets("json/menu_data_kannada.json")!!)
            } else if (MainActivity.context.get()!!.getString(R.string.malayalamVal) == ""+locale){
                jsonObject = JSONObject(getJSONFromAssets("json/menu_data_malayalam.json")!!)
            } else if (MainActivity.context.get()!!.getString(R.string.marathiVal) == ""+locale){
                jsonObject = JSONObject(getJSONFromAssets("json/menu_data_marathi.json")!!)
            } else if (MainActivity.context.get()!!.getString(R.string.punjabiVal) == ""+locale){
                jsonObject = JSONObject(getJSONFromAssets("json/menu_data_punjabi.json")!!)
            } else if (MainActivity.context.get()!!.getString(R.string.tamilVal) == ""+locale){
                jsonObject = JSONObject(getJSONFromAssets("json/menu_data_tamil.json")!!)
            } else if (MainActivity.context.get()!!.getString(R.string.teluguVal) == ""+locale){
                jsonObject = JSONObject(getJSONFromAssets("json/menu_data_telugu.json")!!)
            } else if (MainActivity.context.get()!!.getString(R.string.assameseVal) == ""+locale){
                jsonObject = JSONObject(getJSONFromAssets("json/menu_data_assamese.json")!!)
            } else if (MainActivity.context.get()!!.getString(R.string.urduVal) == ""+locale){
                jsonObject = JSONObject(getJSONFromAssets("json/menu_data_urdu.json")!!)
            }

            val jsonArray = jsonObject.getJSONArray("menu")
            val k = jsonArray.length()

            for (i in 0 until k) {
                val tempJsonObject = jsonArray.getJSONObject(i).toString()
               // if (i != 9){
                    newspaperList?.add(Gson().fromJson(tempJsonObject, ItemMenuModel::class.java))
              //  }
            }
            return newspaperList
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun getJSONFromAssets(fileName: String): String? {
        val json: String
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return json
    }
}