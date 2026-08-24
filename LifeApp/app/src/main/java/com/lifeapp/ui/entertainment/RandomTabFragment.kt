package com.lifeapp.ui.entertainment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.lifeapp.LifeApp
import com.lifeapp.data.entity.Entertainment
import com.lifeapp.databinding.FragmentTabListBinding
import kotlinx.coroutines.launch

class RandomTabFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!
    private var current: Entertainment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvList.visibility = View.GONE
        binding.fabAdd.visibility = View.GONE

        val container = binding.root as ViewGroup
        val card = CardView(requireContext()).apply {
            layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(32, 64, 32, 16)
            }
            radius = 16f
            cardElevation = 4f
        }
        val inner = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 48, 32, 48)
            gravity = android.view.Gravity.CENTER
        }
        val tvResult = TextView(requireContext()).apply {
            text = "点击下方按钮随机推荐"
            textSize = 20f
            gravity = android.view.Gravity.CENTER
        }
        val tvType = TextView(requireContext()).apply {
            text = ""
            textSize = 14f
            setTextColor(android.graphics.Color.GRAY)
        }
        val btnRandom = Button(requireContext()).apply {
            text = getString(com.lifeapp.R.string.random_recommend)
            layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = 32 }
        }
        val btnLayout = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
        }
        val btnThis = Button(requireContext()).apply {
            text = getString(com.lifeapp.R.string.just_this)
            visibility = View.GONE
        }
        val btnAnother = Button(requireContext()).apply {
            text = getString(com.lifeapp.R.string.another_one)
            visibility = View.GONE
        }
        btnLayout.addView(btnThis)
        btnLayout.addView(btnAnother)
        inner.addView(tvResult)
        inner.addView(tvType)
        inner.addView(btnRandom)
        inner.addView(btnLayout)
        card.addView(inner)
        container.addView(card)

        btnRandom.setOnClickListener {
            lifecycleScope.launch {
                val item = LifeApp.instance.database.entertainmentDao().getRandomWant()
                if (item == null) {
                    Toast.makeText(context, "想看清单为空", Toast.LENGTH_SHORT).show()
                } else {
                    current = item
                    tvResult.text = item.name
                    val typeText = when (item.type) { 0 -> "电影"; 1 -> "剧"; 2 -> "书"; else -> "游戏" }
                    tvType.text = typeText
                    btnThis.visibility = View.VISIBLE
                    btnAnother.visibility = View.VISIBLE
                }
            }
        }
        btnThis.setOnClickListener {
            current?.let { item ->
                lifecycleScope.launch {
                    LifeApp.instance.database.entertainmentDao().update(item.copy(status = 1))
                    Toast.makeText(context, "已标记为进行中", Toast.LENGTH_SHORT).show()
                    tvResult.text = "点击下方按钮随机推荐"
                    tvType.text = ""
                    btnThis.visibility = View.GONE
                    btnAnother.visibility = View.GONE
                }
            }
        }
        btnAnother.setOnClickListener { btnRandom.performClick() }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
