package com.nhm.distributor.screens.main.webPage

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.DownloadListener
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.nhm.distributor.R
import com.nhm.distributor.databinding.WebpageBinding
import com.nhm.distributor.networking.LoginOtp
import com.nhm.distributor.networking.LoginPassword
import com.nhm.distributor.networking.Screen
import com.nhm.distributor.networking.WEB_URL
import com.nhm.distributor.screens.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.io.encoding.ExperimentalEncodingApi


@AndroidEntryPoint
class WebPage : Fragment() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        private var _binding: WebpageBinding? = null
        private val binding get() = _binding!!
        @SuppressLint("StaticFieldLeak")
        lateinit var navController: NavController
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()){
                    binding.webView.goBack()
                }
                else {
                    view?.findNavController()?.navigateUp()
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = WebpageBinding.inflate(inflater)
        return binding.root
    }




    var urlLoad = ""

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()
        MainActivity.mainActivity.get()?.callFragment(0)


        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                return super.shouldInterceptRequest(view, request)
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.GONE
                //  view?.loadUrl(getString(R.string.url))
//                view?.loadUrl("javascript:docWrite('" + data + "')")
//                 view?.loadUrl("javascript:docWrite2('fsdfasfg')");
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                binding.progressBar.visibility = View.GONE
            }
        }

        val screen = arguments?.getString(Screen)
        Handler(Looper.getMainLooper()).postDelayed({
            if (screen == LoginPassword){
                binding.webView.loadUrl(WEB_URL+"#/mobile-login")
//                binding.webView.loadUrl("https://amritmahotsav.nic.in/downloads.htm")
            } else if (screen == LoginOtp){
                binding.webView.loadUrl(WEB_URL+"#/mobile-otp-login")
            } else if (screen == LoginOtp){
                binding.webView.loadUrl(WEB_URL+"#/privacy-policy")
            } else if (screen == LoginOtp){
                binding.webView.loadUrl(WEB_URL+"#/term-condition")
            } else if (screen == LoginOtp){
                binding.webView.loadUrl(WEB_URL+"#/term-condition")
            }
        }, 200)



        Handler(Looper.getMainLooper()).postDelayed({
            binding.webView.addJavascriptInterface(WebAppInterface(requireContext(), binding), "Android")
        }, 200)



        binding.webView.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            if (Build.VERSION.SDK_INT >= 33) {
                urlLoad = url
                pushNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                if (url.startsWith("data:")) {
                    val path: String = createAndSaveFileFromBase64Url(url)
                }
            }
        })

    }


    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
//            Log.e("TAG", "AAAAgranted " + granted)
            if (urlLoad.startsWith("data:")) {
                val path: String = createAndSaveFileFromBase64Url(urlLoad)
            }
        } else {
//            Log.e("TAG", "BBBBgranted " + granted)
        }

    }

        @SuppressLint("MutableImplicitPendingIntent")
    @OptIn(ExperimentalEncodingApi::class)
    fun createAndSaveFileFromBase64Url(url: String): String {
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val filetype = url.substring(url.indexOf("/") + 1, url.indexOf(";"))
        val filename = System.currentTimeMillis().toString() + "." + filetype
        val file = File(path, filename)
        try {
            if (!path.exists()) path.mkdirs()
            if (!file.exists()) file.createNewFile()
            val base64EncodedString = url.substring(url.indexOf(",") + 1)
//            val decodedBytes = Base64.decode(base64EncodedString, Base64.DEFAULT)
            val decodedBytes = android.util.Base64.decode(base64EncodedString, android.util.Base64.DEFAULT);
//            android.util.Base64.decode(url, android.util.Base64.DEFAULT)
            val os: OutputStream = FileOutputStream(file)
            os.write(decodedBytes)
            os.close()
            val intent = Intent(Intent.ACTION_VIEW)

//            Log.e("TAG", "fileZZ "+file.toString())

//            val data =
//                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file)

            val data= if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file)
            }else{
                val imagePath: File = File(file.absolutePath)
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", imagePath)
            }

            intent.setDataAndType(data, "image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // startActivity(intent)

            val pendingIntent= if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT)
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(requireContext(),0,intent,PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getActivity(requireContext(),0,intent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
            }


            val CHANNEL_ID="my_channel_01" // The id of the channel.
            val name: CharSequence="Download" // The user-visible name of the channel.
            val importance=NotificationManager.IMPORTANCE_HIGH
            var mChannel: NotificationChannel?=null
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel=NotificationChannel(CHANNEL_ID,name,importance)
                mChannel.enableLights(true)
                mChannel.lightColor= Color.WHITE
                mChannel.setShowBadge(true)
                mChannel.lockscreenVisibility=Notification.VISIBILITY_PUBLIC
            }

            val notification=NotificationCompat.Builder(requireContext(),CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) //.setLargeIcon(icon)
//                .setStyle("bigText")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(getString(R.string.app_name))
                .setAutoCancel(true)
                .setChannelId(CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentText(file.name).build()
            val notificationManager=
                requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) notificationManager.createNotificationChannel(
                mChannel!!
            )
            notificationManager.notify(0,notification)

        } catch (e: IOException) {
            Log.w("ExternalStorage", "Error writing $file", e)
        }
        return file.toString()
    }





    class WebAppInterface(private val mContext: Context, var bind: WebpageBinding) {
        /** Show a toast from the web page.  */
        @JavascriptInterface
        fun showToast(toast: String) {
            Toast.makeText(mContext, "4"+toast, Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun redirectToForgetPage(toast: String) {
            Toast.makeText(mContext, "5"+toast, Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun redirectToMainPage(toast: String) {
            // Toast.makeText(mContext, "1"+toast, Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).post(Thread {
                MainActivity.activity.get()?.runOnUiThread {
                    //navController.navigate(R.id.action_webPage_to_onboard)
                    navController.navigateUp()
                }
            })
        }

        @JavascriptInterface
        fun redirectToRegisterPage(toast: String) {
//            Toast.makeText(mContext, "2"+toast, Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).post(Thread {
                MainActivity.activity.get()?.runOnUiThread {
                    navController.navigate(R.id.action_webPage_to_register)
                }
            })

        }

        @JavascriptInterface
        fun redirectToQucikRegisterPage(toast: String) {
//            Toast.makeText(mContext, "3"+toast, Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).post(Thread {
                MainActivity.activity.get()?.runOnUiThread {
                    navController.navigate(R.id.action_webPage_to_QuickRegister)
                }
            })

        }

        @JavascriptInterface
        fun redirectToOTPLogin(toast: String) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        }


        @JavascriptInterface
        fun sendWebLinkToAndroid(toast: String) {
//            Log.e("TAG", "sendWebLinkToAndroid "+toast)
            Handler(Looper.getMainLooper()).post(Thread {
                MainActivity.activity.get()?.runOnUiThread {
//                    Toast.makeText(mContext, "AA1"+toast, Toast.LENGTH_SHORT).show()
                    toast?.let {
                        val webIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(toast)
                        )
                        try {
                            binding.root.context.startActivity(webIntent)
                        } catch (ex: ActivityNotFoundException) {
                            //binding.root.context.startActivity(webIntent)
                        }
                    }
                }
            })
        }


        @JavascriptInterface
        fun sendVideoLinkToAndroid(toast: String) {
//            Log.e("TAG", "sendVideoLinkToAndroid "+toast)
            Handler(Looper.getMainLooper()).post(Thread {
                MainActivity.activity.get()?.runOnUiThread {
                    toast?.let {
                        val webIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(toast)
                        )
                        try {
                            binding.root.context.startActivity(webIntent)
                        } catch (ex: ActivityNotFoundException) {
                            //binding.root.context.startActivity(webIntent)
                        }
                    }
                }
            })
        }





    }

    override fun onResume() {
        super.onResume()
        binding.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)

        binding.webView.settings.setSupportZoom(false)
        val settings = binding.webView.settings
        // Enable java script in web view
        settings.javaScriptEnabled = true
        binding. webView.setPadding(0, 0, 0, 0)

        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        binding.webView.setVerticalScrollBarEnabled(false);
        binding.webView.setHorizontalScrollBarEnabled(false);

        // Enable zooming in web view
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false

        // Zoom web view text
//        settings.textZoom = 100

        // Enable disable images in web view
        settings.blockNetworkImage = false
        // Whether the WebView should load image resources
        settings.loadsImagesAutomatically = true


        // More web view settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.safeBrowsingEnabled = true  // api 26
        }

        settings.pluginState = WebSettings.PluginState.ON
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.mediaPlaybackRequiresUserGesture = false

        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.loadWithOverviewMode = true
        settings.allowContentAccess = true
        settings.setGeolocationEnabled(true)
        settings.allowFileAccess = true



        binding.webView.fitsSystemWindows = true

        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
//        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        binding.webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//        webView.setInitialScale(getScale())

        binding.webView.settings.databaseEnabled = true
        binding.webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        binding.webView.setInitialScale(30);
    }



    override fun onDestroyView() {
        binding.webView.removeAllViews()
        binding.webView.destroy()
        _binding = null
        super.onDestroyView()
    }
}