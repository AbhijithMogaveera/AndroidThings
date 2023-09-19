package com.example.notifications.viewmodel

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel

class NotificationActivityViewModel() : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.P)
    val permissionP = arrayOf(Manifest.permission.FOREGROUND_SERVICE)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val permissionsTIRAMISU = arrayOf(Manifest.permission.POST_NOTIFICATIONS) + permissionP

    val requestCode = 100

}