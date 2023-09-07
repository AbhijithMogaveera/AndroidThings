package com.example.androidthings.things

import androidx.recyclerview.widget.RecyclerView
import com.example.androidthings.databinding.ThingsViewHolderBinding

class ThingsViewHolder(
    val binding: ThingsViewHolderBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(thing: Thing, onClick: (Thing) -> Unit) {
        binding.tvThingName.text = thing.name
        binding.root.setOnClickListener { onClick(thing) }
    }
}