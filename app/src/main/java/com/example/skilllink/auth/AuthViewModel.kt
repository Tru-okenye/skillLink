package com.example.skilllink.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilllink.data.model.User
import com.example.skilllink.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _currentUser.value = null
        _authState.value = AuthState.Idle
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        role: String,
        skillsToTeach: String,
        skillsToLearn: String,
        location: String,
        availability: String,
        category: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUpUser(
                name = name,
                email = email,
                password = password,
                role = role,
                skillsToTeach = skillsToTeach,
                skillsToLearn = skillsToLearn,
                location = location,
                availability = availability,
                category = category
            )
            _authState.value = result.fold(
                onSuccess = {
                    _currentUser.value = it
                    AuthState.Success("Registration successful")
                },
                onFailure = {
                    AuthState.Error(it.message ?: "Unknown error")
                }
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.loginUser(email, password)
            _authState.value = result.fold(
                onSuccess = {
                    fetchUser(it)
                    AuthState.Success("Login successful")
                },
                onFailure = {
                    AuthState.Error(it.message ?: "Login failed")
                }
            )
        }
    }

    private fun fetchUser(uid: String) {
        viewModelScope.launch {
            val result = repository.getUser(uid)
            result.onSuccess { user -> _currentUser.value = user }
        }
    }
}
