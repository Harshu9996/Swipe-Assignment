package com.example.swipeassignment.di.modules

import androidx.room.Room
import com.example.swipeassignment.connectivity.ConnectivityObserver
import com.example.swipeassignment.connectivity.ConnectivityObserverImpl
import com.example.swipeassignment.domain.Repository
import com.example.swipeassignment.data.RepositoryImpl
import com.example.swipeassignment.data.api.ApiDAO
import com.example.swipeassignment.data.roomDB.RoomDB
import com.example.swipeassignment.data.roomDB.RoomDao
import com.example.swipeassignment.notification.SwipeNotification
import com.example.swipeassignment.notification.SwipeNotificationImpl
import com.example.swipeassignment.ui.home.viewModels.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//App Module to tell Koin what to create and How to create for dependency injection
val appModule = module {
    val BASE_URL = "https://app.getswipe.in/api/public/"
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiDAO::class.java)
    }
    single<RoomDB>{
        Room.databaseBuilder(
            get(),
            RoomDB::class.java, "swipe-database"
        ).fallbackToDestructiveMigration().build()
    }
    single<RoomDao>{
        val db = get<RoomDB>()
            db.getDao()
    }
    single<Repository> {
        RepositoryImpl(apiDAO = get()
            , roomDAO = get(),
            connectivityObserver = get()
        )
    }
    viewModel{
        HomeViewModel(get()
            ,get()
        )
    }
    single<SwipeNotification>{
        SwipeNotificationImpl(get())
    }
    single<ConnectivityObserver> {
        ConnectivityObserverImpl(get())
    }
}