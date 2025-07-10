package com.example.skilllink.chat


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.skilllink.data.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    currentUserId: String,
    recipientId: String,
    navController: NavHostController
) {
    val viewModel = remember { ChatViewModel(currentUserId = currentUserId) }
    val messages by viewModel.messages.collectAsState()
    var input by remember { mutableStateOf("") }

    // Initialize chat and handle visibility
    LaunchedEffect(Unit) {
        viewModel.initChat(userA = currentUserId, userB = recipientId)
        viewModel.setChatVisibility(true)
        viewModel.resetUnseenCount()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setChatVisibility(false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            items(messages.reversed()) { msg ->
                val isSender = msg.senderId == currentUserId
                val alignment = if (isSender) Alignment.End else Alignment.Start
                val backgroundColor = if (isSender) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                val textColor = if (isSender) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalAlignment = alignment
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start
                    ) {
                        if (!isSender) {
                            InitialCircle(initial = msg.senderId.take(1).uppercase())
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Surface(
                            color = backgroundColor,
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = 2.dp,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = msg.message,
                                    color = textColor,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatTimestamp(msg.timestamp),
                                    fontSize = 11.sp,
                                    color = textColor.copy(alpha = 0.7f),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        if (isSender) {
                            val statusText = when {
                                msg.seen -> "Seen"
                                msg.delivered -> "Delivered"
                                else -> "Sent"
                            }
                            Text(
                                text = statusText,
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val callId = UUID.randomUUID().toString()
                navController.navigate("video_call/$callId")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Start Video Call")
        }



        Spacer(modifier = Modifier.height(8.dp))



        Row {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        viewModel.sendMessage(currentUserId, recipientId, input)
                        input = ""
                    }
                }
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun InitialCircle(initial: String) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
