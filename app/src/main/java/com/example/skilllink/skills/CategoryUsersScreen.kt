package com.example.skilllink.skills

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryUsersScreen(category: String, navController: NavController) {
    val users = remember { mutableStateListOf<Pair<String, Map<String, Any>>>() }
    val loading = remember { mutableStateOf(true) }

    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(category) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("category", category)
            .get()
            .addOnSuccessListener { result ->
                users.clear()
                users.addAll(
                    result.documents
                        .filter { it.id != currentUserUid }
                        .mapNotNull { doc -> doc.data?.let { doc.id to it } }
                )

                loading.value = false
            }
            .addOnFailureListener {
                loading.value = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Users in $category") })
        }
    ) { padding ->
        if (loading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(users) { (uid, user) ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Name: ${user["name"]}")
                            Text(text = "Role: ${user["role"]}")
                            Text(text = "Location: ${user["location"]}")

                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("recipientId", uid)
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("currentUserId", currentUserUid)
                                    navController.navigate("chat")
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Message")
                            }
                        }
                    }
                }
            }
        }
    }
}
