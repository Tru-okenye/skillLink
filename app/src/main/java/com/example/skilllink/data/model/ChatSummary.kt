package com.example.skilllink.data.model


data class ChatSummary(
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    val lastMessage: String,
    val timestamp: Long = 0L,
    val unseenCount: Int = 0
)
