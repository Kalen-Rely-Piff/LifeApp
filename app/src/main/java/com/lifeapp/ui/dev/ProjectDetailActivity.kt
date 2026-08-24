package com.lifeapp.ui.dev

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lifeapp.databinding.ActivityProjectDetailBinding

class ProjectDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProjectDetailBinding
    private var projectId: Long = 0
    private var projectName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        projectId = intent.getLongExtra("project_id", 0)
        projectName = intent.getStringExtra("project_name") ?: ""

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = projectName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val tabs = listOf(
            getString(com.lifeapp.R.string.tasks),
            getString(com.lifeapp.R.string.code_snippets),
            getString(com.lifeapp.R.string.tech_notes)
        )

        binding.viewPager.adapter = TabAdapter(this, projectId, tabs.size)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    class TabAdapter(activity: FragmentActivity, private val projectId: Long, private val count: Int) : FragmentStateAdapter(activity) {
        override fun getItemCount() = count
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> TasksTabFragment.newInstance(projectId)
                1 -> SnippetsTabFragment.newInstance(projectId)
                else -> NotesTabFragment.newInstance(projectId)
            }
        }
    }
}
