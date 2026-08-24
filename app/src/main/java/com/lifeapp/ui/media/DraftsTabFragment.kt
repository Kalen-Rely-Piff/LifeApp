package com.lifeapp.ui.media

import androidx.lifecycle.asLiveData
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifeapp.LifeApp
import com.lifeapp.data.entity.ScriptDraft
import com.lifeapp.databinding.FragmentTabListBinding
import com.lifeapp.databinding.ItemSnippetBinding
import kotlinx.coroutines.launch

class DraftsTabFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DraftAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DraftAdapter { draft ->
            val intent = Intent(context, DraftEditActivity::class.java)
            intent.putExtra("draft_id", draft.id)
            startActivity(intent)
        }
        binding.rvList.layoutManager = LinearLayoutManager(context)
        binding.rvList.adapter = adapter
        binding.fabAdd.setOnClickListener {
            lifecycleScope.launch {
                val id = LifeApp.instance.database.scriptDraftDao().insert(ScriptDraft(title = "未命名草稿", content = ""))
                val intent = Intent(context, DraftEditActivity::class.java)
                intent.putExtra("draft_id", id)
                startActivity(intent)
            }
        }
        LifeApp.instance.database.scriptDraftDao().getAll().asLiveData().observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class DraftAdapter(private val onClick: (ScriptDraft) -> Unit) : RecyclerView.Adapter<DraftAdapter.VH>() {
    private var items: List<ScriptDraft> = emptyList()
    fun submitList(list: List<ScriptDraft>) { items = list; notifyDataSetChanged() }
    inner class VH(val binding: ItemSnippetBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemSnippetBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val draft = items[position]
        holder.binding.tvTitle.text = draft.title
        holder.binding.tvPreview.text = if (draft.tags.isNotEmpty()) "标签: ${draft.tags}" else draft.content.take(60)
        holder.binding.root.setOnClickListener { onClick(draft) }
    }
    override fun getItemCount() = items.size
}
