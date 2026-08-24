package com.lifeapp.ui.media

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.lifeapp.LifeApp
import com.lifeapp.data.entity.ScriptDraft
import com.lifeapp.databinding.ActivityDraftEditBinding
import kotlinx.coroutines.launch

class DraftEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDraftEditBinding
    private var draftId: Long = 0
    private var draft: ScriptDraft? = null
    private var saveRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDraftEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(com.lifeapp.R.string.drafts)

        draftId = intent.getLongExtra("draft_id", 0)

        lifecycleScope.launch {
            draft = LifeApp.instance.database.scriptDraftDao().getById(draftId)
            draft?.let {
                binding.etTitle.setText(it.title)
                binding.etTags.setText(it.tags)
                binding.etContent.setText(it.content)
            }
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                autoSave()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etTitle.addTextChangedListener(watcher)
        binding.etTags.addTextChangedListener(watcher)
        binding.etContent.addTextChangedListener(watcher)

        binding.btnDelete.setOnClickListener {
            lifecycleScope.launch {
                draft?.let { LifeApp.instance.database.scriptDraftDao().delete(it) }
                Toast.makeText(this@DraftEditActivity, com.lifeapp.R.string.deleted, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun autoSave() {
        val title = binding.etTitle.text.toString()
        val tags = binding.etTags.text.toString()
        val content = binding.etContent.text.toString()
        lifecycleScope.launch {
            val current = LifeApp.instance.database.scriptDraftDao().getById(draftId)
            if (current != null) {
                LifeApp.instance.database.scriptDraftDao().update(
                    current.copy(title = title.ifEmpty { "未命名草稿" }, tags = tags, content = content, updatedAt = System.currentTimeMillis())
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onPause() {
        super.onPause()
        autoSave()
    }
}
