package com.example.androidthings.things

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidthings.databinding.ThingsViewHolderBinding

class ThingsAdapter(
    val things: List<Thing>,
    val onClick: (Thing) -> Unit
) : RecyclerView.Adapter<ThingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThingsViewHolder {
        return ThingsViewHolder(
            ThingsViewHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return things.size
    }

    override fun onBindViewHolder(holder: ThingsViewHolder, position: Int) {
        holder.bind(things[position], onClick = onClick)
    }
}