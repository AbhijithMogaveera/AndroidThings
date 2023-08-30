package com.example.androidthings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.androidthings.databinding.ActivityMainBinding
import com.example.androidthings.things.ThingsAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm  = ViewModelProvider(this)[MainActivityViewModel::class.java]
        ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            it.rvThingsList.adapter = ThingsAdapter(vm.things)
        }
    }
}