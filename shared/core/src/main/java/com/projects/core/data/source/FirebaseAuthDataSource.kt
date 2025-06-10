package com.projects.core.data.source

import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.projects.core.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirebaseAuthDataSource @Inject constructor() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signUp(email: String, password: String): Flow<Response<AuthResult>> = flow {
        emit(Response.Loading())
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            emit(Response.Success(result))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Authentication failed"))
        }
    }

    fun login(email: String, password: String): Flow<Response<AuthResult>> = flow {
        emit(Response.Loading())
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            emit(Response.Success(result))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Login failed"))
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }

    fun signInWithCredential(credential: AuthCredential): Flow<Response<FirebaseUser>> = flow {
        emit(Response.Loading())
        try {
            val result = auth.signInWithCredential(credential).await()
            result.user?.let {
                emit(Response.Success(it))
            } ?: emit(Response.Error("Sign-in failed: No user returned"))
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Sign-in error", e)
            emit(Response.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}