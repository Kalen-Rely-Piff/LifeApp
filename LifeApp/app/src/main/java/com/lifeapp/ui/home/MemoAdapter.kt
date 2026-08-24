package com.lifeapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lifeapp.data.entity.Memo
import com.lifeapp.databinding.ItemMemoBinding

class MemoAdapter(
    private val onSwipeDelete: (Memo) -> Unit
) : RecyclerView.Adapter<MemoAdapter.ViewHolder>() {

    private var items: List<Memo> = emptyList()

    fun submitList(list: List<Memo>) {
        items = list
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Memo = items[position]

    inner class ViewHolder(val binding: ItemMemoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvMemo.text = items[position].content
    }

    override fun getItemCount() = items.size
}
