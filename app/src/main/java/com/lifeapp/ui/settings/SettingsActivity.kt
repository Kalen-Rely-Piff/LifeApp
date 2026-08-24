package com.lifeapp.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.lifeapp.LifeApp
import com.lifeapp.data.AppDatabase
import com.lifeapp.databinding.ActivitySettingsBinding
import com.lifeapp.util.PrefsUtil
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val PICK_BACKGROUND = 2001
    private val PICK_RESTORE = 2002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(com.lifeapp.R.string.settings)

        val dbPath = AppDatabase.getDatabasePath(this)
        binding.tvDbPath.text = dbPath

        binding.btnCopyPath.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            clipboard.setPrimaryClip(android.content.ClipData.newPlainText("db_path", dbPath))
            Toast.makeText(this, com.lifeapp.R.string.copied, Toast.LENGTH_SHORT).show()
        }

        binding.btnExport.setOnClickListener { exportBackup() }
        binding.btnRestore.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            startActivityForResult(Intent.createChooser(intent, "选择备份文件"), PICK_RESTORE)
        }
        binding.btnClearData.setOnClickListener { showClearDataDialog() }
        binding.btnBackground.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "选择背景图片"), PICK_BACKGROUND)
        }

        val themeMode = PrefsUtil.getThemeMode(this)
        when (themeMode) {
            0 -> binding.rbLight.isChecked = true
            1 -> binding.rbDark.isChecked = true
            else -> binding.rbSystem.isChecked = true
        }
        binding.rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                binding.rbDark.id -> 1
                binding.rbSystem.id -> 2
                else -> 0
            }
            PrefsUtil.setThemeMode(this, mode)
            Toast.makeText(this, com.lifeapp.R.string.saved, Toast.LENGTH_SHORT).show()
        }

        binding.tvAbout.text = "${getString(com.lifeapp.R.string.app_name)}\n${getString(com.lifeapp.R.string.version)} 1.0\n${getString(com.lifeapp.R.string.local_only_note)}"
    }

    private fun exportBackup() {
        try {
            val dbFile = getDatabasePath("life_app.db")
            val destFile = File(getExternalFilesDir(null), "life_app_backup_${System.currentTimeMillis()}.db")
            FileInputStream(dbFile).use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            Toast.makeText(this, "备份已保存到: ${destFile.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "备份失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreBackup(uri: Uri) {
        try {
            val dbFile = getDatabasePath("life_app.db")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(dbFile).use { output ->
                    input.copyTo(output)
                }
            }
            Toast.makeText(this, "恢复成功，请重启应用", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "恢复失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showClearDataDialog() {
        val et = EditText(this).apply { hint = "请输入\"确认\"" }
        AlertDialog.Builder(this)
            .setTitle(com.lifeapp.R.string.clear_all_data)
            .setMessage(com.lifeapp.R.string.clear_data_confirm)
            .setView(et)
            .setNegativeButton(com.lifeapp.R.string.cancel, null)
            .setPositiveButton(com.lifeapp.R.string.confirm) { _, _ ->
                if (et.text.toString() == "确认") {
                    lifecycleScope.launch {
                        LifeApp.instance.database.clearAllTables()
                        PrefsUtil.setBackgroundUri(this@SettingsActivity, null)
                        Toast.makeText(this@SettingsActivity, "数据已清空", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "输入不正确，未清空", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        when (requestCode) {
            PICK_BACKGROUND -> {
                data?.data?.let { uri ->
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    PrefsUtil.setBackgroundUri(this, uri.toString())
                    Toast.makeText(this, "背景已设置", Toast.LENGTH_SHORT).show()
                }
            }
            PICK_RESTORE -> {
                data?.data?.let { uri -> restoreBackup(uri) }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
