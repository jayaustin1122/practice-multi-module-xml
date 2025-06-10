package com.projects.core.domain.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false
)