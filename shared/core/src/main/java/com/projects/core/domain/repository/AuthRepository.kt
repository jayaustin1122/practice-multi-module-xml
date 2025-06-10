package com.projects.core.domain.repository

import com.google.firebase.auth.AuthCredential
import com.projects.core.Response
import com.projects.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signUp(email: String, password: String): Flow<Response<User>>
    fun login(email: String, password: String): Flow<Response<User>>
    suspend fun logout(): Response<Unit>
    fun getUser(): Flow<Response<User>>
    fun updateUser(user: User): Flow<Response<User>>
    fun isUserAuthenticated(): Boolean
    fun signInWithGoogle(credential: AuthCredential): Flow<Response<User>>
}