package com.example.swipeassignment.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    //interface to access network connectivity status

    //Observe the status
    fun observe(): Flow<Status>

    //Get a one time status information
    fun getCurrentStatus(): ConnectivityObserver.Status

    enum class Status{
        Available,Unavailable,Losing,Lost
    }

}