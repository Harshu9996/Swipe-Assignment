package com.example.swipeassignment.domain

import com.example.swipeassignment.connectivity.ConnectivityObserver
import com.example.swipeassignment.domain.Result
import com.example.swipeassignment.domain.PostResponse
import com.example.swipeassignment.domain.ProductItem
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface Repository {
    //Single source to access all the data sources

    suspend fun getProducts(): Result<List<ProductItem>>
    suspend fun addProduct(productItem: ProductItem) : Result<PostResponse>
    fun observeConnectivity(): Flow<ConnectivityObserver.Status>
}