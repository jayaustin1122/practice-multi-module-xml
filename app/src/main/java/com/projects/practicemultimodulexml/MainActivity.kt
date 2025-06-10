package com.projects.practicemultimodulexml

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.projects.practicemultimodulexml.databinding.ActivityMainBinding
import com.projects.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.toolbar)
//
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()
//        }
        val bundle = Bundle().apply {
            putSerializable("app_theme", getAppTheme())
        }

        supportFragmentManager.setFragmentResultListener("theme_request", this) { _, _ ->
            supportFragmentManager.setFragmentResult("theme_response", bundle)
        }
    }

    private fun getAppTheme(): AppTheme {
        // Force APP2 theme for this activity regardless of build flavor
        Log.d("MainActivity", "Forcing APP1 theme for this activity")
        return AppTheme.APP1
    }
}