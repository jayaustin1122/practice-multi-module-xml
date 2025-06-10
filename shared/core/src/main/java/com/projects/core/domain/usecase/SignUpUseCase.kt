package com.projects.core.domain.usecase

import com.projects.core.Response
import com.projects.core.domain.model.User
import com.projects.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(private val repository: AuthRepository) {
    operator fun invoke(email: String, password: String): Flow<Response<User>> {
        return repository.signUp(email, password)
    }
}
