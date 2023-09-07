package com.example.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notifications.databinding.ActivityNotificationBinding
import com.example.notifications.screens.MissingNotificationFragment
import com.example.notifications.screens.NotificationSampleFragment
import com.google.android.material.snackbar.Snackbar

class NotificationActivityViewModel() : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val permission = Manifest.permission.POST_NOTIFICATIONS
    val requestCode = 100

}

class NotificationActivity : AppCompatActivity() {


    private lateinit var binding: ActivityNotificationBinding

    private val viewModel by lazy {
        ViewModelProvider(this)[NotificationActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isNotificationPermissionIsGranted =
                ContextCompat.checkSelfPermission(
                    this,
                    viewModel.permission
                ) == PackageManager.PERMISSION_GRANTED
            if (isNotificationPermissionIsGranted) {
                onPermissionGranted()
            } else {
                supportFragmentManager.beginTransaction().apply {
                    replace(binding.container.id, MissingNotificationFragment::class.java, null)
                }.commit()
            }
        }else{
            onPermissionGranted()
        }


        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Hii:)", Snackbar.LENGTH_LONG)
                .setAction( "Hello")  { }.show()
        }
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
        if (requestCode == viewModel.requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted()
            }
        }
    }
}