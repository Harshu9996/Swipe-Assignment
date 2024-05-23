package com.example.swipeassignment.notification


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.swipeassignment.MainActivity
import com.example.swipeassignment.R
import com.example.swipeassignment.domain.ProductItem

class SwipeNotificationImpl(
   private val context:Context
) : SwipeNotification {
    //Implementation of Notification interface
    val TAG = "SwipeNotification"

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    //Below function shows notification for the given message and title
    override fun showNotification(message: String, title: String){


        val intentToHome = Intent(context,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context,1,intentToHome,
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)


        val notification = NotificationCompat.Builder(context, ADD_PRODUCT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1,notification)
    }

    companion object{
        const val ADD_PRODUCT_CHANNEL_ID = "add_product_channel"
    }
}