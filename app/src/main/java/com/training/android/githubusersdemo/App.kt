package com.training.android.githubusersdemo

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.training.android.githubusersdemo.helper.Utils

private val TAG = App::class.java.simpleName

class App : Application() {

    var isInternetConnected: Boolean = false
        private set

    private val connectivityManager: ConnectivityManager
        get() = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isInternetConnected = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            isInternetConnected = false
        }
    }

    override fun onCreate() {
        super.onCreate()
        Utils.Network.initialize(applicationContext)
        registerNetworkCallback()
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterNetworkCallback()
    }

    private fun registerNetworkCallback() {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}