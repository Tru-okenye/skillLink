package com.example.skilllink

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.skilllink.auth.AuthViewModel
import com.example.skilllink.navigation.AuthNavHost

@Composable
fun SkillLinkApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    AuthNavHost(
        navController = navController,
        authViewModel = authViewModel
    )
}




