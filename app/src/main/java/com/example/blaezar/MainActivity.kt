package com.example.blaezar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = (supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment).navController

        // Hide the action bar
        supportActionBar?.hide()

        // Set up the bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        // Handle navigation clicks
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.rewardFragment -> {
                    navController.navigate(R.id.action_uploadFragment_to_rewardsFragment)
                    true
                }
                R.id.uploadFragment -> {
                    navController.navigate(R.id.action_rewardsFragment_to_uploadFragment)
                    true
                }
                else -> false
            }
        }
    }
}
