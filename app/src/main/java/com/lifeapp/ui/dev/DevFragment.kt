package com.lifeapp.ui.dev

import androidx.lifecycle.asLiveData
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lifeapp.LifeApp
import com.lifeapp.data.entity.Project
import com.lifeapp.databinding.FragmentDevBinding
import kotlinx.coroutines.launch

class DevFragment : Fragment() {
    private var _binding: FragmentDevBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProjectAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDevBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeData()
    }

    private fun setupViews() {
        adapter = ProjectAdapter(
            scope = lifecycleScope,
            getStats = { projectId ->
                val completed = LifeApp.instance.database.devTaskDao().getCompletedCount(projectId)
                val total = LifeApp.instance.database.devTaskDao().getTotalCount(projectId)
                Pair(completed, total)
            },
            onClick = { project ->
                val intent = Intent(context, ProjectDetailActivity::class.java)
                intent.putExtra("project_id", project.id)
                intent.putExtra("project_name", project.name)
                startActivity(intent)
            }
        )
        binding.rvProjects.layoutManager = LinearLayoutManager(context)
        binding.rvProjects.adapter = adapter

        binding.fabAddProject.setOnClickListener {
            val et = EditText(context)
            et.hint = getString(com.lifeapp.R.string.project_name)
            AlertDialog.Builder(requireContext())
                .setTitle(com.lifeapp.R.string.new_project)
                .setView(et)
                .setNegativeButton(com.lifeapp.R.string.cancel, null)
                .setPositiveButton(com.lifeapp.R.string.save) { _, _ ->
                    val name = et.text.toString().trim()
                    if (name.isNotEmpty()) {
                        lifecycleScope.launch {
                            LifeApp.instance.database.projectDao().insert(Project(name = name))
                            Toast.makeText(context, com.lifeapp.R.string.saved, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .show()
        }
    }

    private fun observeData() {
        LifeApp.instance.database.projectDao().getAll().asLiveData().observe(viewLifecycleOwner) { projects ->
            adapter.submitList(projects)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
