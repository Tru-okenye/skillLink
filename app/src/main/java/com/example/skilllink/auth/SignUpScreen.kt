package com.example.skilllink.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.text.input.KeyboardType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("teacher") }
    var skillsToTeach by remember { mutableStateOf("") }
    var skillsToLearn by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("") }

    // Error states
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val skillCategories = listOf("IT", "Business", "Art", "Language", "Science", "Health", "Engineering")
    var selectedCategory by remember { mutableStateOf(skillCategories.first()) }


    val authState by authViewModel.authState.collectAsState()


    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate("login") {
                popUpTo("signup") { inclusive = true }
            }
            authViewModel.resetState()
        }
    }



    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
//                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize() // Let Card fill the screen
                    .padding(16.dp), // Optional inner padding
                elevation = CardDefaults.cardElevation(6.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    item {
                        Text("Create an Account", style = MaterialTheme.typography.headlineSmall)
                    }

                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                nameError = if (it.isBlank()) "Name is required" else null
                            },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            isError = nameError != null,
                            supportingText = { nameError?.let { Text(it) } }
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError =
                                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches())
                                        "Invalid email address" else null
                            },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(10.dp),
                            isError = emailError != null,
                            supportingText = { emailError?.let { Text(it) } }
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError =
                                    if (it.length < 6) "Password must be at least 6 characters" else null
                            },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(10.dp),
                            isError = passwordError != null,
                            supportingText = { passwordError?.let { Text(it) } }
                        )
                    }

                    item {
                        Text("Select Skill Category", style = MaterialTheme.typography.bodyLarge)

                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                readOnly = true,
                                value = selectedCategory,
                                onValueChange = {},
                                label = { Text("Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                skillCategories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category) },
                                        onClick = {
                                            selectedCategory = category
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }



                    item {
                        Text("I am registering as:")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("teacher", "learner", "both").forEach {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = role == it,
                                        onClick = { role = it }
                                    )
                                    Text(it.replaceFirstChar { c -> c.uppercaseChar() })
                                }
                            }
                        }
                    }

                    if (role == "teacher" || role == "both") {
                        item {
                            OutlinedTextField(
                                value = skillsToTeach,
                                onValueChange = { skillsToTeach = it },
                                label = { Text("Skills to Teach") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    if (role == "learner" || role == "both") {
                        item {
                            OutlinedTextField(
                                value = skillsToLearn,
                                onValueChange = { skillsToLearn = it },
                                label = { Text("Skills to Learn") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Location") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = availability,
                            onValueChange = { availability = it },
                            label = { Text("Availability") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                val valid = name.isNotBlank() &&
                                        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                                        password.length >= 6

                                if (valid) {
                                    authViewModel.signUp(
                                        name = name,
                                        email = email,
                                        password = password,
                                        role = role,
                                        skillsToTeach = skillsToTeach,
                                        skillsToLearn = skillsToLearn,
                                        location = location,
                                        availability = availability,
                                        category = selectedCategory
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = authState !is AuthState.Loading

                        ) {
                            if (authState is AuthState.Loading){
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Sign Up")
                            }
                        }

                        when (authState) {
                            is AuthState.Error -> {
                                Spacer(Modifier.height(8.dp))
                                Text((authState as AuthState.Error).message, color = MaterialTheme.colorScheme.error)
                            }
                            is AuthState.Success -> {
                                Spacer(Modifier.height(8.dp))
                                Text((authState as AuthState.Success).message, color = MaterialTheme.colorScheme.primary)
                            }
                            else -> {}
                        }


                        Spacer(Modifier.height(16.dp))
                        TextButton(onClick = {
                            navController.navigate("login") {
                                popUpTo("signup") { inclusive = true }
                            }
                        }) {
                            Text("Already have an account? Log In")
                        }
                    }
                }
            }
        }
    }
}
