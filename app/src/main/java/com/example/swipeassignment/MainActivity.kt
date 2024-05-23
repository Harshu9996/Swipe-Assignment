package com.example.swipeassignment

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.swipeassignment.ui.home.fragments.FeedbackDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Showing splash screen
        installSplashScreen()
        setContentView(R.layout.activity_main)


        //Request necessary permissions
        requestPermission()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }
    private fun requestPermission(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){

            //Request Notification Permission
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if(!isGranted){
                        //Tell the user that the feature is unavailable
                        //First checking How many times dialog has already been shown
                        //Show a maximum of 2 times to avoid spoilage of user experience
                        val sharedPref = this.getSharedPreferences(
                            getString(R.string.shared_pref_key), Context.MODE_PRIVATE)
                        val noOfTimeDialogShown = sharedPref.getInt(TIMES_DIALOG_SHOWN,0)

                        //Avoid showing dialog multiple times to preserve good user experience
                        if(noOfTimeDialogShown< MAX_TIMES_DIALOG_ALLOWED){
                            //Show Dialog
                            val title = getString(R.string.feature_unavailable)
                            val message = getString(R.string.notification_feature_unavailable)
                            MaterialAlertDialogBuilder(this)
                                .setTitle(title)
                                .setMessage(message)
                                .setPositiveButton(getString(R.string.ok)) { dialog,id ->
                                    dialog.dismiss()
                                }
                                .create()
                                .show()

                            sharedPref.edit().putInt(TIMES_DIALOG_SHOWN,noOfTimeDialogShown+1).apply()
                        }


                    }
                }




            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    //SwipeNotificationImpl will send notification successfully
                    //No need to write any code here
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this, android.Manifest.permission.POST_NOTIFICATIONS) -> {



                    val sharedPref = this.getSharedPreferences(
                        getString(R.string.shared_pref_key), Context.MODE_PRIVATE)
                    val noOfTimePermissionRequiredDialogShown = sharedPref.getInt(
                        TIMES_PERMISSION_REQUIRED_DIALOG_SHOWN,0)

                    //Avoid showing dialog multiple times to preserve good user experience
                    if(noOfTimePermissionRequiredDialogShown< MAX_TIMES_PERMISSION_REQUIRED_DIALOG_ALLOWED){
                        //Showing a dialog
                        val title = getString(R.string.permission_not_granted)
                        val message = getString(R.string.notification_permission_description)

                        MaterialAlertDialogBuilder(this)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton(getString(R.string.ok)) { dialog,id ->
                                requestPermissionLauncher.launch(
                                    android.Manifest.permission.POST_NOTIFICATIONS)

                            }.setNegativeButton(getString(R.string.cancel)){dialog, id->
                                dialog.dismiss()

                            }
                            .create()
                            .show()

                        sharedPref.edit().putInt(TIMES_PERMISSION_REQUIRED_DIALOG_SHOWN,noOfTimePermissionRequiredDialogShown+1).apply()
                    }


                }
                else -> {

                    //Asking for permission
                    requestPermissionLauncher.launch(
                        android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }




    }

    companion object{
        const val TIMES_DIALOG_SHOWN = "TimesPermissionDialogShown"
        const val MAX_TIMES_DIALOG_ALLOWED = 2
        const val TIMES_PERMISSION_REQUIRED_DIALOG_SHOWN = "TimesPermissionRequiredDialogShown"
        const val MAX_TIMES_PERMISSION_REQUIRED_DIALOG_ALLOWED = 2
    }
}