package com.example.skilllink.data.viewmodel


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilllink.video.Signaling
import com.example.skilllink.video.WebRTCClient
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.webrtc.*
import javax.inject.Inject

class VideoCallViewModel(application: Application) : AndroidViewModel(application)
 {

    private val db = FirebaseFirestore.getInstance()
    private val eglBase = EglBase.create()

    private val _localTrack = MutableStateFlow<VideoTrack?>(null)
    val localTrack: StateFlow<VideoTrack?> = _localTrack

    private val _remoteTrack = MutableStateFlow<VideoTrack?>(null)
    val remoteTrack: StateFlow<VideoTrack?> = _remoteTrack

    private lateinit var client: WebRTCClient
    private lateinit var signaling: Signaling

     fun initWebRTC(callId: String?) {
         val id = callId ?: db.collection("calls").document().id
         signaling = Signaling(db, id)

         client = WebRTCClient(
             context = getApplication(),
             onRemoteStream = {
                 Log.d("VideoCall", "Remote video track received")
                 _remoteTrack.value = it
             },
             onLocalStream = {
                 Log.d("VideoCall", "Local video track set")
                 _localTrack.value = it
             }
         )

         client.createPeerConnection(
             listOf(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()),
             signaling
         )

         // Listen for ICE candidates
         signaling.listenForIce { client.addIceCandidate(it) }

         if (callId == null) {
             // Caller
             client.createOffer(MediaConstraints(), signaling)
             signaling.listenForSession { client.setRemoteDescription(it) }
         } else {
             // Callee
             signaling.listenForSession { desc ->
                 client.setRemoteDescription(desc)
                 client.createAnswer(MediaConstraints(), signaling)
             }
         }
     }


    fun close() {
        client.close()
    }

    fun getEglContext(): EglBase.Context = eglBase.eglBaseContext
}
