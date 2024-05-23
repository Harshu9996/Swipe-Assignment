package com.example.swipeassignment.ui.home.events

import com.example.swipeassignment.domain.ProductItem

sealed class Events {
    //Define User Events
    data class AddProduct(val productItem: ProductItem): Events()
    data object FetchDataAgain : Events()
    data class search(val query:String) : Events()

    //Can accommodate more events according to the need

}