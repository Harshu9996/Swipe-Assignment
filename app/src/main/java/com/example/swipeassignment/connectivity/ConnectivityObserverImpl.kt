package com.example.swipeassignment.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class ConnectivityObserverImpl(
    private val context:Context
) : ConnectivityObserver {

    //Implementation for ConnectivityObserver interface

    val TAG = "ConnectivityObserverImpl"

    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager



    override fun observe(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Log.d(TAG, "onAvailable: Netwrok available")

                    launch { send( ConnectivityObserver.Status.Available) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    Log.d(TAG, "onAvailable: Netwrok losing")

                    launch { send(ConnectivityObserver.Status.Losing) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Log.d(TAG, "onAvailable: Netwrok lost")

                    launch { send(ConnectivityObserver.Status.Lost) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    Log.d(TAG, "onAvailable: Netwrok unavailable")

                    launch { send(ConnectivityObserver.Status.Unavailable) }
                }

            }

            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }

    override fun getCurrentStatus(): ConnectivityObserver.Status {
        if(connectivityManager.activeNetworkInfo?.isConnected == true){
            return ConnectivityObserver.Status.Available
        }else{
            return ConnectivityObserver.Status.Unavailable
        }
    }
}