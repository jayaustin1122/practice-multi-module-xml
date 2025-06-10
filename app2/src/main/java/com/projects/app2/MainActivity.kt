package com.projects.app2

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.projects.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundle = Bundle().apply {
            putSerializable("app_theme", getAppTheme())
        }

        supportFragmentManager.setFragmentResultListener("theme_request", this) { _, _ ->
            supportFragmentManager.setFragmentResult("theme_response", bundle)
        }
    }

    private fun getAppTheme(): AppTheme {
        // Force APP2 theme for this activity regardless of build flavor
        Log.d("MainActivity", "Forcing APP2 theme for this activity")
        return AppTheme.APP2
    }
}