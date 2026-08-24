package com.lifeapp.ui.home

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lifeapp.data.entity.Todo
import com.lifeapp.databinding.ItemTodoBinding

class TodoAdapter(
    private val onToggle: (Todo) -> Unit
) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    private var items: List<Todo> = emptyList()

    fun submitList(list: List<Todo>) {
        items = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = items[position]
        holder.binding.tvTodoContent.text = todo.content
        holder.binding.cbTodo.isChecked = todo.isCompleted
        if (todo.isCompleted) {
            holder.binding.tvTodoContent.paintFlags = holder.binding.tvTodoContent.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.binding.tvTodoContent.paintFlags = holder.binding.tvTodoContent.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        val colorRes = when (todo.priority) {
            0 -> android.R.color.holo_red_dark
            1 -> android.R.color.holo_orange_dark
            else -> android.R.color.darker_gray
        }
        holder.binding.vPriority.setBackgroundResource(colorRes)
        holder.binding.cbTodo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != todo.isCompleted) {
                onToggle(todo.copy(isCompleted = isChecked))
            }
        }
    }

    override fun getItemCount() = items.size
}
