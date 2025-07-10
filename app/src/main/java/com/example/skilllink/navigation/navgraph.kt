package com.example.skilllink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.skilllink.auth.LoginScreen
import com.example.skilllink.auth.SignUpScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.skilllink.auth.AuthViewModel
import com.example.skilllink.chat.ChatScreen
import com.example.skilllink.profile.ProfileScreen
import com.example.skilllink.skills.CategoryUsersScreen
import com.example.skilllink.ui.theme.home.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.skilllink.data.viewmodel.VideoCallViewModel
import com.example.skilllink.video.VideoCallRoute
import com.example.skilllink.video.VideoCallScreen


@Composable
fun AuthNavHost(navController: NavHostController,
                 authViewModel: AuthViewModel, startDestination: String = "login") {
    NavHost(
        navController = navController,
        startDestination = startDestination
    )  {
        composable("signup") {
            SignUpScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel,
                onNavigateToSignUp = {
                    navController.navigate("signup") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("profile") {
            ProfileScreen()
        }

        composable("category_users/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            CategoryUsersScreen(category = category, navController = navController)
        }

        composable("chat") {
            val navBackStackEntry = navController.previousBackStackEntry
            val recipientId = navBackStackEntry?.savedStateHandle?.get<String>("recipientId")
            val currentUserId = navBackStackEntry?.savedStateHandle?.get<String>("currentUserId")

            if (recipientId != null && currentUserId != null) {
                ChatScreen(
                    currentUserId = currentUserId,
                    recipientId = recipientId,
                    navController = navController
                )
            }
        }



        composable("video_call/{callId}") { backStackEntry ->
            val callId = backStackEntry.arguments?.getString("callId")
            VideoCallRoute(callId = callId)
        }


    }
}
