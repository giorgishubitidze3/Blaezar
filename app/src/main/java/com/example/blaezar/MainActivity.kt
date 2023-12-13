package com.example.blaezar

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.blaezar.fragments.UploadFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView
    private val REQUEST_STORAGE_PERMISSION_CODE = 102
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





        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION_CODE
            )
        } else {
            // Permission already granted, proceed with your logic
            val fragment = UploadFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, fragment)
            transaction.commit()
        }
            }

            override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)

                if (requestCode == REQUEST_STORAGE_PERMISSION_CODE && grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission granted, proceed with your logic
                        Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
                        val fragment = UploadFragment()
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.add(R.id.fragment_container, fragment)
                        transaction.commit()
                    } else {
                        // Permission denied, show a message to the user
                        Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show()
                    }
                }
            }


}

// Check if storage permission is already granted




