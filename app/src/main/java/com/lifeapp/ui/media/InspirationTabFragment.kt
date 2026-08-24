package com.lifeapp.ui.media

import androidx.lifecycle.asLiveData
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifeapp.LifeApp
import com.lifeapp.data.entity.Inspiration
import com.lifeapp.databinding.FragmentTabListBinding
import com.lifeapp.databinding.ItemSnippetBinding
import kotlinx.coroutines.launch

class InspirationTabFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: InspAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = InspAdapter { insp ->
            val et = EditText(context).apply { setText(insp.content) }
            AlertDialog.Builder(requireContext())
                .setTitle(com.lifeapp.R.string.edit)
                .setView(et)
                .setNegativeButton(com.lifeapp.R.string.cancel, null)
                .setPositiveButton(com.lifeapp.R.string.save) { _, _ ->
                    lifecycleScope.launch {
                        LifeApp.instance.database.inspirationDao().update(insp.copy(content = et.text.toString()))
                    }
                }
                .setNeutralButton(com.lifeapp.R.string.delete) { _, _ ->
                    lifecycleScope.launch { LifeApp.instance.database.inspirationDao().delete(insp) }
                }
                .show()
        }
        binding.rvList.layoutManager = LinearLayoutManager(context)
        binding.rvList.adapter = adapter
        binding.fabAdd.setOnClickListener {
            val et = EditText(context).apply { hint = getString(com.lifeapp.R.string.inspiration_content) }
            AlertDialog.Builder(requireContext())
                .setTitle(com.lifeapp.R.string.new_inspiration)
                .setView(et)
                .setNegativeButton(com.lifeapp.R.string.cancel, null)
                .setPositiveButton(com.lifeapp.R.string.save) { _, _ ->
                    val content = et.text.toString().trim()
                    if (content.isNotEmpty()) {
                        lifecycleScope.launch {
                            LifeApp.instance.database.inspirationDao().insert(Inspiration(content = content))
                            Toast.makeText(context, com.lifeapp.R.string.saved, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .show()
        }
        LifeApp.instance.database.inspirationDao().getAll().asLiveData().observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class InspAdapter(private val onClick: (Inspiration) -> Unit) : RecyclerView.Adapter<InspAdapter.VH>() {
    private var items: List<Inspiration> = emptyList()
    fun submitList(list: List<Inspiration>) { items = list; notifyDataSetChanged() }
    inner class VH(val binding: ItemSnippetBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemSnippetBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.tvTitle.text = items[position].content
        holder.binding.tvPreview.visibility = View.GONE
        holder.binding.root.setOnClickListener { onClick(items[position]) }
    }
    override fun getItemCount() = items.size
}
