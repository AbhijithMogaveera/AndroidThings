package com.example.androidthings

import androidx.lifecycle.ViewModel
import com.example.androidthings.things.Thing

class MainActivityViewModel: ViewModel() {
    val things = listOf(
        Thing("Notifications")
    )
}