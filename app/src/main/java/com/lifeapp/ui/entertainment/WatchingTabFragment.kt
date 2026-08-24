package com.lifeapp.ui.entertainment

import androidx.lifecycle.asLiveData
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
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

class WatchingTabFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WatchingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = WatchingAdapter { item -> showDetailDialog(item) }
        binding.rvList.layoutManager = LinearLayoutManager(context)
        binding.rvList.adapter = adapter
        binding.fabAdd.visibility = View.GONE
        LifeApp.instance.database.entertainmentDao().getAll().asLiveData().observe(viewLifecycleOwner) { items ->
            adapter.submitList(items.filter { it.status == 1 || it.status == 2 })
        }
    }

    private fun showDetailDialog(item: Entertainment) {
        val etProgress = EditText(context).apply {
            hint = getString(com.lifeapp.R.string.progress)
            setText(item.progress)
        }
        val etThoughts = EditText(context).apply {
            hint = getString(com.lifeapp.R.string.thoughts)
            setText(item.thoughts)
        }
        val ratingBar = RatingBar(context).apply {
            numStars = 5
            rating = item.rating.toFloat()
        }
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 20, 40, 0)
            addView(TextView(context).apply { text = getString(com.lifeapp.R.string.rating) })
            addView(ratingBar)
            addView(etProgress)
            addView(etThoughts)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(item.name)
            .setView(layout)
            .setNegativeButton(com.lifeapp.R.string.cancel, null)
            .setPositiveButton(com.lifeapp.R.string.save) { _, _ ->
                lifecycleScope.launch {
                    LifeApp.instance.database.entertainmentDao().update(
                        item.copy(progress = etProgress.text.toString(), thoughts = etThoughts.text.toString(), rating = ratingBar.rating.toInt())
                    )
                    Toast.makeText(context, com.lifeapp.R.string.saved, Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class WatchingAdapter(private val onClick: (Entertainment) -> Unit) : RecyclerView.Adapter<WatchingAdapter.VH>() {
    private var items: List<Entertainment> = emptyList()
    fun submitList(list: List<Entertainment>) { items = list; notifyDataSetChanged() }
    inner class VH(val binding: ItemSnippetBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemSnippetBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val statusText = if (item.status == 1) "进行中" else "已看完"
        holder.binding.tvTitle.text = item.name
        holder.binding.tvPreview.text = "$statusText ${if (item.rating > 0) "★${item.rating}" else ""} ${item.progress}"
        holder.binding.root.setOnClickListener { onClick(item) }
    }
    override fun getItemCount() = items.size
}
