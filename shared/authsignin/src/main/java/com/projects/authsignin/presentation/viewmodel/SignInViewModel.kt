package com.projects.authsignin.presentation.viewmodel

import com.projects.core.domain.usecase.SignUpUseCase
import com.projects.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : BaseViewModel() {

    fun signUp(email: String, password: String) = signUpUseCase(email, password)
}
