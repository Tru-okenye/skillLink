package com.example.skilllink.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "", // "teacher", "learner", or "both"
    val skillsToTeach: List<String> = emptyList(),
    val skillsToLearn: List<String> = emptyList(),
    val location: String = "",
    val availability: String = "",
    val profileImageUrl: String? = null,
    val category: String = ""
)

