package com.projects.core.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.projects.core.Response
import com.projects.core.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirestoreUserDataSource @Inject constructor() {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("usersmvvmtest")

    fun createUser(user: User): Flow<Response<User>> = flow {
        emit(Response.Loading())
        try {
            usersCollection.document(user.id).set(user).await()
            emit(Response.Success(user))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to create user"))
        }
    }

    fun getUser(userId: String): Flow<Response<User>> = flow {
        emit(Response.Loading())
        try {
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java) ?: User(id = userId)
            emit(Response.Success(user))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to get user"))
        }
    }

    fun updateUser(user: User): Flow<Response<User>> = flow {
        emit(Response.Loading())
        try {
            usersCollection.document(user.id).set(user).await()
            emit(Response.Success(user))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "Failed to update user"))
        }
    }
}