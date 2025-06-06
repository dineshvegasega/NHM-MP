package com.nhm.distributor.networking

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import com.nhm.distributor.utils.ioThread
import com.nhm.distributor.utils.mainDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

/**
 * Connectivity Manager
 * */
class ConnectivityManager(context: Context) : LiveData<Boolean>() {

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()


    /**
     * Initializer
     * */
    init {
        ioThread {
            delay(500)
            withContext(mainDispatcher) {
                this@ConnectivityManager.checkValidNetworks()
            }
        }
    }

    /**
     * Check Valid Network
     * */
    private fun checkValidNetworks() {
        postValue(validNetworks.size > 0)
    }


    /**
     * On Active
     * */
    override fun onActive() {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }


    /**
     * On Inactive
     * */
    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


    /**
     * Create Network Callback
     * */
    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        /**
         * On Available Network
         * */
        override fun onAvailable(network: Network) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val hasInternetCapability =
                networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

            if (hasInternetCapability == true) {
                // Check if this network actually has internet
                ioThread {
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    if (hasInternet) {
                        withContext(mainDispatcher) {
                            validNetworks.add(network)
                            checkValidNetworks()
                        }
                    }
                }
            }
        }

        /**
         * On Lost Network
         * */
        override fun onLost(network: Network) {
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }

    /**
     * DOes Network Have Internet
     * */
    object DoesNetworkHaveInternet {
        /**
         * Execute Socket Factory
         * */
        fun execute(socketFactory: SocketFactory): Boolean {
            return try {
                val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
                socket.connect(InetSocketAddress(PING, 53), 1500)
                socket.close()
                true
            } catch (e: IOException) {
                false
            }
        }
    }
}