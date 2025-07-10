package com.example.skilllink.data.repository


import com.example.skilllink.data.model.ChatSummary
import com.example.skilllink.data.model.Message
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun getOrCreateChat(userA: String, userB: String, onComplete: (String) -> Unit) {
        db.collection("chats")
            .whereArrayContains("participants", userA)
            .get()
            .addOnSuccessListener { result ->
                val chat = result.documents.firstOrNull {
                    val participants = it.get("participants") as? List<*>
                    participants?.contains(userB) == true
                }

                if (chat != null) {
                    onComplete(chat.id)
                } else {
                    val newChat = mapOf(
                        "participants" to listOf(userA, userB),
                        "lastMessage" to "",
                        "lastTimestamp" to System.currentTimeMillis()
                    )
                    db.collection("chats").add(newChat).addOnSuccessListener {
                        onComplete(it.id)
                    }
                }
            }
    }

    fun sendMessage(chatId: String, senderId: String, recipientId: String, text: String) {
        val timestamp = System.currentTimeMillis()

        val message = Message(
            senderId = senderId,
            recipientId = recipientId,
            message = text,
            timestamp = timestamp,
            delivered = false,
            seen = false
        )

        val chatRef = db.collection("chats").document(chatId)
        val messageRef = chatRef.collection("messages").document()

        db.runBatch { batch ->
            batch.set(messageRef, message)

            batch.update(chatRef, mapOf(
                "lastMessage" to text,
                "lastTimestamp" to timestamp,
                "${recipientId}_unseenCount" to FieldValue.increment(1)
            ))
        }
    }

    fun listenForMessages(
        chatId: String,
        listenerUserId: String,
        onMessagesReceived: (List<Message>) -> Unit
    ) {
        db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull {
                        it.toObject(Message::class.java)?.copy(id = it.id)
                    }

                    // Update delivery status for messages the current user receives
                    messages.filter {
                        !it.delivered && it.senderId != listenerUserId
                    }.forEach { msg ->
                        db.collection("chats").document(chatId)
                            .collection("messages").document(msg.id)
                            .update("delivered", true)
                    }

                    onMessagesReceived(messages)
                }
            }
    }

    fun markMessageAsSeen(chatId: String, messageId: String) {
        db.collection("chats").document(chatId)
            .collection("messages").document(messageId)
            .update("seen", true)
    }

    fun getUserChatSummaries(currentUserId: String, callback: (List<ChatSummary>) -> Unit) {
        db.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .get()
            .addOnSuccessListener { snapshots ->
                if (snapshots != null && !snapshots.isEmpty) {
                    val chatSummaries = mutableListOf<ChatSummary>()
                    var pendingFetches = snapshots.size()

                    snapshots.documents.forEach { doc ->
                        val chatId = doc.id
                        val participants = doc.get("participants") as? List<*> ?: emptyList<String>()
                        val otherUserId = participants.filterIsInstance<String>().firstOrNull { it != currentUserId }

                        if (otherUserId != null) {
                            db.collection("users").document(otherUserId).get()
                                .addOnSuccessListener { userDoc ->
                                    val otherUserName = userDoc.getString("name") ?: "Unknown"

                                    doc.reference.collection("messages")
                                        .orderBy("timestamp", Query.Direction.DESCENDING)
                                        .limit(1)
                                        .get()
                                        .addOnSuccessListener { messagesSnapshot ->
                                            val lastMsgDoc = messagesSnapshot.documents.firstOrNull()
                                            val lastMessage = lastMsgDoc?.getString("message") ?: ""
                                            val timestamp = lastMsgDoc?.getLong("timestamp") ?: 0L
                                            val unseenCount = doc.getLong("${currentUserId}_unseenCount")?.toInt() ?: 0

                                            val summary = ChatSummary(
                                                chatId = chatId,
                                                otherUserId = otherUserId,
                                                otherUserName = otherUserName,
                                                lastMessage = lastMessage,
                                                timestamp = timestamp,
                                                unseenCount = unseenCount
                                            )
                                            chatSummaries.add(summary)
                                        }
                                        .addOnCompleteListener {
                                            pendingFetches--
                                            if (pendingFetches == 0) {
                                                callback(chatSummaries.sortedByDescending { it.timestamp })
                                            }
                                        }
                                }
                        } else {
                            pendingFetches--
                            if (pendingFetches == 0) {
                                callback(chatSummaries.sortedByDescending { it.timestamp })
                            }
                        }
                    }
                } else {
                    callback(emptyList())
                }
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}
