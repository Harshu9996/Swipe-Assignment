package com.example.swipeassignment.data.api

import com.example.swipeassignment.domain.PostResponse
import com.example.swipeassignment.domain.ProductItem
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiDAO {
    //Data access object for fetching and posting data to API end points

    @GET("get")
    suspend fun getProducts() : Response<List<ProductItem>>

    @Multipart
    @POST("add")
    suspend fun addProduct(@Part("product_name") productName: RequestBody,
                           @Part("product_type") productType: RequestBody,
                           @Part("price") price: RequestBody,
                           @Part("tax") tax: RequestBody) : Response<PostResponse>


}