package com.example.khawlah

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.khawlah.databinding.ItemRowBinding

class equationAdapter(private val messages: ArrayList<String>): RecyclerView.Adapter<equationAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: ItemRowBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val message = messages[position]
        holder.binding.apply {
            tvEquation.text = message
        }
    }
    override fun getItemCount() = messages.size
}