package com.example.swipeassignment

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.swipeassignment.di.modules.appModule
import com.example.swipeassignment.notification.SwipeNotificationImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SwipeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //Create notification channel
        createNotificationChannel()

        //Start Koin for dependency injection
        startKoin {
            modules(appModule)
            androidContext(this@SwipeApplication)
        }



    }

    private fun createNotificationChannel(){
        // Notification channel is only needed in and above Android oreo
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                SwipeNotificationImpl.ADD_PRODUCT_CHANNEL_ID,
                getString(R.string.add_product_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = getString(R.string.add_product_notification_channel_description)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}