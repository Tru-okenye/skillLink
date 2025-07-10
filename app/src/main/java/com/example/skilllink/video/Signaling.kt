package com.example.skilllink.video


import com.google.firebase.firestore.FirebaseFirestore
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class Signaling(private val db: FirebaseFirestore, private val callId: String) {

    fun sendOffer(desc: SessionDescription) {
        db.collection("calls").document(callId).set(
            mapOf("offer" to mapOf("type" to desc.type.canonicalForm(), "sdp" to desc.description))
        )
    }

    fun sendAnswer(desc: SessionDescription) {
        db.collection("calls").document(callId)
            .update("answer", mapOf("type" to desc.type.canonicalForm(), "sdp" to desc.description))
    }

    fun sendIceCandidate(candidate: IceCandidate) {
        db.collection("calls").document(callId)
            .collection("candidates").add(candidate.toMap())
    }

    private var answered = false

    fun listenForSession(onAnswer: (SessionDescription) -> Unit) {
        db.collection("calls").document(callId)
            .addSnapshotListener { snap, _ ->
                if (!answered && snap?.get("answer") != null) {
                    val m = snap.get("answer") as Map<*, *>
                    val desc = SessionDescription(
                        SessionDescription.Type.fromCanonicalForm(m["type"] as String),
                        m["sdp"] as String
                    )
                    answered = true
                    onAnswer(desc)
                }
            }
    }


    fun listenForIce(onIce: (IceCandidate) -> Unit) {
        db.collection("calls").document(callId)
            .collection("candidates")
            .addSnapshotListener { snap, _ ->
                snap?.documentChanges?.forEach { dc ->
                    val c = dc.document.data.toIceCandidate()
                    onIce(c)
                }
            }
    }
}



fun IceCandidate.toMap() = mapOf(
    "sdpMid" to sdpMid,
    "sdpMLineIndex" to sdpMLineIndex,
    "sdp" to sdp
)

fun Map<String, Any>.toIceCandidate(): IceCandidate = IceCandidate(
    this["sdpMid"] as String,
    (this["sdpMLineIndex"] as Long).toInt(),
    this["sdp"] as String
)
