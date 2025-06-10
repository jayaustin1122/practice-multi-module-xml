package com.projects.core.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.projects.core.Response
import com.projects.core.data.source.FirebaseAuthDataSource
import com.projects.core.data.source.FirestoreUserDataSource
import com.projects.core.domain.model.User
import com.projects.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val userDataSource: FirestoreUserDataSource
) : AuthRepository {

    override fun signUp(email: String, password: String): Flow<Response<User>> = flow {
        emit(Response.Loading())
        authDataSource.signUp(email, password).collect { result ->
            when (result) {
                is Response.Success -> {
                    val userId = result.data.user?.uid ?: ""
                    val user = User(id = userId, email = email)
                    userDataSource.createUser(user).collect { userResource ->
                        emit(userResource)
                    }
                }

                is Response.Loading -> emit(Response.Loading())
                is Response.Error -> emit(Response.Error(result.message))
            }
        }
    }

    override fun login(email: String, password: String): Flow<Response<User>> = flow {
        emit(Response.Loading())
        authDataSource.login(email, password).collect { result ->
            when (result) {
                is Response.Success -> {
                    val userId = result.data.user?.uid
                    if (userId != null) {
                        userDataSource.getUser(userId).collect { userResource ->
                            emit(userResource)
                        }
                    } else {
                        emit(Response.Error("Authentication failed"))
                    }
                }

                is Response.Loading -> emit(Response.Loading())
                is Response.Error -> emit(Response.Error(result.message))
            }
        }
    }

    override suspend fun logout(): Response<Unit> {
        return try {
            authDataSource.logout()
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e.message ?: "Logout failed")
        }
    }

    override fun getUser(): Flow<Response<User>> = flow {
        emit(Response.Loading())
        val userId = authDataSource.getCurrentUserId()
        if (userId != null) {
            userDataSource.getUser(userId).collect { userResource ->
                emit(userResource)
            }
        } else {
            emit(Response.Error("No user logged in"))
        }
    }

    override fun updateUser(user: User): Flow<Response<User>> = flow {
        emit(Response.Loading())
        userDataSource.updateUser(user).collect { userResource ->
            emit(userResource)
        }
    }

    override fun isUserAuthenticated(): Boolean {
        return authDataSource.isUserAuthenticated()
    }

    private suspend fun <T> withTimeout(timeMillis: Long = 10000, block: suspend () -> T): T {
        return kotlinx.coroutines.withTimeoutOrNull(timeMillis) {
            block()
        } ?: throw TimeoutException("Operation timed out after $timeMillis ms")
    }

    override fun signInWithGoogle(credential: AuthCredential): Flow<Response<User>> = flow {
        emit(Response.Loading())
        try {
            val authResult = withTimeout(5000) {
                var finalResult: Response<FirebaseUser>? = null
                authDataSource.signInWithCredential(credential).collect { result ->
                    if (result !is Response.Loading) {
                        finalResult = result
                    }
                }
                finalResult ?: Response.Error<FirebaseUser>("No result received")
            }

            when (authResult) {
                is Response.Success -> {
                    val firebaseUser = authResult.data
                    val userId = firebaseUser.uid

                    val userResult = withTimeout(5000) {
                        var user: Response<User>? = null
                        userDataSource.getUser(userId).collect { result ->
                            if (result !is Response.Loading) {
                                user = result
                            }
                        }
                        user ?: Response.Error<User>("Failed to retrieve user data")
                    }

                    when (userResult) {
                        is Response.Success -> {
                            emit(userResult)
                        }

                        is Response.Error -> {
                            // User doesn't exist in Firestore, create a new one
                            val newUser = User(
                                id = userId,
                                email = firebaseUser.email ?: "",
                                displayName = firebaseUser.displayName ?: "",
                                photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                            )

                            withTimeout(5000) {
                                var createResult: Response<User>? = null
                                userDataSource.createUser(newUser).collect { result ->
                                    if (result !is Response.Loading) {
                                        createResult = result
                                    }
                                }
                                createResult ?: Response.Error<User>("Failed to create user")
                            }
                        }

                        is Response.Loading -> {
                            emit(Response.Error("Unexpected loading state"))
                        }
                    }
                }

                is Response.Error -> {
                    emit(Response.Error(authResult.message))
                }

                is Response.Loading -> {
                    emit(Response.Error("Unexpected loading state"))
                }
            }
        } catch (e: TimeoutException) {
            emit(Response.Error("Operation timed out. Please check your network connection and try again."))
        } catch (e: Exception) {
            emit(Response.Error(e.message ?: "An unknown error occurred"))
        }
    }
}