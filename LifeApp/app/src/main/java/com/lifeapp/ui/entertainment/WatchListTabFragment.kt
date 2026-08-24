package com.lifeapp.ui.entertainment

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
import com.lifeapp.data.entity.Entertainment
import com.lifeapp.databinding.FragmentTabListBinding
import com.lifeapp.databinding.ItemSnippetBinding
import kotlinx.coroutines.launch

class WatchListTabFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EntAdapter
    private var filterType: Int = -1 // -1 = all

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = EntAdapter { item ->
            showStatusDialog(item)
        }
        binding.rvList.layoutManager = LinearLayoutManager(context)
        binding.rvList.adapter = adapter
        binding.fabAdd.setOnClickListener { showAddDialog() }
        LifeApp.instance.database.entertainmentDao().getAll().asLiveData().observe(viewLifecycleOwner) { items ->
            val filtered = if (filterType == -1) items.filter { it.status == 0 } else items.filter { it.status == 0 && it.type == filterType }
            adapter.submitList(filtered)
        }
    }

    private fun showAddDialog() {
        val etName = EditText(context).apply { hint = getString(com.lifeapp.R.string.entertainment_name) }
        val spinner = Spinner(requireContext()).apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item,
                listOf(getString(com.lifeapp.R.string.movie), getString(com.lifeapp.R.string.tv), getString(com.lifeapp.R.string.book), getString(com.lifeapp.R.string.game)))
        }
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 0)
            addView(etName)
            addView(spinner)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(com.lifeapp.R.string.new_entertainment)
            .setView(layout)
            .setNegativeButton(com.lifeapp.R.string.cancel, null)
            .setPositiveButton(com.lifeapp.R.string.save) { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    lifecycleScope.launch {
                        LifeApp.instance.database.entertainmentDao().insert(
                            Entertainment(name = name, type = spinner.selectedItemPosition, status = 0)
                        )
                        Toast.makeText(context, com.lifeapp.R.string.saved, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun showStatusDialog(item: Entertainment) {
        val options = arrayOf(getString(com.lifeapp.R.string.want_to_watch), getString(com.lifeapp.R.string.in_progress), getString(com.lifeapp.R.string.watched))
        AlertDialog.Builder(requireContext())
            .setTitle(item.name)
            .setItems(options) { _, which ->
                lifecycleScope.launch {
                    LifeApp.instance.database.entertainmentDao().update(item.copy(status = which))
                }
            }
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class EntAdapter(private val onClick: (Entertainment) -> Unit) : RecyclerView.Adapter<EntAdapter.VH>() {
    private var items: List<Entertainment> = emptyList()
    fun submitList(list: List<Entertainment>) { items = list; notifyDataSetChanged() }
    inner class VH(val binding: ItemSnippetBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemSnippetBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val typeText = when (item.type) { 0 -> "电影"; 1 -> "剧"; 2 -> "书"; else -> "游戏" }
        holder.binding.tvTitle.text = item.name
        holder.binding.tvPreview.text = typeText
        holder.binding.root.setOnClickListener { onClick(item) }
    }
    override fun getItemCount() = items.size
}
