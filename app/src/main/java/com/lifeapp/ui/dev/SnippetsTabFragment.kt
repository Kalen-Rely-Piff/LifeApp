package com.lifeapp.ui.dev

import androidx.lifecycle.asLiveData
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifeapp.LifeApp
import com.lifeapp.data.entity.CodeSnippet
import com.lifeapp.databinding.FragmentTabListBinding
import com.lifeapp.databinding.ItemSnippetBinding
import kotlinx.coroutines.launch

class SnippetsTabFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SnippetAdapter
    private var projectId: Long = 0
    private val PICK_FILE = 1001

    companion object {
        fun newInstance(projectId: Long) = SnippetsTabFragment().apply {
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
        adapter = SnippetAdapter { snippet ->
            showSnippetDetail(snippet)
        }
        binding.rvList.layoutManager = LinearLayoutManager(context)
        binding.rvList.adapter = adapter

        // Add file import header view
        val header = layoutInflater.inflate(com.lifeapp.R.layout.header_file_import, binding.rvList, false)
        header.findViewById<Button>(com.lifeapp.R.id.btn_import_file).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent, "选择文件"), PICK_FILE)
        }
        // We'll handle import via a different approach since RecyclerView headers are complex
        // Instead, add a button above the list

        binding.fabAdd.setOnClickListener { showAddDialog() }

        LifeApp.instance.database.codeSnippetDao().getByProject(projectId).asLiveData().observe(viewLifecycleOwner) { snippets ->
            adapter.submitList(snippets)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                importFile(uri)
            }
        }
    }

    private fun importFile(uri: Uri) {
        try {
            val fileName = uri.lastPathSegment ?: "imported.txt"
            val content = requireContext().contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() } ?: ""
            lifecycleScope.launch {
                LifeApp.instance.database.codeSnippetDao().insert(
                    CodeSnippet(projectId = projectId, title = fileName, code = content, sourceFileName = fileName)
                )
                Toast.makeText(context, "文件已导入为代码片段", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "导入失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddDialog() {
        val etTitle = EditText(context).apply { hint = getString(com.lifeapp.R.string.code_title) }
        val etCode = EditText(context).apply {
            hint = getString(com.lifeapp.R.string.code_content)
            minLines = 4
            isSingleLine = false
        }
        val btnImport = Button(context).apply { text = getString(com.lifeapp.R.string.import_file) }
        btnImport.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent, "选择文件"), PICK_FILE)
        }
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 0)
            addView(etTitle)
            addView(etCode)
            addView(btnImport)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(com.lifeapp.R.string.new_snippet)
            .setView(layout)
            .setNegativeButton(com.lifeapp.R.string.cancel, null)
            .setPositiveButton(com.lifeapp.R.string.save) { _, _ ->
                val title = etTitle.text.toString().trim()
                val code = etCode.text.toString()
                if (title.isNotEmpty()) {
                    lifecycleScope.launch {
                        LifeApp.instance.database.codeSnippetDao().insert(
                            CodeSnippet(projectId = projectId, title = title, code = code)
                        )
                        Toast.makeText(context, com.lifeapp.R.string.saved, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun showSnippetDetail(snippet: CodeSnippet) {
        val tv = TextView(context).apply {
            text = snippet.code
            setPadding(40, 20, 40, 20)
            textSize = 12f
            setTextIsSelectable(true)
        }
        val scroll = android.widget.ScrollView(context).apply { addView(tv) }
        AlertDialog.Builder(requireContext())
            .setTitle(snippet.title)
            .setView(scroll)
            .setNeutralButton(com.lifeapp.R.string.copy_all) { _, _ ->
                val clipboard = requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                clipboard.setPrimaryClip(android.content.ClipData.newPlainText("code", snippet.code))
                Toast.makeText(context, com.lifeapp.R.string.copied, Toast.LENGTH_SHORT).show()
            }
            .setPositiveButton(com.lifeapp.R.string.delete) { _, _ ->
                lifecycleScope.launch {
                    LifeApp.instance.database.codeSnippetDao().delete(snippet)
                    Toast.makeText(context, com.lifeapp.R.string.deleted, Toast.LENGTH_SHORT).show()
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

class SnippetAdapter(private val onClick: (CodeSnippet) -> Unit) : RecyclerView.Adapter<SnippetAdapter.VH>() {
    private var items: List<CodeSnippet> = emptyList()
    fun submitList(list: List<CodeSnippet>) { items = list; notifyDataSetChanged() }
    inner class VH(val binding: ItemSnippetBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemSnippetBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val snippet = items[position]
        holder.binding.tvTitle.text = snippet.title
        holder.binding.tvPreview.text = snippet.code.take(80)
        holder.binding.root.setOnClickListener { onClick(snippet) }
    }
    override fun getItemCount() = items.size
}
