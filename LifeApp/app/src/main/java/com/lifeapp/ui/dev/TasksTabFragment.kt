package com.lifeapp.ui.dev

import androidx.lifecycle.asLiveData
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifeapp.LifeApp
import com.lifeapp.data.entity.DevTask
import com.lifeapp.databinding.FragmentTabListBinding
import com.lifeapp.databinding.ItemDevTaskBinding
import kotlinx.coroutines.launch

class TasksTabFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TaskAdapter
    private var projectId: Long = 0

    companion object {
        fun newInstance(projectId: Long) = TasksTabFragment().apply {
            arguments = Bundle().apply { putLong("project_id", projectId) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectId = arguments?.getLong("project_id") ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TaskAdapter { task ->
            val newStatus = (task.status + 1) % 3
            lifecycleScope.launch {
                LifeApp.instance.database.devTaskDao().update(task.copy(status = newStatus))
            }
        }
        binding.rvList.layoutManager = LinearLayoutManager(context)
        binding.rvList.adapter = adapter

        binding.fabAdd.setOnClickListener { showAddDialog() }

        LifeApp.instance.database.devTaskDao().getByProject(projectId).asLiveData().observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
        }
    }

    private fun showAddDialog() {
        val et = EditText(context)
        et.hint = getString(com.lifeapp.R.string.content)
        val spinner = Spinner(requireContext())
        spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
            listOf(getString(com.lifeapp.R.string.high), getString(com.lifeapp.R.string.medium), getString(com.lifeapp.R.string.low)))
        val layout = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(40, 20, 40, 0)
            addView(et)
            addView(spinner)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(com.lifeapp.R.string.new_task)
            .setView(layout)
            .setNegativeButton(com.lifeapp.R.string.cancel, null)
            .setPositiveButton(com.lifeapp.R.string.save) { _, _ ->
                val content = et.text.toString().trim()
                if (content.isNotEmpty()) {
                    lifecycleScope.launch {
                        LifeApp.instance.database.devTaskDao().insert(
                            DevTask(projectId = projectId, content = content, priority = spinner.selectedItemPosition)
                        )
                        Toast.makeText(context, com.lifeapp.R.string.saved, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class TaskAdapter(private val onStatusClick: (DevTask) -> Unit) : RecyclerView.Adapter<TaskAdapter.VH>() {
    private var items: List<DevTask> = emptyList()
    fun submitList(list: List<DevTask>) { items = list; notifyDataSetChanged() }
    inner class VH(val binding: ItemDevTaskBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemDevTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val task = items[position]
        holder.binding.tvContent.text = task.content
        val statusText = when (task.status) {
            0 -> com.lifeapp.R.string.pending
            1 -> com.lifeapp.R.string.in_progress
            else -> com.lifeapp.R.string.completed
        }
        holder.binding.tvStatus.setText(statusText)
        val colorRes = when (task.status) {
            0 -> android.R.color.darker_gray
            1 -> android.R.color.holo_blue_dark
            else -> android.R.color.holo_green_dark
        }
        holder.binding.tvStatus.setBackgroundResource(colorRes)
        val priColor = when (task.priority) {
            0 -> android.R.color.holo_red_dark
            1 -> android.R.color.holo_orange_dark
            else -> android.R.color.darker_gray
        }
        holder.binding.vPriority.setBackgroundResource(priColor)
        holder.binding.tvStatus.setOnClickListener { onStatusClick(task) }
    }
    override fun getItemCount() = items.size
}
