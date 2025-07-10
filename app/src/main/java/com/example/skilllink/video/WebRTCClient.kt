package com.example.skilllink.video

import android.content.Context
import android.util.Log
import org.webrtc.*

class WebRTCClient(
    context: Context,
    private val onRemoteStream: (VideoTrack) -> Unit,
    private val onLocalStream: (VideoTrack) -> Unit
) {
    private val eglBase = EglBase.create()
    private val factory: PeerConnectionFactory

    private var peerConnection: PeerConnection? = null
    private var localVideoTrack: VideoTrack? = null

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .createInitializationOptions()
        )
        factory = PeerConnectionFactory.builder()
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .createPeerConnectionFactory()

        startLocalVideo(context)
    }

    private fun startLocalVideo(context: Context) {
        val capturer = Camera1Enumerator(false).run {
            deviceNames.firstOrNull { isFrontFacing(it) }?.let { createCapturer(it, null) }
        } ?:  run {
            Log.e("WebRTC", "Failed to find front camera")
            return
        }
        val surfaceTextureHelper = SurfaceTextureHelper.create("Capture", eglBase.eglBaseContext)
        val source = factory.createVideoSource(false)
        capturer.initialize(surfaceTextureHelper, context, source.capturerObserver)
        capturer.startCapture(640, 480, 30)
        localVideoTrack = factory.createVideoTrack("local", source)

        Log.d("WebRTC", "Local video track created")
        onLocalStream(localVideoTrack!!)
    }


    open class PeerConnectionObserverAdapter : PeerConnection.Observer {
        override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
        override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {}
        override fun onIceConnectionReceivingChange(p0: Boolean) {}
        override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
        override fun onIceCandidate(p0: IceCandidate?) {}
        override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {}
        override fun onAddStream(p0: MediaStream?) {}
        override fun onRemoveStream(p0: MediaStream?) {}
        override fun onDataChannel(p0: DataChannel?) {}
        override fun onRenegotiationNeeded() {}
        override fun onAddTrack(receiver: RtpReceiver?, mediaStreams: Array<out MediaStream>?) {}
    }


    fun createPeerConnection(iceServers: List<PeerConnection.IceServer>, signaling: Signaling) {
        peerConnection = factory.createPeerConnection(iceServers, object : PeerConnectionObserverAdapter() {
            override fun onIceCandidate(c: IceCandidate?) {
                c?.let { signaling.sendIceCandidate(it) }
            }

            override fun onAddStream(stream: MediaStream?) {
                stream?.videoTracks?.firstOrNull()?.let(onRemoteStream)
            }
        })?.also {
            val videoTrack = localVideoTrack ?: return
            val stream = factory.createLocalMediaStream("local")
            stream.addTrack(videoTrack)
            it.addStream(stream)

        }

    }

    fun createOffer(options: MediaConstraints, signaling: Signaling) {
        peerConnection?.createOffer(object : SdpObserverAdapter() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                peerConnection?.setLocalDescription(SdpObserverAdapter(), desc)
                desc?.let { signaling.sendOffer(it) }
            }
        }, options)
    }

    fun createAnswer(options: MediaConstraints, signaling: Signaling) {
        peerConnection?.createAnswer(object : SdpObserverAdapter() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                peerConnection?.setLocalDescription(SdpObserverAdapter(), desc)
                desc?.let { signaling.sendAnswer(it) }
            }
        }, options)
    }

    fun setRemoteDescription(desc: SessionDescription) {
        peerConnection?.setRemoteDescription(SdpObserverAdapter(), desc)
    }

    fun addIceCandidate(candidate: IceCandidate) {
        peerConnection?.addIceCandidate(candidate)
    }

    fun close() {
        peerConnection?.close()
        factory.dispose()
    }
}

open class SdpObserverAdapter : SdpObserver {
    override fun onCreateSuccess(sessionDescription: SessionDescription?) {}
    override fun onSetSuccess() {}
    override fun onCreateFailure(error: String?) {}
    override fun onSetFailure(error: String?) {}
}

