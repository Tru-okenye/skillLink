package com.example.skilllink.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skilllink.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val user by viewModel.user.collectAsState()
    val loading by viewModel.loading.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var skillsToTeach by remember { mutableStateOf("") }
    var skillsToLearn by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    val categoryOptions = listOf("Technology", "Business", "Arts", "Health", "Other")
    var expanded by remember { mutableStateOf(false) }

    // Fetch user profile once
    LaunchedEffect(true) {
        viewModel.fetchUserProfile()
    }

    // Update local form state when user data changes
    LaunchedEffect(user) {
        name = user.name
        email = user.email
        role = user.role
        skillsToTeach = user.skillsToTeach.joinToString(", ")
        skillsToLearn = user.skillsToLearn.joinToString(", ")
        location = user.location
        availability = user.availability
        category = user.category ?: ""
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Edit Profile", style = MaterialTheme.typography.headlineMedium)

                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })

                    Text("Role:")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("teacher", "learner", "both").forEach {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = role == it,
                                    onClick = { role = it }
                                )
                                Text(it.replaceFirstChar(Char::uppercaseChar))
                            }
                        }
                    }

                    if (role != "learner") {
                        OutlinedTextField(
                            value = skillsToTeach,
                            onValueChange = { skillsToTeach = it },
                            label = { Text("Skills to Teach") }
                        )
                    }

                    if (role != "teacher") {
                        OutlinedTextField(
                            value = skillsToLearn,
                            onValueChange = { skillsToLearn = it },
                            label = { Text("Skills to Learn") }
                        )
                    }

                    OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
                    OutlinedTextField(value = availability, onValueChange = { availability = it }, label = { Text("Availability") })

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categoryOptions.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        category = selectionOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val updatedUser = user.copy(
                                name = name,
                                email = email,
                                role = role,
                                skillsToTeach = skillsToTeach.split(",").map { it.trim() },
                                skillsToLearn = skillsToLearn.split(",").map { it.trim() },
                                location = location,
                                availability = availability,
                                category = category
                            )
                            viewModel.updateUserProfile(updatedUser)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Profile")
                    }
                }
            }
        }
    }
}
