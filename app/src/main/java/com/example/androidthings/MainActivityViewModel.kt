package com.example.androidthings

import androidx.lifecycle.ViewModel
import com.example.androidthings.things.Thing

class MainActivityViewModel: ViewModel() {
    val NotificationsThing = Thing("Notifications")
    val things = listOf(
        NotificationsThing
    )
}