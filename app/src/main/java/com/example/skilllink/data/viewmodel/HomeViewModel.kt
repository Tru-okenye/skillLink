package com.example.skilllink.data.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            db.collection("users").get()
                .addOnSuccessListener { result ->
                    val uniqueCategories = result.documents
                        .mapNotNull { it.getString("category") }
                        .toSet()
                        .toList()
                    _categories.value = uniqueCategories
                }
                .addOnFailureListener {
                    _categories.value = emptyList()
                }
        }
    }
}
