package com.example.skilllink.video

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skilllink.data.viewmodel.VideoCallViewModel

@Composable
fun VideoCallRoute(callId: String?) {
    val context = LocalContext.current
    val viewModel: VideoCallViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return VideoCallViewModel(context.applicationContext as Application) as T
        }
    })

    LaunchedEffect(Unit) {
        viewModel.initWebRTC(callId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.close()
        }
    }

    VideoCallScreen(viewModel = viewModel)
}
