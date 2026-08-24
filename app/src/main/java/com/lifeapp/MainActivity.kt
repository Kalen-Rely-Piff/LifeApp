package com.lifeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.lifeapp.databinding.ActivityMainBinding
import com.lifeapp.ui.entertainment.EntertainmentFragment
import com.lifeapp.ui.home.HomeFragment
import com.lifeapp.ui.media.MediaFragment
import com.lifeapp.ui.plan.PlanFragment
import com.lifeapp.ui.dev.DevFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_plan -> replaceFragment(PlanFragment())
                R.id.nav_dev -> replaceFragment(DevFragment())
                R.id.nav_media -> replaceFragment(MediaFragment())
                R.id.nav_entertainment -> replaceFragment(EntertainmentFragment())
                else -> false
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
