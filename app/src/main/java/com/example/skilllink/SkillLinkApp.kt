package com.example.skilllink

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.skilllink.auth.AuthViewModel
import com.example.skilllink.navigation.AuthNavHost

@Composable
fun SkillLinkApp(isDarkTheme: Boolean,
                 onToggleTheme: () -> Unit) {

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = viewModel()
        AuthNavHost(
            navController = navController,
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            authViewModel = authViewModel
        )
    }
}

