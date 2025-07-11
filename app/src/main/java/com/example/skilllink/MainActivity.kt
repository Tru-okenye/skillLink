package com.example.skilllink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.skilllink.profile.DataStoreManager
import com.example.skilllink.ui.theme.SkillLinkTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dataStoreManager = DataStoreManager(applicationContext)

        setContent {
            val isDarkTheme by dataStoreManager.darkModeFlow.collectAsState(initial = false)


                SkillLinkApp(isDarkTheme = isDarkTheme,
                    onToggleTheme = {
                        lifecycleScope.launch {
                            dataStoreManager.setDarkMode(!isDarkTheme)
                        }
                    })

        }
    }
}

