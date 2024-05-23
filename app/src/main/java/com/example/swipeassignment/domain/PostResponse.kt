package com.example.swipeassignment.domain

data class PostResponse(
    val message: String,
    val product_details: ProductItem,
    val product_id: Int,
    val success: Boolean
)