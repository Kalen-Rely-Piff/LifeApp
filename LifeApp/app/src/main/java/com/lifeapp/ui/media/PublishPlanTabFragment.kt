package com.lifeapp.ui.media

import androidx.lifecycle.asLiveData
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifeapp.LifeApp
import com.lifeapp.data.entity.PublishPlan
import com.lifeapp.databinding.FragmentTabListBinding
import com.lifeapp.databinding.ItemSnippetBinding
import com.lifeapp.util.DateUtil
import kotlinx.coroutines.launch
import java.util.Calendar

class PublishPlanTabFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PlanAdapter
    private var selectedDate: String = DateUtil.today()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PlanAdapter { plan ->
            lifecycleScope.launch {
                LifeApp.instance.database.publishPlanDao().update(plan.copy(isPublished = !plan.isPublished))
            }
        }
        binding.rvList.layoutManager = LinearLayoutManager(context)
        binding.rvList.adapter = adapter

        // Add date selector as header - use a simple approach
        binding.fabAdd.setOnClickListener { showAddDialog() }

        observeData()
    }

    private fun observeData() {
        LifeApp.instance.database.publishPlanDao().getAll().asLiveData().observe(viewLifecycleOwner) { plans ->
            adapter.submitList(plans)
        }
    }

    private fun showAddDialog() {
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, y, m, d ->
            val cal2 = Calendar.getInstance()
            cal2.set(y, m, d)
            selectedDate = DateUtil.formatDate(cal2.time)
            showNoteDialog()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showNoteDialog() {
        val etNote = EditText(context).apply { hint = getString(com.lifeapp.R.string.note) }
        AlertDialog.Builder(requireContext())
            .setTitle(getString(com.lifeapp.R.string.new_plan) + " - " + DateUtil.displayDate(selectedDate))
            .setView(etNote)
            .setNegativeButton(com.lifeapp.R.string.cancel, null)
            .setPositiveButton(com.lifeapp.R.string.save) { _, _ ->
                lifecycleScope.launch {
                    LifeApp.instance.database.publishPlanDao().insert(
                        PublishPlan(date = selectedDate, note = etNote.text.toString())
                    )
                    Toast.makeText(context, com.lifeapp.R.string.saved, Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

class PlanAdapter(private val onClick: (PublishPlan) -> Unit) : RecyclerView.Adapter<PlanAdapter.VH>() {
    private var items: List<PublishPlan> = emptyList()
    fun submitList(list: List<PublishPlan>) { items = list; notifyDataSetChanged() }
    inner class VH(val binding: ItemSnippetBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemSnippetBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val plan = items[position]
        holder.binding.tvTitle.text = "${DateUtil.displayDate(plan.date)} ${if (plan.isPublished) "✓" else ""}"
        holder.binding.tvPreview.text = plan.note.ifEmpty { "无备注" }
        holder.binding.root.setOnClickListener { onClick(plan) }
    }
    override fun getItemCount() = items.size
}
