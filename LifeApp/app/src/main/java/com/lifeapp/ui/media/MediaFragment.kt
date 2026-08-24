package com.lifeapp.ui.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lifeapp.databinding.FragmentMediaBinding

class MediaFragment : Fragment() {
    private var _binding: FragmentMediaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabs = listOf(
            getString(com.lifeapp.R.string.inspirations),
            getString(com.lifeapp.R.string.drafts),
            getString(com.lifeapp.R.string.publish_plan),
            getString(com.lifeapp.R.string.materials)
        )
        binding.viewPager.adapter = MediaTabAdapter(requireActivity(), tabs.size)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos -> tab.text = tabs[pos] }.attach()
    }

    class MediaTabAdapter(activity: FragmentActivity, private val count: Int) : FragmentStateAdapter(activity) {
        override fun getItemCount() = count
        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> InspirationTabFragment()
            1 -> DraftsTabFragment()
            2 -> PublishPlanTabFragment()
            else -> MaterialsTabFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
