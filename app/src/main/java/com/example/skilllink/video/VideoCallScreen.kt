package com.example.skilllink.video


import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.skilllink.data.viewmodel.VideoCallViewModel
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

@Composable
fun VideoCallScreen(
    viewModel: VideoCallViewModel
) {
    val localTrack by viewModel.localTrack.collectAsState()
    val remoteTrack by viewModel.remoteTrack.collectAsState()
    val eglContext = viewModel.getEglContext()

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { context ->
                SurfaceViewRenderer(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    init(eglContext, null)
                    setMirror(true)
                    localTrack?.addSink(this)
                }
            }
        )

        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { context ->
                SurfaceViewRenderer(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    init(eglContext, null)
                    setMirror(false)
                    remoteTrack?.addSink(this)
                }
            }
        )
    }
}
