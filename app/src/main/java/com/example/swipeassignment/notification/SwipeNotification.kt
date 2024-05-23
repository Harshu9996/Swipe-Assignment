package com.example.swipeassignment.notification

import com.example.swipeassignment.domain.ProductItem

interface SwipeNotification {
    //Interface to generate notifications

    fun showNotification(message: String, title: String)
}