package com.nhm.distributor.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.DimenRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.stfalcon.imageviewer.StfalconImageViewer
import com.nhm.distributor.R
import com.nhm.distributor.models.ItemReturn
import com.nhm.distributor.screens.mainActivity.MainActivity
import com.nhm.distributor.screens.mainActivity.MainActivityVM.Companion.locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.math.RoundingMode
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


fun View.openKeyboard() = try {
    (MainActivity.context?.get() as Activity).apply {
        postDelayed({
            val inputMethodManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            this@openKeyboard.requestFocus()
            inputMethodManager.showSoftInput(currentFocus, InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }
} catch (e: Exception) {
    e.printStackTrace()
}


fun hideKeyboard() = try {
    (MainActivity.context?.get() as Activity).apply {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
} catch (e: Exception) {
    e.printStackTrace()
}


/**
 * Show Snack Bar
 * */
@SuppressLint("CutPasteId")
fun showSnackBar(string: String, type: Int = 1, navController: NavController? = null) = try {
    hideKeyboard()
    MainActivity.context.get()?.let { context ->
        val message = if (string.contains("Unable to resolve host")) {
            context.getString(R.string.no_internet_connection)
        } else if (string.contains("DOCTYPE html")) {
            context.getString(R.string.something_went_wrong)
        } else if (string.contains("<script>")) {
            context.getString(R.string.something_went_wrong)
        } else if (string.contains("SQLSTATE")) {
            context.getString(R.string.something_went_wrong)
        } else {
            "" + string
        }
        Snackbar.make(
            (context as Activity).findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            setBackgroundTint(ContextCompat.getColor(context, R.color._000000))
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            setTextColor(ContextCompat.getColor(context, R.color._ffffff))
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).textSize =
                (MainActivity.scale10 + 1).toFloat()
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 5
            if (type == 2) {
                setAction(context.getString(R.string.subscribe_now), View.OnClickListener {
                    navController?.navigate(R.id.subscription)
                    this.dismiss()
                }).setActionTextColor(ContextCompat.getColor(context, R.color._ffffff))
                view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).textSize =
                    (MainActivity.scale10 + 1).toFloat()
                view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).isAllCaps =
                    false
            }
            show()
        }
    }
} catch (e: Exception) {
    e.printStackTrace()
}

@SuppressLint("CutPasteId")
fun showSnackBarWithoutKeypad(string: String, type: Int = 1, navController: NavController? = null) = try {
//    hideKeyboard()
    MainActivity.context.get()?.let { context ->
        val message = if (string.contains("Unable to resolve host")) {
            context.getString(R.string.no_internet_connection)
        } else if (string.contains("DOCTYPE html")) {
            context.getString(R.string.something_went_wrong)
        } else if (string.contains("<script>")) {
            context.getString(R.string.something_went_wrong)
        } else if (string.contains("SQLSTATE")) {
            context.getString(R.string.something_went_wrong)
        } else {
            "" + string
        }
        Snackbar.make(
            (context as Activity).findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            setBackgroundTint(ContextCompat.getColor(context, R.color._000000))
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            setTextColor(ContextCompat.getColor(context, R.color._ffffff))
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).textSize =
                (MainActivity.scale10 + 1).toFloat()
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 5
            if (type == 2) {
                setAction(context.getString(R.string.subscribe_now), View.OnClickListener {
                    navController?.navigate(R.id.subscription)
                    this.dismiss()
                }).setActionTextColor(ContextCompat.getColor(context, R.color._ffffff))
                view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).textSize =
                    (MainActivity.scale10 + 1).toFloat()
                view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).isAllCaps =
                    false
            }
            show()
        }
    }
} catch (e: Exception) {
    e.printStackTrace()
}


/**
 * Handle Error Messages
 * */
fun Any?.getErrorMessage(): String = when (this) {
    is Throwable -> this.message.getResponseError()
    else -> this.toString().getResponseError()
}

/**
 * Get Response error
 * */
fun String?.getResponseError(): String {
    if (this.isNullOrEmpty()) return ""
    return try {
        val jsonObject = JSONObject(this)
        if (jsonObject.has("message")) {
            jsonObject.getString("message")
        } else if (jsonObject.has("errors")) {
            val array = jsonObject.getJSONArray("errors")
            if (array.length() > 0) {
                array.getJSONObject(0)?.let {
                    if (it.has("message"))
                        return it.getString("message")
                }
            }
            this
        } else this
    } catch (e: Exception) {
        this
    }
}


fun Context.getRealPathFromUri(contentUri: Uri?): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf<kotlin.String>(MediaStore.Images.Media.DATA)
        //val proj: Array<String>= arrayOf<kotlin.String>(MediaStore.Images.Media.DATA)
        cursor = contentResolver.query(contentUri!!, proj, null, null, null)
        assert(cursor != null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        cursor.getString(column_index)
    } finally {
        cursor?.close()
    }
}


fun Context.getMediaFilePathFor(uri: Uri): String {
    return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
//        val name = cursor.getString(nameIndex)
        File(filesDir, getImageName()).run {
            kotlin.runCatching {
                contentResolver.openInputStream(uri).use { inputStream ->
                    val outputStream = FileOutputStream(this)
                    var read: Int
                    val buffers = ByteArray(inputStream!!.available())
                    while (inputStream.read(buffers).also { read = it } != -1) {
                        outputStream.use {
                            it.write(buffers, 0, read)
                        }
                    }
                }
            }.onFailure {
                ///Logger.error("File Size %s", it.message.orEmpty())
            }
            return path
        }
    } ?: ""
}


@SuppressLint("CheckResult")
fun ImageView.loadImage(
    type: Int,
    url: () -> String,
    errorPlaceHolder: () -> Int = { if (type == 1) R.drawable.user_icon else R.drawable.no_image_banner },
) = try {
    val circularProgressDrawable = CircularProgressDrawable(this.context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }
    load(if (url().startsWith("http")) url() else File(url())) {
        placeholder(circularProgressDrawable)
        crossfade(true)
        error(errorPlaceHolder())
    }
} catch (e: Exception) {
    e.printStackTrace()
}



@SuppressLint("CheckResult")
fun ImageView.loadImageForms(
    type: Int,
    url: () -> String,
    errorPlaceHolder: () -> Int = { if (type == 1) R.drawable.user_icon else R.drawable.no_image_banner },
) = try {
    val circularProgressDrawable = CircularProgressDrawable(this.context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }
    load(if (url().startsWith("http")) url() else File(url())) {
//        placeholder(errorPlaceHolder)
        crossfade(true)
        error(circularProgressDrawable)
    }
} catch (e: Exception) {
    e.printStackTrace()
}



fun isValidPassword(password: String): Boolean {
    val pattern: Pattern
    val matcher: Matcher
    val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
    val PASSWORD_REGEX =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,20}$"
    pattern = Pattern.compile(PASSWORD_REGEX)
    matcher = pattern.matcher(password)
    return matcher.matches()
}


fun AppCompatEditText.focus() {
//    text?.let { setSelection(it.length) }
//    postDelayed({
//        requestFocus()
//        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
//    }, 100)
}


var runnable: Runnable? = null
fun ViewPager.autoScroll() {
    autoScrollStop()
    var scrollPosition = 0
    runnable = object : Runnable {
        override fun run() {
            val count = adapter?.count ?: 0
            setCurrentItem(scrollPosition++ % count, true)
            if (handler != null) {
                handler?.let {
                    postDelayed(this, 3000)
                }
            }
        }
    }
    if (handler != null) {
        if (runnable != null) {
            handler?.let {
                post(runnable as Runnable)
            }
        }
    }
}


fun ViewPager.autoScrollStop() {
    if (handler != null) {
        if (runnable != null) {
            runnable?.let { handler?.removeCallbacks(it) }
        }
    }
}


@SuppressLint("SimpleDateFormat")
fun String.changeDateFormat(inDate: String, outDate: String): String? {
    var str: String? = ""
    try {
        val inputFormat = SimpleDateFormat(inDate)
        val outputFormat = SimpleDateFormat(outDate)
        var date: Date? = null
        try {
            date = inputFormat.parse(this)
            str = date?.let { outputFormat.format(it) }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    } catch (e: Exception) {
        str = ""
    }
    return str
}


@SuppressLint("ClickableViewAccessibility")
fun AppCompatEditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
    this.setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is EditText) {
            if (event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}


fun ArrayList<String>.imageZoom(ivImage: ImageView, type: Int) {
    StfalconImageViewer.Builder<String>(MainActivity.mainActivity.get()!!, this) { view, image ->
        Glide.with(MainActivity.mainActivity.get()!!)
            .load(image)
            .apply(if (type == 1) myOptionsGlide else if (type == 2) myOptionsGlideUser else myOptionsGlide)
            .into(view)
    }
        .withTransitionFrom(ivImage)
        .withBackgroundColor(
            ContextCompat.getColor(
                MainActivity.mainActivity.get()!!,
                R.color._D9000000
            )
        )
        .show()
}


fun getToken(callBack: String.() -> Unit) {
    FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
        if (result != null) {
            callBack(result)
        }
    }.addOnCanceledListener {
        callBack("")
    }
}


fun String.firstCharIfItIsLowercase() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}


fun Context.isTablet(): Boolean {
    return this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
}


fun ViewPager2.updatePagerHeightForChild(view: View) {
    view.post {
        val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
        val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(wMeasureSpec, hMeasureSpec)
        layoutParams = (layoutParams).also { lp -> lp.height = height }
        invalidate()
    }
}


val myOptionsGlide: RequestOptions = RequestOptions()
    .placeholder(R.drawable.main_logo_land)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .dontAnimate()
    //  .apply( RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.no_image_2))
    .skipMemoryCache(false)

val myOptionsGlideUser: RequestOptions = RequestOptions()
    .placeholder(R.drawable.user_icon)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .dontAnimate()
    //  .apply( RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.no_image_2))
    .skipMemoryCache(false)

fun String.glideImage(context: Context, ivMap: ShapeableImageView) {
    Glide.with(context)
        .load(this)
        .apply(myOptionsGlide)
        .into(ivMap)
}

fun String.glideImage(context: Context, ivMap: AppCompatImageView) {
    Glide.with(context)
        .load(this)
        .apply(myOptionsGlide)
        .into(ivMap)
}

val myOptionsGlidePortrait: RequestOptions = RequestOptions()
    .placeholder(R.drawable.main_logo)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .dontAnimate()
    //  .apply( RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.no_image_2))
    .skipMemoryCache(false)

fun String.glideImagePortrait(context: Context, ivMap: ShapeableImageView) {
    Glide.with(context)
        .load(this)
        .apply(myOptionsGlidePortrait)
        .into(ivMap)
}


fun String.glideImagePortraitForImage(context: Context, ivMap: ImageView) {
    Glide.with(context)
        .load(this)
        .apply(myOptionsGlidePortrait)
        .into(ivMap)
}


fun View.singleClick(throttleTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < throttleTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}


fun View.singleClick2Sec(throttleTime: Long = 2000L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < throttleTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}


fun Context.isAppIsInBackground(): Boolean {
    var isInBackground = true
    try {
        val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == this.packageName) {
                        isInBackground = false
                    }
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return isInBackground
}


fun Context.px(@DimenRes dimen: Int): Int = resources.getDimension(dimen).toInt()

fun Context.dp(@DimenRes dimen: Int): Float = px(dimen) / resources.displayMetrics.density


fun String.relationType(array: Array<String>): String {
    "" + this.let {
        return when (it) {
            "father" -> {
                array[0]
            }

            "mother" -> {
                array[1]
            }

            "son" -> {
                array[2]
            }

            "daughter" -> {
                array[3]
            }

            "sister" -> {
                array[4]
            }

            "brother" -> {
                array[5]
            }

            "husband" -> {
                array[6]
            }

            "wife" -> {
                array[7]
            }

            else -> {
                ""
            }
        }
    }
}


@Throws(Exception::class)
fun String.parseResult(): String {
    var words = ""
    val jsonArray = JSONArray(this)
    val jsonArray2 = jsonArray[0] as JSONArray
    for (i in 0..jsonArray2.length() - 1) {
        val jsonArray3 = jsonArray2[i] as JSONArray
        words += jsonArray3[0].toString()
    }
    return words.toString()
}


var networkAlert: AlertDialog? = null

@SuppressLint("SuspiciousIndentation")
fun Context.callNetworkDialog() {
    if (networkAlert?.isShowing == true) {
        return
    }
    networkAlert = MaterialAlertDialogBuilder(this, R.style.LogoutDialogTheme)
        .setTitle(resources.getString(R.string.app_name))
        .setMessage(resources.getString(R.string.no_internet_connection))
        .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
//        .setCancelable(false)
        .show()
}


fun Context.isNetworkAvailable() =
    (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        getNetworkCapabilities(activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }


fun Context.getTitle(_type: String, _title: String): String {
    val locale = LocaleHelper.getLanguage(applicationContext)
    var title = ""
    if (_type == "scheme") {
        title = if (applicationContext.resources.getString(R.string.englishVal) == "" + locale) {
            applicationContext.resources.getString(R.string.scheme_title)
        } else if (applicationContext.resources.getString(R.string.bengaliVal) == "" + locale) {
            applicationContext.resources.getString(R.string.scheme_title_bn)
        } else if (applicationContext.resources.getString(R.string.gujaratiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.scheme_title_gu)
        } else if (applicationContext.resources.getString(R.string.hindiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.scheme_title_hi)
        } else if (applicationContext.resources.getString(R.string.kannadaVal) == "" + locale) {
            applicationContext.resources.getString(R.string.scheme_title_kn)
        } else if (applicationContext.resources.getString(R.string.malayalamVal) == "" + locale) {
            applicationContext.resources.getString(R.string.scheme_title_ml)
        } else if (applicationContext.resources.getString(R.string.marathiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.scheme_title_mr)
        } else if (applicationContext.resources.getString(R.string.punjabiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.scheme_title_pa)
        } else if (applicationContext.resources.getString(R.string.tamilVal) == "" + locale) {
            applicationContext.resources.getString(R.string.scheme_title_ta)
        } else if (applicationContext.resources.getString(R.string.teluguVal) == "" + locale) {
            applicationContext.resources.getString(R.string.scheme_title_te)
        } else if (applicationContext.resources.getString(R.string.assameseVal) == "" + locale) {
            applicationContext.resources.getString(R.string.scheme_title_as)
        } else if (applicationContext.resources.getString(R.string.urduVal) == "" + locale) {
            applicationContext.resources.getString(R.string.scheme_title_ur)
        } else {
            applicationContext.resources.getString(R.string.scheme_title)
        }
    } else if (_type == "notice") {
        title = if (applicationContext.resources.getString(R.string.englishVal) == "" + locale) {
            applicationContext.resources.getString(R.string.notice_title)
        } else if (applicationContext.resources.getString(R.string.bengaliVal) == "" + locale) {
            applicationContext.resources.getString(R.string.notice_title_bn)
        } else if (applicationContext.resources.getString(R.string.gujaratiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.notice_title_gu)
        } else if (applicationContext.resources.getString(R.string.hindiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.notice_title_hi)
        } else if (applicationContext.resources.getString(R.string.kannadaVal) == "" + locale) {
            applicationContext.resources.getString(R.string.notice_title_kn)
        } else if (applicationContext.resources.getString(R.string.malayalamVal) == "" + locale) {
            applicationContext.resources.getString(R.string.notice_title_ml)
        } else if (applicationContext.resources.getString(R.string.marathiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.notice_title_mr)
        } else if (applicationContext.resources.getString(R.string.punjabiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.notice_title_pa)
        } else if (applicationContext.resources.getString(R.string.tamilVal) == "" + locale) {
            applicationContext.resources.getString(R.string.notice_title_ta)
        } else if (applicationContext.resources.getString(R.string.teluguVal) == "" + locale) {
            applicationContext.resources.getString(R.string.notice_title_te)
        } else if (applicationContext.resources.getString(R.string.assameseVal) == "" + locale) {
            applicationContext.resources.getString(R.string.notice_title_as)
        } else if (applicationContext.resources.getString(R.string.urduVal) == "" + locale) {
            applicationContext.resources.getString(R.string.notice_title_ur)
        } else {
            applicationContext.resources.getString(R.string.notice_title)
        }
    } else if (_type == "training") {
        title = if (applicationContext.resources.getString(R.string.englishVal) == "" + locale) {
            applicationContext.resources.getString(R.string.training_title)
        } else if (applicationContext.resources.getString(R.string.bengaliVal) == "" + locale) {
            applicationContext.resources.getString(R.string.training_title_bn)
        } else if (applicationContext.resources.getString(R.string.gujaratiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.training_title_gu)
        } else if (applicationContext.resources.getString(R.string.hindiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.training_title_hi)
        } else if (applicationContext.resources.getString(R.string.kannadaVal) == "" + locale) {
            applicationContext.resources.getString(R.string.training_title_kn)
        } else if (applicationContext.resources.getString(R.string.malayalamVal) == "" + locale) {
            applicationContext.resources.getString(R.string.training_title_ml)
        } else if (applicationContext.resources.getString(R.string.marathiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.training_title_mr)
        } else if (applicationContext.resources.getString(R.string.punjabiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.training_title_pa)
        } else if (applicationContext.resources.getString(R.string.tamilVal) == "" + locale) {
            applicationContext.resources.getString(R.string.training_title_ta)
        } else if (applicationContext.resources.getString(R.string.teluguVal) == "" + locale) {
            applicationContext.resources.getString(R.string.training_title_te)
        } else if (applicationContext.resources.getString(R.string.assameseVal) == "" + locale) {
            applicationContext.resources.getString(R.string.training_title_as)
        } else if (applicationContext.resources.getString(R.string.urduVal) == "" + locale) {
            applicationContext.resources.getString(R.string.training_title_ur)
        } else {
            applicationContext.resources.getString(R.string.training_title)
        }
    } else if (_type == "Vendor Details" || _type == "VendorDetails") {
        if (_title.contains("approved")) {
            title =
                if (applicationContext.resources.getString(R.string.englishVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title)
                } else if (applicationContext.resources.getString(R.string.bengaliVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_bn)
                } else if (applicationContext.resources.getString(R.string.gujaratiVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_gu)
                } else if (applicationContext.resources.getString(R.string.hindiVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_hi)
                } else if (applicationContext.resources.getString(R.string.kannadaVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_kn)
                } else if (applicationContext.resources.getString(R.string.malayalamVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_ml)
                } else if (applicationContext.resources.getString(R.string.marathiVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_mr)
                } else if (applicationContext.resources.getString(R.string.punjabiVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_pa)
                } else if (applicationContext.resources.getString(R.string.tamilVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_ta)
                } else if (applicationContext.resources.getString(R.string.teluguVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_te)
                } else if (applicationContext.resources.getString(R.string.assameseVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_as)
                } else if (applicationContext.resources.getString(R.string.urduVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_ur)
                } else {
                    applicationContext.resources.getString(R.string.vendor_details_title)
                }
        } else if (_title.contains("rejected")) {
            title =
                if (applicationContext.resources.getString(R.string.englishVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject)
                } else if (applicationContext.resources.getString(R.string.bengaliVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject_bn)
                } else if (applicationContext.resources.getString(R.string.gujaratiVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject_gu)
                } else if (applicationContext.resources.getString(R.string.hindiVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject_hi)
                } else if (applicationContext.resources.getString(R.string.kannadaVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject_kn)
                } else if (applicationContext.resources.getString(R.string.malayalamVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject_ml)
                } else if (applicationContext.resources.getString(R.string.marathiVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject_mr)
                } else if (applicationContext.resources.getString(R.string.punjabiVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject_pa)
                } else if (applicationContext.resources.getString(R.string.tamilVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject_ta)
                } else if (applicationContext.resources.getString(R.string.teluguVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject_te)
                } else if (applicationContext.resources.getString(R.string.assameseVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject_as)
                } else if (applicationContext.resources.getString(R.string.urduVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject_ur)
                } else {
                    applicationContext.resources.getString(R.string.vendor_details_title_reject)
                }
        } else if (_title.contains("pending")) {
            title =
                if (applicationContext.resources.getString(R.string.englishVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.pending_title_en)
                } else if (applicationContext.resources.getString(R.string.bengaliVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.pending_title_bn)
                } else if (applicationContext.resources.getString(R.string.gujaratiVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.pending_title_gu)
                } else if (applicationContext.resources.getString(R.string.hindiVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.pending_title_hi)
                } else if (applicationContext.resources.getString(R.string.kannadaVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.pending_title_kn)
                } else if (applicationContext.resources.getString(R.string.malayalamVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.pending_title_ml)
                } else if (applicationContext.resources.getString(R.string.marathiVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.pending_title_mr)
                } else if (applicationContext.resources.getString(R.string.punjabiVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.pending_title_pa)
                } else if (applicationContext.resources.getString(R.string.tamilVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.pending_title_ta)
                } else if (applicationContext.resources.getString(R.string.teluguVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.pending_title_te)
                } else if (applicationContext.resources.getString(R.string.assameseVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.pending_title_as)
                } else if (applicationContext.resources.getString(R.string.urduVal) == "" + locale) {
                    applicationContext.resources.getString(R.string.pending_title_ur)
                } else {
                    applicationContext.resources.getString(R.string.pending_title_en)
                }
        }
    } else if (_type == "information") {
        title = if (applicationContext.resources.getString(R.string.englishVal) == "" + locale) {
            applicationContext.resources.getString(R.string.information_title)
        } else if (applicationContext.resources.getString(R.string.bengaliVal) == "" + locale) {
            applicationContext.resources.getString(R.string.information_title_bn)
        } else if (applicationContext.resources.getString(R.string.gujaratiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.information_title_gu)
        } else if (applicationContext.resources.getString(R.string.hindiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.information_title_hi)
        } else if (applicationContext.resources.getString(R.string.kannadaVal) == "" + locale) {
            applicationContext.resources.getString(R.string.information_title_kn)
        } else if (applicationContext.resources.getString(R.string.malayalamVal) == "" + locale) {
            applicationContext.resources.getString(R.string.information_title_ml)
        } else if (applicationContext.resources.getString(R.string.marathiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.information_title_mr)
        } else if (applicationContext.resources.getString(R.string.punjabiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.information_title_pa)
        } else if (applicationContext.resources.getString(R.string.tamilVal) == "" + locale) {
            applicationContext.resources.getString(R.string.information_title_ta)
        } else if (applicationContext.resources.getString(R.string.teluguVal) == "" + locale) {
            applicationContext.resources.getString(R.string.information_title_te)
        } else if (applicationContext.resources.getString(R.string.assameseVal) == "" + locale) {
            applicationContext.resources.getString(R.string.information_title_as)
        } else if (applicationContext.resources.getString(R.string.urduVal) == "" + locale) {
            applicationContext.resources.getString(R.string.information_title_ur)
        } else {
            applicationContext.resources.getString(R.string.information_title)
        }
    } else if (_title.contains("Feedback")) {
        title = if (applicationContext.resources.getString(R.string.englishVal) == "" + locale) {
            applicationContext.resources.getString(R.string.feedback_title)
        } else if (applicationContext.resources.getString(R.string.bengaliVal) == "" + locale) {
            applicationContext.resources.getString(R.string.feedback_title_bn)
        } else if (applicationContext.resources.getString(R.string.gujaratiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.feedback_title_gu)
        } else if (applicationContext.resources.getString(R.string.hindiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.feedback_title_hi)
        } else if (applicationContext.resources.getString(R.string.kannadaVal) == "" + locale) {
            applicationContext.resources.getString(R.string.feedback_title_kn)
        } else if (applicationContext.resources.getString(R.string.malayalamVal) == "" + locale) {
            applicationContext.resources.getString(R.string.feedback_title_ml)
        } else if (applicationContext.resources.getString(R.string.marathiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.feedback_title_mr)
        } else if (applicationContext.resources.getString(R.string.punjabiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.feedback_title_pa)
        } else if (applicationContext.resources.getString(R.string.tamilVal) == "" + locale) {
            applicationContext.resources.getString(R.string.feedback_title_ta)
        } else if (applicationContext.resources.getString(R.string.teluguVal) == "" + locale) {
            applicationContext.resources.getString(R.string.feedback_title_te)
        } else if (applicationContext.resources.getString(R.string.assameseVal) == "" + locale) {
            applicationContext.resources.getString(R.string.feedback_title_as)
        } else if (applicationContext.resources.getString(R.string.urduVal) == "" + locale) {
            applicationContext.resources.getString(R.string.feedback_title_ur)
        } else {
            applicationContext.resources.getString(R.string.feedback_title)
        }
    } else if (_title.contains("Complaint")) {
        title = if (applicationContext.resources.getString(R.string.englishVal) == "" + locale) {
            applicationContext.resources.getString(R.string.complaint_title)
        } else if (applicationContext.resources.getString(R.string.bengaliVal) == "" + locale) {
            applicationContext.resources.getString(R.string.complaint_title_bn)
        } else if (applicationContext.resources.getString(R.string.gujaratiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.complaint_title_gu)
        } else if (applicationContext.resources.getString(R.string.hindiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.complaint_title_hi)
        } else if (applicationContext.resources.getString(R.string.kannadaVal) == "" + locale) {
            applicationContext.resources.getString(R.string.complaint_title_kn)
        } else if (applicationContext.resources.getString(R.string.malayalamVal) == "" + locale) {
            applicationContext.resources.getString(R.string.complaint_title_ml)
        } else if (applicationContext.resources.getString(R.string.marathiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.complaint_title_mr)
        } else if (applicationContext.resources.getString(R.string.punjabiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.complaint_title_pa)
        } else if (applicationContext.resources.getString(R.string.tamilVal) == "" + locale) {
            applicationContext.resources.getString(R.string.complaint_title_ta)
        } else if (applicationContext.resources.getString(R.string.teluguVal) == "" + locale) {
            applicationContext.resources.getString(R.string.complaint_title_te)
        } else if (applicationContext.resources.getString(R.string.assameseVal) == "" + locale) {
            applicationContext.resources.getString(R.string.complaint_title_as)
        } else if (applicationContext.resources.getString(R.string.urduVal) == "" + locale) {
            applicationContext.resources.getString(R.string.complaint_title_ur)
        } else {
            applicationContext.resources.getString(R.string.complaint_title)
        }
    } else if (_type == "membership") {
        title = if (applicationContext.resources.getString(R.string.englishVal) == "" + locale) {
            applicationContext.resources.getString(R.string.membership_title)
        } else if (applicationContext.resources.getString(R.string.bengaliVal) == "" + locale) {
            applicationContext.resources.getString(R.string.membership_title_bn)
        } else if (applicationContext.resources.getString(R.string.gujaratiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.membership_title_gu)
        } else if (applicationContext.resources.getString(R.string.hindiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.membership_title_hi)
        } else if (applicationContext.resources.getString(R.string.kannadaVal) == "" + locale) {
            applicationContext.resources.getString(R.string.membership_title_kn)
        } else if (applicationContext.resources.getString(R.string.malayalamVal) == "" + locale) {
            applicationContext.resources.getString(R.string.membership_title_ml)
        } else if (applicationContext.resources.getString(R.string.marathiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.membership_title_mr)
        } else if (applicationContext.resources.getString(R.string.punjabiVal) == "" + locale) {
            applicationContext.resources.getString(R.string.membership_title_pa)
        } else if (applicationContext.resources.getString(R.string.tamilVal) == "" + locale) {
            applicationContext.resources.getString(R.string.membership_title_ta)
        } else if (applicationContext.resources.getString(R.string.teluguVal) == "" + locale) {
            applicationContext.resources.getString(R.string.membership_title_te)
        } else if (applicationContext.resources.getString(R.string.assameseVal) == "" + locale) {
            applicationContext.resources.getString(R.string.membership_title_as)
        } else if (applicationContext.resources.getString(R.string.urduVal) == "" + locale) {
            applicationContext.resources.getString(R.string.membership_title_ur)
        } else {
            applicationContext.resources.getString(R.string.membership_title)
        }
    } else {
        ""
    }
    return title
}


fun String.getChannelName(): String {
    return if (this == "scheme") {
        "Scheme"
    } else if (this == "notice") {
        "Notice"
    } else if (this == "training") {
        "Training"
    } else if (this == "Vendor Details" || this == "VendorDetails") {
        "Vendor Details"
    } else if (this == "information") {
        "Information Center"
    } else if (this == "Feedback") {
        "Complaints/Feedback"
    } else if (this == "membership") {
        "Membership"
    } else {
        "Others"
    }
}


fun String.getNotificationId(): Int {
    return if (this == "scheme") {
        1
    } else if (this == "notice") {
        2
    } else if (this == "training") {
        3
    } else if (this == "Vendor Details" || this == "VendorDetails") {
        4
    } else if (this == "information") {
        5
    } else if (this == "Feedback") {
        6
    } else if (this == "membership") {
        7
    } else {
        8
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}


fun String.parseOneTimeCode(): String {
    return if (this != null && this.length >= 6) {
        this.trim { it <= ' ' }.substring(0, 6)
    } else ""
}


@Suppress("DEPRECATION")
inline fun <reified P : Parcelable> Intent.getParcelable(key: String): P? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, P::class.java)
    } else {
        getParcelableExtra(key)
    }
}

fun Context.determineScreenDensityCode(): String {
    return when (resources.displayMetrics.densityDpi) {
        DisplayMetrics.DENSITY_LOW -> "ldpi"
        DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
        DisplayMetrics.DENSITY_HIGH -> "hdpi"
        DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_280 -> "xhdpi"
        DisplayMetrics.DENSITY_XXHIGH, DisplayMetrics.DENSITY_360, DisplayMetrics.DENSITY_400, DisplayMetrics.DENSITY_420 -> "xxhdpi"
        DisplayMetrics.DENSITY_XXXHIGH, DisplayMetrics.DENSITY_560 -> "xxxhdpi"
        else -> "Unknown code ${resources.displayMetrics.densityDpi}"
    }
}


fun Context.getDensityName(): String {
    val density = resources.displayMetrics.density
    if (density >= 4.0) {
        return "xxxhdpi"
    }
    if (density >= 3.0) {
        return "xxhdpi"
    }
    if (density >= 2.0) {
        return "xhdpi"
    }
    if (density >= 1.5) {
        return "hdpi"
    }
    return if (density >= 1.0) {
        "mdpi"
    } else "ldpi"
}


@RequiresApi(Build.VERSION_CODES.P)
fun Context.getSignature(): String {
    var info: PackageInfo? = null
    try {
        info = packageManager.getPackageInfo(
            packageName,
            PackageManager.GET_SIGNING_CERTIFICATES
        )
        val sigHistory: Array<Signature> = info.signingInfo!!.signingCertificateHistory
        val signature: ByteArray = sigHistory[0].toByteArray()
        val md = MessageDigest.getInstance("SHA1")
        val digest = md.digest(signature)
        val sha1Builder = StringBuilder()
        for (b in digest) sha1Builder.append(String.format("%02x", b))
        return sha1Builder.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}


fun Double.decimal2Digits(): String {
    return String.format("%.2f", this)
}

fun Double.roundOffDecimal(): String { //here, 1.45678 = 1.46
    val df = DecimalFormat("####0.00", DecimalFormatSymbols(Locale.ENGLISH))
    df.roundingMode = RoundingMode.HALF_UP
    return "" + df.format(this)
}

//fun roundOffDecimal(number: Double): Double? { //here, 1.45678 = 1.45
//    val df = DecimalFormat("#.##")
//    df.roundingMode = RoundingMode.FLOOR
//    return df.format(number).toDouble()
//}

fun Activity.getCameraPath(callBack: Uri.() -> Unit) {
    runOnUiThread() {
        val directory = File(filesDir, "camera_images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val uriReal = FileProvider.getUriForFile(
            this,
            packageName + ".provider",
            File(directory, "${Calendar.getInstance().timeInMillis}.png")
        )
        callBack(uriReal)
    }
}


fun getImageName(): String {
    return "${"NBPA_" + SimpleDateFormat("HHmmss").format(Date())}.png"
}


@SuppressLint("SimpleDateFormat")
fun getPdfName(): String {
    return "${"NBPA_" + SimpleDateFormat("HHmmss").format(Date())}.pdf"
}


fun Activity.showOptions(callBack: Int.() -> Unit) = try {
    val dialogView = layoutInflater.inflate(R.layout.dialog_choose_image_option, null)
    val btnCancel = dialogView.findViewById<AppCompatButton>(R.id.btnCancel)
    val tvPhotos = dialogView.findViewById<AppCompatTextView>(R.id.tvPhotos)
    val tvPhotosDesc = dialogView.findViewById<AppCompatTextView>(R.id.tvPhotosDesc)
    val tvCamera = dialogView.findViewById<AppCompatTextView>(R.id.tvCamera)
    val tvCameraDesc = dialogView.findViewById<AppCompatTextView>(R.id.tvCameraDesc)
    val dialog = BottomSheetDialog(this, R.style.TransparentDialog)
    dialog.setContentView(dialogView)
    dialog.show()

    btnCancel.singleClick {
        dialog.dismiss()
    }
    tvCamera.singleClick {
        dialog.dismiss()
        callBack(1)
    }
    tvCameraDesc.singleClick {
        dialog.dismiss()
        callBack(1)
    }

    tvPhotos.singleClick {
        dialog.dismiss()
        callBack(2)
    }
    tvPhotosDesc.singleClick {
        dialog.dismiss()
        callBack(2)
    }
} catch (e: Exception) {
    e.printStackTrace()
}


@SuppressLint("SuspiciousIndentation")
fun Activity.callPermissionDialog(callBack: Intent.() -> Unit) {
    MaterialAlertDialogBuilder(this, R.style.LogoutDialogTheme)
        .setTitle(resources.getString(R.string.app_name))
        .setMessage(resources.getString(R.string.required_permissions))
        .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
            dialog.dismiss()
            callBack(Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                addCategory(Intent.CATEGORY_DEFAULT)
                data = Uri.parse("package:" + packageName)
            })
        }
        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
//            Handler(Looper.getMainLooper()).postDelayed({
//                MainActivity.binding.drawerLayout.close()
//            }, 500)
        }
        .setCancelable(false)
        .show()
}


fun Activity.showDropDownDialog(
    type: Int = 0,
    arrayList: Array<String?> = emptyArray(),
    callBack: ItemReturn.() -> Unit,
) {
    hideKeyboard()

    when (type) {
        1 -> {
            val list = resources.getStringArray(R.array.gender_array)
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.gender))
                .setItems(list) { _, which ->
                    callBack(ItemReturn(which, list[which]))
                }.show()
        }

        2 -> {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                this,
                R.style.CalendarDatePickerDialog,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    val today = LocalDate.now()
                    val birthday: LocalDate = LocalDate.of(year, (monthOfYear + 1), dayOfMonth)
                    val p: Period = Period.between(birthday, today)

                    var mm: String = (monthOfYear + 1).toString()
                    if (mm.length == 1) {
                        mm = "0" + mm
                    }

                    var dd: String = "" + dayOfMonth
                    if (dd.length == 1) {
                        dd = "0" + dd
                    }

                    if (p.getYears() > 13) {
                        callBack(ItemReturn(name = "" + year + "-" + mm + "-" + dd))
                    } else {
                        showSnackBar(getString(R.string.age_minimum))
                        callBack(ItemReturn(name = ""))
                    }
                },
                year,
                month,
                day
            )
            dpd.show()
        }

        3 -> {
            val list = resources.getStringArray(R.array.socialCategory_array)
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.social_category))
                .setItems(list) { _, which ->
                    callBack(ItemReturn(which, list[which]))
                }.show()
        }

        4 -> {
            val list = resources.getStringArray(R.array.socialEducation_array)
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.education_qualifacation))
                .setItems(list) { _, which ->
                    callBack(ItemReturn(which, list[which]))
                }.show()
        }

        5 -> {
            val list = resources.getStringArray(R.array.maritalStatus_array)
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.marital_status))
                .setItems(list) { _, which ->
                    callBack(ItemReturn(which, list[which]))
                }.show()
        }

        6 -> {
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.select_state))
                .setItems(arrayList) { _, which ->
                    callBack(ItemReturn(which, arrayList[which]!!))
                }.show()
        }

        7 -> {
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.select_district))
                .setItems(arrayList) { _, which ->
                    callBack(ItemReturn(which, arrayList[which]!!))
                }.show()
        }

        8 -> {
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.municipality_panchayat))
                .setItems(arrayList) { _, which ->
                    callBack(ItemReturn(which, arrayList[which]!!))
                }.show()
        }

        9 -> {
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.select_pincode))
                .setItems(arrayList) { _, which ->
                    callBack(ItemReturn(which, arrayList[which]!!))
                }.show()
        }

        10 -> {
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.type_of_market_place))
                .setItems(arrayList) { _, which ->
                    callBack(ItemReturn(which, arrayList[which]!!))
                }.show()
        }

        11 -> {
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.type_of_vending))
                .setItems(arrayList) { _, which ->
                    callBack(ItemReturn(which, arrayList[which]!!))
                }.show()
        }

        12 -> {
            val list = resources.getStringArray(R.array.years_array)
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.total_years_of_vending))
                .setItems(list) { _, which ->
                    callBack(ItemReturn(which, list[which]))
                }.show()
        }

        13 -> {
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.localOrganisation))
                .setItems(arrayList) { _, which ->
                    callBack(ItemReturn(which, arrayList[which]!!))
                }.show()
        }

        14 -> {
            val mTimePicker: TimePickerDialog
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            mTimePicker = TimePickerDialog(
                this,
                R.style.TimeDialogTheme,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        val datetime = Calendar.getInstance()
                        datetime[Calendar.HOUR_OF_DAY] = hourOfDay
                        datetime[Calendar.MINUTE] = minute
                        val strHrsToShow =
                            if (datetime.get(Calendar.HOUR) === 0) "12" else Integer.toString(
                                datetime.get(Calendar.HOUR)
                            )
                        var am_pm = ""
                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";
                        val time =
                            strHrsToShow + ":" + (if (minute.toString().length == 1) "0" + datetime.get(
                                Calendar.MINUTE
                            ) else datetime.get(Calendar.MINUTE)) + " " + am_pm
                        val time00 =
                            "" + hourOfDay + ":" + (if (minute.toString().length == 1) "0" + minute else minute) + ":00"
                        callBack(ItemReturn(0, time, time00))
                    }
                },
                hour,
                minute,
                false
            )
            mTimePicker.show()
        }

        15 -> {
            val list = resources.getStringArray(R.array.relation_array)
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.relationship_TypeStar))
                .setItems(list) { _, which ->
                    callBack(ItemReturn(which, list[which]))
                }.show()
        }

        16 -> {
            val list = resources.getStringArray(R.array.type_array)
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.select_your_choice))
                .setItems(list) { _, which ->
                    callBack(ItemReturn(which, list[which]))
                }.show()
        }

        17 -> {
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.select_complaint_type))
                .setItems(arrayList) { _, which ->
                    callBack(ItemReturn(which, arrayList[which]!!))
                }.show()
        }

        18 -> {
            val list = resources.getStringArray(R.array.month_year_array)
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.select_month_year))
                .setItems(list) { _, which ->
                    callBack(ItemReturn(which, list[which]))
                }.show()
        }

        19 -> {
            val list = resources.getStringArray(R.array.numbers_array)
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.choose_number))
                .setItems(list) { _, which ->
                    callBack(ItemReturn(which, list[which]))
                }.show()
        }

        20 -> {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this, R.style.TimeDialogTheme, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    callBack(ItemReturn(0, "$formattedDate", "$formattedDate"))
                    Log.e("TAG", "showDropDownDialog: $formattedDate")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
//            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
//            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis())

            datePickerDialog.show()
        }

        21 -> {
            val calendar = Calendar.getInstance()
            val mYear = calendar.get(Calendar.YEAR)
            val mMonth = calendar.get(Calendar.MONTH)
            val mDay = calendar.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(
                this,
                R.style.monthDialogStyle,
                object : OnDateSetListener {
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        monthOfYear: Int,
                        dayOfMonth: Int,
                    ) {
                        val sdf: SimpleDateFormat = SimpleDateFormat("MMM")
                        calendar.set(year, monthOfYear, dayOfMonth);
                        val dateString: String? = sdf.format(calendar.getTime())
                        callBack(ItemReturn(0, "$dateString", "$dateString"))
                    }
                },
                mYear,
                mMonth,
                mDay
            )

            dialog.getDatePicker()
                .findViewById<View?>(getResources().getIdentifier("year", "id", "android"))
                .setVisibility(
                    View.GONE
                )
            dialog.getDatePicker()
                .findViewById<View?>(getResources().getIdentifier("day", "id", "android"))
                .setVisibility(
                    View.GONE
                )
            dialog.show()
        }

        22 -> {
            val calendar = Calendar.getInstance()
            val mYear = calendar.get(Calendar.YEAR)
            val mMonth = calendar.get(Calendar.MONTH)
            val mDay = calendar.get(Calendar.DAY_OF_MONTH)
            val dialog = DatePickerDialog(
                this,
                R.style.monthDialogStyle,
                object : OnDateSetListener {
                    override fun onDateSet(
                        view: DatePicker?,
                        year: Int,
                        monthOfYear: Int,
                        dayOfMonth: Int,
                    ) {
                        val sdf: SimpleDateFormat = SimpleDateFormat("dd")
                        calendar.set(year, monthOfYear, dayOfMonth);
                        val dateString: String? = sdf.format(calendar.getTime())
                        callBack(ItemReturn(0, "$dateString", "$dateString"))
                    }
                },
                mYear,
                mMonth,
                mDay
            )

            dialog.getDatePicker()
                .findViewById<View?>(getResources().getIdentifier("year", "id", "android"))
                .setVisibility(
                    View.GONE
                )
            dialog.getDatePicker()
                .findViewById<View?>(getResources().getIdentifier("month", "id", "android"))
                .setVisibility(
                    View.GONE
                )
            dialog.show()
        }
    }
}







fun Context.loadHtml(type: Int, callBack: String.() -> Unit) {
    var inputStream: InputStream = assets.open("web/aboutUs/about.html")
    when (type) {
        1 -> {
            inputStream = when ("" + locale) {
                MainActivity.context.get()!!
                    .getString(R.string.englishVal),
                    -> assets.open("web/aboutUs/about.html")

                MainActivity.context.get()!!
                    .getString(R.string.bengaliVal),
                    -> assets.open("web/aboutUs/about_bangla.html")

                MainActivity.context.get()!!
                    .getString(R.string.gujaratiVal),
                    -> assets.open("web/aboutUs/about_gujrati.html")

                MainActivity.context.get()!!
                    .getString(R.string.hindiVal),
                    -> assets.open("web/aboutUs/about_hindi.html")

                MainActivity.context.get()!!
                    .getString(R.string.kannadaVal),
                    -> assets.open("web/aboutUs/about_kannada.html")

                MainActivity.context.get()!!
                    .getString(R.string.malayalamVal),
                    -> assets.open("web/aboutUs/about_malyalam.html")

                MainActivity.context.get()!!
                    .getString(R.string.marathiVal),
                    -> assets.open("web/aboutUs/about_marathi.html")

                MainActivity.context.get()!!
                    .getString(R.string.punjabiVal),
                    -> assets.open("web/aboutUs/about_punjabi.html")

                MainActivity.context.get()!!
                    .getString(R.string.tamilVal),
                    -> assets.open("web/aboutUs/about_tamil.html")

                MainActivity.context.get()!!
                    .getString(R.string.teluguVal),
                    -> assets.open("web/aboutUs/about_telugu.html")

                MainActivity.context.get()!!
                    .getString(R.string.assameseVal),
                    -> assets.open("web/aboutUs/about_assamese.html")

                MainActivity.context.get()!!
                    .getString(R.string.urduVal),
                    -> assets.open("web/aboutUs/about_urdu.html")

                else -> assets.open("web/aboutUs/about_telugu.html")
            }
        }

        2 -> {
            inputStream = when ("" + locale) {
                MainActivity.context.get()!!
                    .getString(R.string.englishVal),
                    -> assets.open("web/privacyPolicy/privacy.html")

                MainActivity.context.get()!!
                    .getString(R.string.bengaliVal),
                    -> assets.open("web/privacyPolicy/privacy_bangla.html")

                MainActivity.context.get()!!
                    .getString(R.string.gujaratiVal),
                    -> assets.open("web/privacyPolicy/privacy_gujrati.html")

                MainActivity.context.get()!!
                    .getString(R.string.hindiVal),
                    -> assets.open("web/privacyPolicy/privacy_hindi.html")

                MainActivity.context.get()!!
                    .getString(R.string.kannadaVal),
                    -> assets.open("web/privacyPolicy/privacy_kannada.html")

                MainActivity.context.get()!!
                    .getString(R.string.malayalamVal),
                    -> assets.open("web/privacyPolicy/privacy_malyalam.html")

                MainActivity.context.get()!!
                    .getString(R.string.marathiVal),
                    -> assets.open("web/privacyPolicy/privacy_marathi.html")

                MainActivity.context.get()!!
                    .getString(R.string.punjabiVal),
                    -> assets.open("web/privacyPolicy/privacy_punjabi.html")

                MainActivity.context.get()!!
                    .getString(R.string.tamilVal),
                    -> assets.open("web/privacyPolicy/privacy_tamil.html")

                MainActivity.context.get()!!
                    .getString(R.string.teluguVal),
                    -> assets.open("web/privacyPolicy/privacy_telugu.html")

                MainActivity.context.get()!!
                    .getString(R.string.assameseVal),
                    -> assets.open("web/privacyPolicy/privacy_assamese.html")

                MainActivity.context.get()!!
                    .getString(R.string.urduVal),
                    -> assets.open("web/privacyPolicy/privacy_urdu.html")

                else -> assets.open("web/privacyPolicy/privacy.html")
            }
        }

        3 -> {
            inputStream = when ("" + locale) {
                MainActivity.context.get()!!
                    .getString(R.string.englishVal),
                    -> assets.open("web/termsConditions/terms.html")

                MainActivity.context.get()!!
                    .getString(R.string.bengaliVal),
                    -> assets.open("web/termsConditions/terms_bangla.html")

                MainActivity.context.get()!!
                    .getString(R.string.gujaratiVal),
                    -> assets.open("web/termsConditions/terms_gujrati.html")

                MainActivity.context.get()!!
                    .getString(R.string.hindiVal),
                    -> assets.open("web/termsConditions/terms_hindi.html")

                MainActivity.context.get()!!
                    .getString(R.string.kannadaVal),
                    -> assets.open("web/termsConditions/terms_kannada.html")

                MainActivity.context.get()!!
                    .getString(R.string.malayalamVal),
                    -> assets.open("web/termsConditions/terms_malyalam.html")

                MainActivity.context.get()!!
                    .getString(R.string.marathiVal),
                    -> assets.open("web/termsConditions/terms_marathi.html")

                MainActivity.context.get()!!
                    .getString(R.string.punjabiVal),
                    -> assets.open("web/termsConditions/terms_punjabi.html")

                MainActivity.context.get()!!
                    .getString(R.string.tamilVal),
                    -> assets.open("web/termsConditions/terms_tamil.html")

                MainActivity.context.get()!!
                    .getString(R.string.teluguVal),
                    -> assets.open("web/termsConditions/terms_telugu.html")

                MainActivity.context.get()!!
                    .getString(R.string.assameseVal),
                    -> assets.open("web/termsConditions/terms_assamese.html")

                MainActivity.context.get()!!
                    .getString(R.string.urduVal),
                    -> assets.open("web/termsConditions/terms_urdu.html")

                else -> assets.open("web/termsConditions/terms.html")
            }
        }
    }

    val size: Int = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    val str = String(buffer)
    callBack(str)
}


fun String.orderId(): String {
    return if (this.length > 3) this.substring(
        4,
        this.length
    ) + "-" + SimpleDateFormat(
        "yyMMdd-HHmmss",
        Locale.ENGLISH
    ).format(Calendar.getInstance().time) else ""
}

fun dateTime(): String {
    return SimpleDateFormat(
        "dd-MM-yyyy HH:mm:ss",
        Locale.ENGLISH
    ).format(Calendar.getInstance().time)
}


fun gen(): Int {
    val r = Random(System.currentTimeMillis())
    return 1000000000 + r.nextInt(2000000000)
}


fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
    var drawable = ContextCompat.getDrawable(context!!, drawableId)
    if (SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = DrawableCompat.wrap(drawable!!).mutate()
    }
    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}


fun saveImageInQ(context: Context?, bitmap: Bitmap): Uri {
    val filename = getImageName()
    var fos: OutputStream? = null
    var imageUri: Uri? = null
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        put(MediaStore.Video.Media.IS_PENDING, 1)
    }

    val contentResolver = context!!.contentResolver
    contentResolver.also { resolver ->
        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = imageUri?.let { resolver.openOutputStream(it) }
    }
    fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }
    contentValues.clear()
    contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
    contentResolver.update(imageUri!!, contentValues, null, null)
    return imageUri!!
}


fun getFormatedStringFromDays(value: Int, type: Int, requireContext: Context): String {
    val year = value / 365
    val month = (value % 365) / 30
    val week = ((value % 365) % 30) / 7
    val day = ((value % 365) % 30) % 7
    var totalValues = ""

    if (type == 1) {
        if (year == 1) {
            totalValues += "$year ${requireContext.getString(R.string._year)} "
        } else if (year >= 2) {
            totalValues += "$year ${requireContext.getString(R.string._years)} "
        }

        if (month == 1) {
            totalValues += "$month ${requireContext.getString(R.string._month)} "
        } else if (month >= 2) {
            totalValues += "$month ${requireContext.getString(R.string._months)} "
        }

        if (week == 1) {
            totalValues += "$week ${requireContext.getString(R.string._week)} "
        } else if (week >= 2) {
            totalValues += "$week ${requireContext.getString(R.string._weeks)} "
        }

        if (day == 1) {
            totalValues += "$day ${requireContext.getString(R.string._day)} "
        } else if (day >= 2) {
            totalValues += "$day ${requireContext.getString(R.string._days)} "
        }
    } else if (type == 2) {
        if (year == 1) {
            totalValues += "$year Year "
        } else if (year >= 2) {
            totalValues += "$year Years "
        }

        if (month == 1) {
            totalValues += "$month Month "
        } else if (month >= 2) {
            totalValues += "$month Months "
        }

        if (week == 1) {
            totalValues += "$week Week "
        } else if (week >= 2) {
            totalValues += "$week Weeks "
        }

        if (day == 1) {
            totalValues += "$day Day"
        } else if (day >= 2) {
            totalValues += "$day Days"
        }
    }
    return totalValues
}


fun getNumberToWord(amount: String, type: Int, requireContext: Context): String {
    var totalValues = ""
//    var sss = Locale.Builder().setLanguageTag("en").build()
//    val string = java.lang.String.format(Locale.ENGLISH, amount, 6)
//    Log.e("TAG", "aaQQ11 "+string)
    if (amount.toString().contains(".")) {
        val aa = amount.toString().split(".")[0]
        val bb = amount.toString().split(".")[1]

//        Log.e("TAG", "aaQQ "+aa)
//        Log.e("TAG", "bbQQ "+bb)

        if (aa.toDouble() > 0) {
            val return_val_in_english1 = EnglishNumberToWords.convertToIndianCurrency(aa)
            if (type == 1) {
                totalValues += return_val_in_english1 + " " + requireContext.resources.getString(R.string._rupees) + " "
            } else if (type == 2) {
                totalValues += return_val_in_english1 + " Rupees "
            }
        }
        if (bb.toDouble() > 0) {
            val return_val_in_english2 = EnglishNumberToWords.convertToIndianCurrency(bb)
            if (type == 1) {
                totalValues += "And "
                totalValues += return_val_in_english2 + " " + requireContext.resources.getString(R.string._paise)
            } else if (type == 2) {
                totalValues += "And "
                totalValues += return_val_in_english2 + " Paise"
            }
        }
    } else {
        val aa = amount.toString()
        if (aa.toDouble() > 0) {
            val return_val_in_english1 = EnglishNumberToWords.convertToIndianCurrency(aa)
            if (type == 1) {
                totalValues += return_val_in_english1 + " " + requireContext.resources.getString(R.string._rupees)
            } else if (type == 2) {
                totalValues += return_val_in_english1 + " Rupees"
            }
        }
    }
    if (type == 1) {
        totalValues += " " + requireContext.resources.getString(R.string._only)
    } else if (type == 2) {
        totalValues += " Only"
    }

    return totalValues
}


fun getDateToLongTime(amount: String): Long {
    val current1 =
        LocalDate.parse(amount, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
    val millis1: Long = SimpleDateFormat("yyyy-MM-dd").parse(current1.toString())?.time ?: 0
    return millis1
}

fun getDateToLongTimeNow(): Long {
    val current1 =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
    val millis1: Long = SimpleDateFormat("yyyy-MM-dd").parse(current1.toString())?.time ?: 0
    return millis1
}


fun checkCameraLocationPermission(context: Context?): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.checkSelfPermission(
            context!!,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        ActivityCompat.checkSelfPermission(
            context!!,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}

/**
 * Request Camera & Location Permission
 */
fun requestCameraLocationPermission(context: Context?, requestCode: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.requestPermissions(
            (context as Activity?)!!, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), requestCode
        )
    } else {
        ActivityCompat.requestPermissions(
            (context as Activity?)!!, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), requestCode
        )
    }
}

//fun Context.getAddress(latLng: LatLng) : String{
//    if (latLng != null){
//        val geocoder = Geocoder(this, Locale.getDefault())
//        val addresses: List<Address>?
//        val address: Address?
//        var fulladdress = ""
//        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
//
//        if (addresses!!.isNotEmpty()) {
//            address = addresses[0]
//            fulladdress = address.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex
//            var city = address.getLocality();
//            var state = address.getAdminArea();
//            var country = address.getCountryName();
//            var postalCode = address.getPostalCode();
//            var knownName = address.getFeatureName(); // Only if available else return NULL
//            return fulladdress.toString()
//        } else{
//            fulladdress = "Location not found"
//        }
//    }
//    return ""
//}


suspend fun Context.getAddress(latLng: LatLng) : String{
    if (latLng != null){
        val geocoder = Geocoder(this, Locale.getDefault())

        var address = geocoder.getAddressFrom(latLng.latitude, latLng.longitude)

//        val addresses: List<Address>?
//        val address: Address?
        var fulladdress = ""
//        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (address != null) {
            fulladdress = address.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex
//            var city = address.getLocality();
//            var state = address.getAdminArea();
//            var country = address.getCountryName();
//            var postalCode = address.getPostalCode();
//            var knownName = address.getFeatureName(); // Only if available else return NULL
            return fulladdress.toString()
        } else{
            fulladdress = "Location not found"
        }
    }
    return ""
}



private suspend fun Geocoder.getAddressFrom(
    latitude: Double,
    longitude: Double,
): Address? = withContext (Dispatchers.IO) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCoroutine { cont ->
                getFromLocation(latitude, longitude, 1) {
                    cont.resume(it.firstOrNull())
                }
            }
        } else {
            suspendCoroutine { cont ->
                @Suppress("DEPRECATION")
                val address = getFromLocation(latitude, longitude, 1)?.firstOrNull()
                cont.resume(address)
            }
        }
    } catch (e: Exception) {
        null
    }
}


fun String.getNotNullData() : String{
    return if (this.isNullOrEmpty()) "" else this
}

fun Int.getNotNullData() : String{
    return if (this.toString().isNullOrEmpty()) "" else ""+this
}

fun RecyclerView.isLastItemDisplaying(): Boolean {
    if (this.adapter!!.itemCount != 0) {
        val lastVisibleItemPosition =
            (this.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
        if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == this.adapter!!
                .itemCount - 1
        ) return true
    }
    return false
}


fun getAbbreviatedFromDateTime(dateTime: Calendar, field: String): String? {
    val output = SimpleDateFormat(field)
    try {
        return output.format(dateTime.time)    // format output
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

fun String.getMonthFromHindi() : String{
    var name : String = ""
    if (this == "जन"){
        name = "Jan"
    } else if (this == "फ़र"){
        name = "Feb"
    } else if (this == "मार्च"){
        name = "Mar"
    } else if (this == "अप्रै"){
        name = "Apr"
    } else if (this == "मई"){
        name = "May"
    } else if (this == "जून"){
        name = "Jun"
    } else if (this == "जुला"){
        name = "Jul"
    } else if (this == "अग"){
        name = "Aug"
    } else if (this == "सितं"){
        name = "Sep"
    } else if (this == "अक्टू"){
        name = "Oct"
    } else if (this == "नवं"){
        name = "Nov"
    } else if (this == "दिसं"){
        name = "Dec"
    } else {
        name = this
    }
    return name
}

fun isLocationEnabled(context: Context): Boolean {
    val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}


fun showGPSNotEnabledDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.enable_gps))
        .setMessage(context.getString(R.string.required_for_this_app))
        .setCancelable(false)
        .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        .show()
}


@SuppressLint("SuspiciousIndentation")
fun Activity.callPermissionDialogGPS(callBack: Intent.() -> Unit) {
    MaterialAlertDialogBuilder(this, R.style.LogoutDialogTheme)
        .setTitle(resources.getString(R.string.enable_gps))
        .setMessage(resources.getString(R.string.required_for_this_app))
        .setPositiveButton(resources.getString(R.string.enable_now)) { dialog, _ ->
            dialog.dismiss()
            callBack(Intent().apply {
                action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            })
        }
        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        .setCancelable(false)
        .show()
}


//@RequiresApi(Build.VERSION_CODES.TIRAMISU)
//suspend fun Context.getCityStateName(location : Location?) : String? =
//    (suspendCancellableCoroutine {  })<String?> { cancellableContinuation ->
//        location?.let { loc ->
//            Geocoder(this).getFromLocation(
//                loc.latitude, loc.longitude, 1
//            ) { list -> // Geocoder.GeocodeListener
//                list.firstOrNull()?.let { address ->
//                    cancellableContinuation.resumeWith(
//                        Result.success(
//                            "${address.locality} ${address.adminArea}"
//                        )
//                    )
//                }
//            }
//        }
//    }

//Nikshay Patra - Hypro



//getExternalFilesDir() Android/data/data/your_package/
//getExternalStorageDirectory() mnt/sdcard/
//getExternalCacheDir()
//getExternalStoragePublicDirectory  Environment.DIRECTORY_DOWNLOADS
//getExternalMediaDirs()

