package com.lifeapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifeapp.LifeApp
import com.lifeapp.MainActivity
import com.lifeapp.data.entity.Memo
import com.lifeapp.data.entity.Todo
import com.lifeapp.databinding.FragmentHomeBinding
import com.lifeapp.ui.settings.SettingsActivity
import com.lifeapp.util.DateUtil
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var memoAdapter: MemoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeData()
    }

    private fun setupViews() {
        binding.tvGreeting.text = getString(com.lifeapp.R.string.greeting, DateUtil.displayDate(DateUtil.today()))

        todoAdapter = TodoAdapter { todo ->
            lifecycleScope.launch {
                LifeApp.instance.database.todoDao().update(todo)
            }
        }
        binding.rvHomeTodos.layoutManager = LinearLayoutManager(context)
        binding.rvHomeTodos.adapter = todoAdapter

        memoAdapter = MemoAdapter {}
        binding.rvMemos.layoutManager = LinearLayoutManager(context)
        binding.rvMemos.adapter = memoAdapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, tgt: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                val memo = memoAdapter.getItem(vh.adapterPosition)
                lifecycleScope.launch {
                    LifeApp.instance.database.memoDao().delete(memo)
                    Toast.makeText(context, com.lifeapp.R.string.deleted, Toast.LENGTH_SHORT).show()
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvMemos)

        binding.btnSaveMemo.setOnClickListener {
            val content = binding.etMemo.text.toString().trim()
            if (content.isNotEmpty()) {
                lifecycleScope.launch {
                    LifeApp.instance.database.memoDao().insert(Memo(content = content))
                    binding.etMemo.text.clear()
                    Toast.makeText(context, com.lifeapp.R.string.saved, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvViewAll.setOnClickListener {
            (activity as? MainActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(com.lifeapp.R.id.bottom_nav)?.selectedItemId = com.lifeapp.R.id.nav_plan
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(context, SettingsActivity::class.java))
        }

        binding.cardDev.setOnClickListener {
            (activity as? MainActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(com.lifeapp.R.id.bottom_nav)?.selectedItemId = com.lifeapp.R.id.nav_dev
        }
        binding.cardMedia.setOnClickListener {
            (activity as? MainActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(com.lifeapp.R.id.bottom_nav)?.selectedItemId = com.lifeapp.R.id.nav_media
        }
        binding.cardEntertainment.setOnClickListener {
            (activity as? MainActivity)?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(com.lifeapp.R.id.bottom_nav)?.selectedItemId = com.lifeapp.R.id.nav_entertainment
        }
    }

    private fun observeData() {
        val today = DateUtil.today()
        LifeApp.instance.database.todoDao().getPendingTodosByDate(today, 5).asLiveData().observe(viewLifecycleOwner) { todos ->
            todoAdapter.submitList(todos)
        }
        LifeApp.instance.database.memoDao().getAll().asLiveData().observe(viewLifecycleOwner) { memos ->
            memoAdapter.submitList(memos.take(5))
        }
        // Dev pending tasks count
        LifeApp.instance.database.projectDao().getAll().asLiveData().observe(viewLifecycleOwner) { projects ->
            lifecycleScope.launch {
                var total = 0
                for (p in projects) {
                    total += LifeApp.instance.database.devTaskDao().getPendingCount(p.id)
                }
                binding.tvDevCount.text = total.toString()
            }
        }
        // Media draft count
        lifecycleScope.launch {
            val count = LifeApp.instance.database.scriptDraftDao().getCount()
            binding.tvMediaCount.text = count.toString()
        }
        LifeApp.instance.database.scriptDraftDao().getAll().asLiveData().observe(viewLifecycleOwner) { drafts ->
            binding.tvMediaCount.text = drafts.size.toString()
        }
        // Entertainment want count
        LifeApp.instance.database.entertainmentDao().getAll().asLiveData().observe(viewLifecycleOwner) { items ->
            binding.tvEntertainmentCount.text = items.count { it.status == 0 }.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
