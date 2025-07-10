package com.example.skilllink.data.viewmodel

import androidx.lifecycle.ViewModel
import com.example.skilllink.data.model.Message
import com.example.skilllink.data.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel(
    private val currentUserId: String,
    private val repository: ChatRepository = ChatRepository()
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    var currentChatId: String? = null
        private set

    private var isChatVisible = false

    fun initChat(userA: String, userB: String) {
        repository.getOrCreateChat(userA, userB) { chatId ->
            currentChatId = chatId
            observeMessages(chatId)
        }
    }

    fun resetUnseenCount() {
        currentChatId?.let { id ->
            firestore.collection("chats").document(id)
                .update("${currentUserId}_unseenCount", 0)
        }
    }

    fun setChatVisibility(visible: Boolean) {
        isChatVisible = visible
        if (visible && currentChatId != null) {
            markUnseenMessagesAsSeen(currentChatId!!, _messages.value)
        }
    }

    private fun observeMessages(chatId: String) {
        repository.listenForMessages(chatId, currentUserId) { newMessages ->
            _messages.value = newMessages
            if (isChatVisible) {
                markUnseenMessagesAsSeen(chatId, newMessages)
            }
        }
    }

    private fun markUnseenMessagesAsSeen(chatId: String, messages: List<Message>) {
        // 1. Mark individual unseen messages as seen
        messages.filter { !it.seen && it.senderId != currentUserId }
            .forEach { msg ->
                repository.markMessageAsSeen(chatId, msg.id)
            }

        // 2. Reset unseen count for current user
        firestore.collection("chats").document(chatId)
            .update("${currentUserId}_unseenCount", 0)
    }

    fun markMessageAsSeenIfNeeded(message: Message) {
        if (!message.seen && message.senderId != currentUserId) {
            currentChatId?.let { chatId ->
                repository.markMessageAsSeen(chatId, message.id)
            }
        }
    }

    fun sendMessage(senderId: String, recipientId: String, text: String) {
        currentChatId?.let {
            repository.sendMessage(it, senderId, recipientId, text)
        }
    }
}
