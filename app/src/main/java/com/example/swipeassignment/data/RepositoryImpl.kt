package com.example.swipeassignment.data

import android.util.Log
import com.example.swipeassignment.connectivity.ConnectivityObserver
import com.example.swipeassignment.data.api.ApiDAO
import com.example.swipeassignment.data.roomDB.RoomDao
import com.example.swipeassignment.data.roomDB.entities.RoomProductItem
import com.example.swipeassignment.domain.PostResponse
import com.example.swipeassignment.domain.ProductItem
import com.example.swipeassignment.domain.Repository
import com.example.swipeassignment.domain.Result
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType
import okhttp3.RequestBody


class RepositoryImpl(
    private val apiDAO: ApiDAO,
    private val roomDAO: RoomDao,
    private val connectivityObserver: ConnectivityObserver
) : Repository {

    //Implementation of Repository interface
    val TAG = "RepositoryImpl"

    override suspend fun getProducts(): Result<List<ProductItem>> {

        //Apply Offline caching
        //Check the connectivity
        val connectivityStatus = connectivityObserver.getCurrentStatus()



        if(connectivityStatus==ConnectivityObserver.Status.Available){
            // fetch from api
            //Fetch data from network
            try {

                val response =  apiDAO.getProducts()

                Log.d(TAG, "getProducts: response from api = "+response.body())


                if(response.isSuccessful && response.body()!=null){
                    //Network request is successful
                    //Add data to Room for offline caching
                    val responseFromNetwork = response.body()
                    syncWithRoom(responseFromNetwork!!)
                    return Result.Success(data = responseFromNetwork!!)
                }else if (response.errorBody()!=null){
                    // An error occurred in the network call
                    return Result.Error(message = response.errorBody().toString())
                }else{
                    // Handle remaining scenarios
                    return Result.Error(message = "Something went wrong")
                }
            }catch (e:Exception){
                //If any other exception occur return failure
                return Result.Error(message = "Something went wrong")
            }
        }else{
            //First check if the data is present in Room or not
            val roomProductList = roomDAO.getProducts()
            if(roomProductList.isEmpty()){
                //Return failure
                return Result.Error(message = "Something went wrong")
            }else{
                //return from RoomDB
                val responseList = arrayListOf<ProductItem>()
                for(roomProduct in roomProductList){
                    responseList.add(roomProduct.toProductItem())
                }
                return Result.Success(data = responseList)
            }
        }





    }

    override suspend fun addProduct(productItem: ProductItem): Result<PostResponse> {

        try {

            Log.d(TAG, "addProduct: price = "+productItem.price.toString())
            Log.d(TAG, "addProduct: tax = "+productItem.tax.toString())
            Log.d(TAG, "addProduct: name = "+productItem.product_name)
            Log.d(TAG, "addProduct: type = "+productItem.product_type)
            Log.d(TAG, "addProduct: full = "+productItem)

            val name = productItem.product_name
            val productName = RequestBody.create(MediaType.parse("text/plain"), name)

            val type = productItem.product_type
            val productType = RequestBody.create(MediaType.parse("text/plain"), type)

            val price = productItem.price.toString()
            val productPrice = RequestBody.create(MediaType.parse("text/plain"), price)

            val tax = productItem.tax.toString()
            val productTax = RequestBody.create(MediaType.parse("text/plain"), tax)

            val response =  apiDAO.addProduct(productName = productName,
                productType = productType,
                price = productPrice, tax = productTax)


            if(response.isSuccessful && response.body()!=null){
                //Network request is successful
                //Add product to Room
                roomDAO.insert(productItem.toRoomProductItem(id= null))
                return Result.Success(data = response.body()!!)
            }else if (response.errorBody()!=null){
                // An error occurred in the network call
                return Result.Error(message = response.errorBody().toString())
            }else{
                // Handle remaining scenarios
                return Result.Error(message = "Something went wrong")
            }
        }catch (e:Exception){
            //If any other exception occur return failure
            Log.d(TAG, "addProduct: exception = "+e)
            return Result.Error(message = "Something went wrong")
        }
    }

    override fun observeConnectivity(): Flow<ConnectivityObserver.Status> {
        return connectivityObserver.observe()
    }

    private suspend fun syncWithRoom(responseFromNetwork:List<ProductItem>){


        try {

            val responseFromRoom = roomDAO.getProducts()
            val productsAlreadyInRoom  = mutableListOf<ProductItem>()

            responseFromRoom.forEach {
                productsAlreadyInRoom.add(it.toProductItem())
            }

            val newProducts:List<ProductItem> = responseFromNetwork-productsAlreadyInRoom
            val newProductsAsRoomEntity = mutableListOf<RoomProductItem>()
            newProducts.forEach {
                newProductsAsRoomEntity.add(it.toRoomProductItem(id = null))
            }

            roomDAO.insert(newProductsAsRoomEntity)

        }catch (e:Exception){
            Log.d(TAG, "syncWithRoom: exception: "+e)
        }
    }


}