package com.example.skilllink.profile


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilllink.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun fetchUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        _loading.value = true

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                document.toObject(User::class.java)?.let {
                    _user.value = it
                }
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
            }
    }

    fun updateUserProfile(updatedUser: User) {
        val uid = auth.currentUser?.uid ?: return
        _loading.value = true

        firestore.collection("users").document(uid).set(updatedUser)
            .addOnSuccessListener {
                _user.value = updatedUser
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
            }
    }
}
