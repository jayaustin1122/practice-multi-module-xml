package com.projects.authsignin.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import com.projects.authsignin.databinding.FragmentSignInBinding
import com.projects.authsignin.presentation.viewmodel.SignInViewModel
import com.projects.ui.base.BaseFragment
import com.projects.ui.theme.AppTheme
import com.projects.ui.theme.AppThemeProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding, SignInViewModel>() {

    @Inject
    lateinit var appThemeProvider: AppThemeProvider

    override fun initViews() {
        super.initViews()
        setupUI()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        val appTheme = arguments?.getSerializable("app_theme") as? AppTheme ?: getDefaultTheme()
        val themeResources = appThemeProvider.getThemeResources(appTheme)
        Log.d("Testing", "setupUI: Theme: $appTheme, Resources: $themeResources")
        binding.logo.setImageResource(themeResources.logoRes)
    }

    private fun getDefaultTheme(): AppTheme {
        return when (requireContext().packageName) {
            "com.projects.app2" -> AppTheme.APP2
            "com.projects.practicemultimodulexml" -> AppTheme.APP1
            else -> AppTheme.APP2
        }
    }
}