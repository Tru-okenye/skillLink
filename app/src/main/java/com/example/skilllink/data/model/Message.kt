package com.example.skilllink.data.model



data class Message(
    val id: String = "",
    val senderId: String = "",
    val recipientId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val delivered: Boolean = false,
    val seen: Boolean = false
)

