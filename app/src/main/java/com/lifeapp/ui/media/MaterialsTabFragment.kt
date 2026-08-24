package com.lifeapp.ui.media

import androidx.lifecycle.asLiveData
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifeapp.LifeApp
import com.lifeapp.data.entity.Material
import com.lifeapp.databinding.FragmentTabListBinding
import com.lifeapp.databinding.ItemDevTaskBinding
import kotlinx.coroutines.launch

class MaterialsTabFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MaterialAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = MaterialAdapter { material ->
            lifecycleScope.launch {
                LifeApp.instance.database.materialDao().update(material.copy(isCompleted = !material.isCompleted))
            }
        }
        binding.rvList.layoutManager = LinearLayoutManager(context)
        binding.rvList.adapter = adapter
        binding.fabAdd.setOnClickListener { showAddDialog() }
        LifeApp.instance.database.materialDao().getAll().asLiveData().observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    private fun showAddDialog() {
        val et = EditText(context).apply { hint = getString(com.lifeapp.R.string.material_content) }
        val spinner = Spinner(requireContext()).apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                listOf(getString(com.lifeapp.R.string.image), getString(com.lifeapp.R.string.video), getString(com.lifeapp.R.string.other)))
        }
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 0)
            addView(et)
            addView(spinner)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(com.lifeapp.R.string.new_material)
            .setView(layout)
            .setNegativeButton(com.lifeapp.R.string.cancel, null)
            .setPositiveButton(com.lifeapp.R.string.save) { _, _ ->
                val content = et.text.toString().trim()
                if (content.isNotEmpty()) {
                    lifecycleScope.launch {
                        LifeApp.instance.database.materialDao().insert(
                            Material(content = content, type = spinner.selectedItemPosition)
                        )
                        Toast.makeText(context, com.lifeapp.R.string.saved, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class MaterialAdapter(private val onClick: (Material) -> Unit) : RecyclerView.Adapter<MaterialAdapter.VH>() {
    private var items: List<Material> = emptyList()
    fun submitList(list: List<Material>) { items = list; notifyDataSetChanged() }
    inner class VH(val binding: ItemDevTaskBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemDevTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val m = items[position]
        holder.binding.tvContent.text = m.content
        val typeText = when (m.type) { 0 -> com.lifeapp.R.string.image; 1 -> com.lifeapp.R.string.video; else -> com.lifeapp.R.string.other }
        holder.binding.tvStatus.setText(typeText)
        holder.binding.tvStatus.setBackgroundResource(if (m.isCompleted) android.R.color.holo_green_dark else android.R.color.darker_gray)
        holder.binding.vPriority.setBackgroundResource(android.R.color.transparent)
        holder.binding.root.setOnClickListener { onClick(m) }
    }
    override fun getItemCount() = items.size
}
