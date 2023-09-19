package com.example.notifications

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.notifications.databinding.ActivityNotificationBinding
import com.example.notifications.screens.MissingNotificationFragment
import com.example.notifications.screens.NotificationSampleFragment
import com.example.notifications.services.MediaPlayerNotificationService
import com.example.notifications.viewmodel.NotificationActivityViewModel
import com.google.android.material.snackbar.Snackbar


class NotificationActivity : AppCompatActivity() {

    private var _myService: MediaPlayerNotificationService? = null
    private val myService: MediaPlayerNotificationService get() = _myService!!
    private var isBound = false

    private val serviceConnection: ServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder: MediaPlayerNotificationService.ServiceBinder =
                    service as MediaPlayerNotificationService.ServiceBinder
                _myService = binder.service
                isBound = true
            }

            override fun onServiceDisconnected(name: ComponentName) {
                _myService = null
                isBound = false
            }
        }
    }


    private lateinit var binding: ActivityNotificationBinding

    private val viewModel by lazy {
        ViewModelProvider(this)[NotificationActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNotificationBinding.inflate(layoutInflater)

        setContentView(binding.root)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = isAllPermissionGrantedForTIRAMISU()
            if (!granted) {
                onPermissionNotGranted()
                return
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val granted = isAllPermissionGrantedForP()
            if (!granted) {
                onPermissionNotGranted()
                return
            }
        }
        onPermissionGranted()

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Hii:)", Snackbar.LENGTH_LONG)
                .setAction("Hello") { }.show()
        }
    }

    private fun onPermissionNotGranted() {
        supportFragmentManager.beginTransaction().apply {
            replace(binding.container.id, MissingNotificationFragment::class.java, null)
        }.commit()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun isAllPermissionGrantedForP() = viewModel.permissionP.all {
        ContextCompat.checkSelfPermission(
            this,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isAllPermissionGrantedForTIRAMISU() = viewModel.permissionsTIRAMISU.all {
        ContextCompat.checkSelfPermission(
            this,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun onPermissionGranted() {
        supportFragmentManager.beginTransaction().apply {
            replace(binding.container.id, NotificationSampleFragment::class.java, null)
        }.commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (isAllPermissionGrantedForTIRAMISU()) {
                onPermissionGranted()
            }
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (isAllPermissionGrantedForP()) {
                onPermissionGranted()
            }
            return
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MediaPlayerNotificationService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }


    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }
}