package com.lifeapp.ui.dev

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lifeapp.data.entity.Project
import com.lifeapp.databinding.ItemProjectBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class ProjectAdapter(
    private val scope: CoroutineScope,
    private val getStats: suspend (Long) -> Pair<Int, Int>,
    private val onClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    private var items: List<Project> = emptyList()

    fun submitList(list: List<Project>) {
        items = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemProjectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = items[position]
        holder.binding.tvProjectName.text = project.name
        holder.binding.root.setOnClickListener { onClick(project) }
        scope.launch {
            val (completed, total) = getStats(project.id)
            val percent = if (total > 0) (completed * 100 / total) else 0
            holder.binding.progressProject.progress = percent
            holder.binding.tvProjectStats.text = "已完成 $completed / 共 $total 项"
        }
    }

    override fun getItemCount() = items.size
}
