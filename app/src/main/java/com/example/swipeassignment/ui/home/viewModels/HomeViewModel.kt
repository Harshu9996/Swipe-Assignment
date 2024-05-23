package com.example.swipeassignment.ui.home.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipeassignment.connectivity.ConnectivityObserver
import com.example.swipeassignment.domain.Repository
import com.example.swipeassignment.domain.PostResponse
import com.example.swipeassignment.domain.ProductItem
import com.example.swipeassignment.domain.Result
import com.example.swipeassignment.notification.SwipeNotification
import com.example.swipeassignment.ui.home.events.Events
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository,
    private val swipeNotification: SwipeNotification
): ViewModel() {

    val TAG = "HomeViewModel"


    private lateinit var productsHolder:List<ProductItem>

    private val _products = MutableLiveData<Result<List<ProductItem>>>()
    val products :LiveData<Result<List<ProductItem>>>
        get() = _products

    private val _addProductStatus = MutableLiveData<Result<PostResponse>>()
    val addProductStatus :LiveData<Result<PostResponse>>
        get() = _addProductStatus

    private val _connectivityStatus = MutableLiveData<ConnectivityObserver.Status>()
    val connectivityStatus: LiveData<ConnectivityObserver.Status>
        get() = _connectivityStatus

    init {
        fetchProducts()
        repository.observeConnectivity().onEach {status->
            if (_products.value?.data?.isEmpty() == true && status==ConnectivityObserver.Status.Available){
                //This edge case will handle and fetch data when user starts the app in offline mode
                //Here the products will be fetched as soon as the users become online
                fetchProducts()
            }
            if(status!=ConnectivityObserver.Status.Available){
                //Send network status
                _connectivityStatus.postValue(status)

            }
        }.launchIn(viewModelScope)
    }


    fun onEvent(event : Events){
        //Handle events
        when(event){
            is Events.AddProduct->{
                addProduct(event.productItem)

            }
            is Events.FetchDataAgain->{
                fetchProducts()
            }
            is Events.search->{

                val query = event.query
                if(query.isNullOrEmpty()){

                    //Query is empty restore and show all products
                    val newProducts = Result.Success(data = productsHolder)
                    _products.value = newProducts!!

                }else{

                    //Filter the products
                    val newProducts = _products.value
                    newProducts?.data = _products.value?.data?.filter {
                        it.doesMatchSearchQuery(event.query)
                    }
                    _products.value = newProducts!!
                }




            }
            else->{
                Log.d(TAG, "onEvent: No Matching event")
            }
        }
    }


    private fun fetchProducts(){
        viewModelScope.launch {
            _products.postValue(Result.Loading())
            val response = repository.getProducts()
            _products.postValue(response)
            when(response){
                is Result.Success->{
                    productsHolder = response.data!!
                }
                else->{
                    //Handle remaining cases
                    //In our case we can leave it empty
                }
            }
        }
    }
    private fun addProduct(productItem:ProductItem){
        viewModelScope.launch {
            _addProductStatus.postValue(Result.Loading())
            Log.d(TAG, "addProduct: product data = "+productItem)
            val response = repository.addProduct(productItem)
            _addProductStatus.postValue(response)


            //Send Notification
            when(response){
                is Result.Success->{
                    if(response.data!=null){
                        val message = response.data!!.product_details.product_name + " added successfully."
                        val title = "Product added successfully"
                        swipeNotification.showNotification(message = message, title = title)
                        //Refresh Product lists
                        fetchProducts()
                        Log.d(TAG, "addProduct: called fetchProducts")
                    }

                }
                is Result.Error->{
                    Log.d(TAG, "addProduct: error = "+response.message)
                    val message = "Something went wrong"
                    val title = "Adding product failed"
                    swipeNotification.showNotification(message = message, title = title)
                }
                else->{
                    //Handle remaining cases
                    //In Our case no need to handle as we just have to show feedback after action completion
                    Log.d(TAG, "addProduct: No Matching event currently")
                }
            }

        }

    }


}