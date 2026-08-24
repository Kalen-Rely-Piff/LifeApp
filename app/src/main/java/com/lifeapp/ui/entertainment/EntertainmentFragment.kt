package com.lifeapp.ui.entertainment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.lifeapp.databinding.FragmentEntertainmentBinding

class EntertainmentFragment : Fragment() {
    private var _binding: FragmentEntertainmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEntertainmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabs = listOf(
            getString(com.lifeapp.R.string.watch_list),
            getString(com.lifeapp.R.string.watching),
            getString(com.lifeapp.R.string.random_pick)
        )
        binding.viewPager.adapter = EntTabAdapter(requireActivity(), tabs.size)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos -> tab.text = tabs[pos] }.attach()
    }

    class EntTabAdapter(activity: FragmentActivity, private val count: Int) : FragmentStateAdapter(activity) {
        override fun getItemCount() = count
        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> WatchListTabFragment()
            1 -> WatchingTabFragment()
            else -> RandomTabFragment()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
