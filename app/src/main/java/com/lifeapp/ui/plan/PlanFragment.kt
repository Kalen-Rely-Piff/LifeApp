package com.lifeapp.ui.plan

import androidx.lifecycle.asLiveData
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lifeapp.LifeApp
import com.lifeapp.data.entity.Todo
import com.lifeapp.databinding.DialogTodoBinding
import com.lifeapp.databinding.FragmentPlanBinding
import com.lifeapp.ui.home.TodoAdapter
import com.lifeapp.util.DateUtil
import kotlinx.coroutines.launch
import java.util.Calendar

class PlanFragment : Fragment() {
    private var _binding: FragmentPlanBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TodoAdapter
    private var currentDate: String = DateUtil.today()
    private var editingTodo: Todo? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeData()
    }

    private fun setupViews() {
        binding.tvDate.text = DateUtil.displayDate(currentDate)

        adapter = TodoAdapter { todo ->
            lifecycleScope.launch {
                LifeApp.instance.database.todoDao().update(todo)
            }
        }
        binding.rvTodos.layoutManager = LinearLayoutManager(context)
        binding.rvTodos.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, tgt: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                val todo = (adapter as? TodoAdapter)?.let {
                    // Need to get item - use a different approach
                }
                // Use position to get item from adapter's data
                // Since TodoAdapter doesn't expose getItem, we'll handle edit via click
            }
        })
        // Don't attach swipe for now, use click to edit

        binding.btnPrevDay.setOnClickListener {
            currentDate = DateUtil.addDays(currentDate, -1)
            binding.tvDate.text = DateUtil.displayDate(currentDate)
            observeData()
        }
        binding.btnNextDay.setOnClickListener {
            currentDate = DateUtil.addDays(currentDate, 1)
            binding.tvDate.text = DateUtil.displayDate(currentDate)
            observeData()
        }
        binding.tvDate.setOnClickListener {
            showDatePicker()
        }
        binding.fabAddTodo.setOnClickListener {
            editingTodo = null
            showTodoDialog()
        }
        binding.btnClearCompleted.setOnClickListener {
            lifecycleScope.launch {
                LifeApp.instance.database.todoDao().deleteCompleted(currentDate)
                Toast.makeText(context, com.lifeapp.R.string.deleted, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DateUtil.parseDate(currentDate)?.let { cal.time = it }
        DatePickerDialog(requireContext(), { _, y, m, d ->
            val cal2 = Calendar.getInstance()
            cal2.set(y, m, d)
            currentDate = DateUtil.formatDate(cal2.time)
            binding.tvDate.text = DateUtil.displayDate(currentDate)
            observeData()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTodoDialog() {
        val dialogBinding = DialogTodoBinding.inflate(layoutInflater)
        val todo = editingTodo
        if (todo != null) {
            dialogBinding.etContent.setText(todo.content)
            when (todo.priority) {
                0 -> dialogBinding.rbHigh.isChecked = true
                1 -> dialogBinding.rbMedium.isChecked = true
                2 -> dialogBinding.rbLow.isChecked = true
            }
        }

        var reminderTime: Long? = todo?.reminderTime
        dialogBinding.cbReminder.isChecked = reminderTime != null
        dialogBinding.cbReminder.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                val cal = Calendar.getInstance()
                TimePickerDialog(requireContext(), { _, h, min ->
                    cal.set(Calendar.HOUR_OF_DAY, h)
                    cal.set(Calendar.MINUTE, min)
                    reminderTime = cal.timeInMillis
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            } else {
                reminderTime = null
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle(if (todo == null) com.lifeapp.R.string.new_todo else com.lifeapp.R.string.edit)
            .setView(dialogBinding.root)
            .setNegativeButton(com.lifeapp.R.string.cancel, null)
            .setPositiveButton(com.lifeapp.R.string.save) { _, _ ->
                val content = dialogBinding.etContent.text.toString().trim()
                if (content.isEmpty()) return@setPositiveButton
                val priority = when (dialogBinding.rgPriority.checkedRadioButtonId) {
                    dialogBinding.rbHigh.id -> 0
                    dialogBinding.rbLow.id -> 2
                    else -> 1
                }
                lifecycleScope.launch {
                    if (todo == null) {
                        LifeApp.instance.database.todoDao().insert(
                            Todo(content = content, date = currentDate, priority = priority, reminderTime = reminderTime)
                        )
                    } else {
                        LifeApp.instance.database.todoDao().update(
                            todo.copy(content = content, priority = priority, reminderTime = reminderTime)
                        )
                    }
                    Toast.makeText(context, com.lifeapp.R.string.saved, Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun observeData() {
        LifeApp.instance.database.todoDao().getTodosByDate(currentDate).asLiveData().observe(viewLifecycleOwner) { todos ->
            adapter.submitList(todos)
            lifecycleScope.launch {
                val completed = todos.count { it.isCompleted }
                binding.tvStats.text = getString(com.lifeapp.R.string.completed_count, completed, todos.size)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
