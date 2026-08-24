package com.lifeapp.ui.dev

import androidx.lifecycle.asLiveData
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifeapp.LifeApp
import com.lifeapp.data.entity.TechNote
import com.lifeapp.databinding.FragmentTabListBinding
import com.lifeapp.databinding.ItemSnippetBinding
import kotlinx.coroutines.launch

class NotesTabFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NoteAdapter
    private var projectId: Long = 0

    companion object {
        fun newInstance(projectId: Long) = NotesTabFragment().apply {
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
        adapter = NoteAdapter { note -> showNoteDetail(note) }
        binding.rvList.layoutManager = LinearLayoutManager(context)
        binding.rvList.adapter = adapter
        binding.fabAdd.setOnClickListener { showAddDialog() }
        LifeApp.instance.database.techNoteDao().getByProject(projectId).asLiveData().observe(viewLifecycleOwner) { notes ->
            adapter.submitList(notes)
        }
    }

    private fun showAddDialog() {
        val etTitle = EditText(context).apply { hint = getString(com.lifeapp.R.string.note_title) }
        val etContent = EditText(context).apply {
            hint = getString(com.lifeapp.R.string.note_content)
            minLines = 4
            isSingleLine = false
        }
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 0)
            addView(etTitle)
            addView(etContent)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(com.lifeapp.R.string.new_note)
            .setView(layout)
            .setNegativeButton(com.lifeapp.R.string.cancel, null)
            .setPositiveButton(com.lifeapp.R.string.save) { _, _ ->
                val title = etTitle.text.toString().trim()
                val content = etContent.text.toString()
                if (title.isNotEmpty()) {
                    lifecycleScope.launch {
                        LifeApp.instance.database.techNoteDao().insert(
                            TechNote(projectId = projectId, title = title, content = content)
                        )
                        Toast.makeText(context, com.lifeapp.R.string.saved, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun showNoteDetail(note: TechNote) {
        val tv = TextView(context).apply {
            text = note.content
            setPadding(40, 20, 40, 20)
            textSize = 14f
            setTextIsSelectable(true)
        }
        val scroll = android.widget.ScrollView(context).apply { addView(tv) }
        AlertDialog.Builder(requireContext())
            .setTitle(note.title)
            .setView(scroll)
            .setPositiveButton(com.lifeapp.R.string.delete) { _, _ ->
                lifecycleScope.launch {
                    LifeApp.instance.database.techNoteDao().delete(note)
                }
            }
            .setNegativeButton(com.lifeapp.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class NoteAdapter(private val onClick: (TechNote) -> Unit) : RecyclerView.Adapter<NoteAdapter.VH>() {
    private var items: List<TechNote> = emptyList()
    fun submitList(list: List<TechNote>) { items = list; notifyDataSetChanged() }
    inner class VH(val binding: ItemSnippetBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemSnippetBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val note = items[position]
        holder.binding.tvTitle.text = note.title
        holder.binding.tvPreview.text = note.content.take(80)
        holder.binding.root.setOnClickListener { onClick(note) }
    }
    override fun getItemCount() = items.size
}
