package com.projects.authsignin.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
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
       // observeViewModel()
    }

    private fun setupUI() {
        val appTheme = arguments?.getSerializable("app_theme") as? AppTheme ?: AppTheme.APP1
        val themeResources = appThemeProvider.getThemeResources(appTheme)

        // Apply theme
        binding.logo.setImageResource(themeResources.logoRes)
    //    binding.signInBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), themeResources.primaryColor))

//        binding.signInBtn.setOnClickListener {
//            val email = binding.emailEt.text.toString().trim()
//            val password = binding.passwordEt.text.toString().trim()
//
//            if (validateInput(email, password)) {
//                viewModel.signIn(email, password)
//            }
//        }
    }

//    private fun observeViewModel() {
//        viewModel.uiState.observe(viewLifecycleOwner) { state ->
//            when (state) {
//                is SignInUiState.Loading -> {
//                    binding.progressBar.visibility = View.VISIBLE
//                    binding.signInBtn.isEnabled = false
//                }
//                is SignInUiState.Success -> {
//                    binding.progressBar.visibility = View.GONE
//                    binding.signInBtn.isEnabled = true
//                    // Navigate to main screen
//                    findNavController().navigate(R.id.action_signIn_to_main)
//                }
//                is SignInUiState.Error -> {
//                    binding.progressBar.visibility = View.GONE
//                    binding.signInBtn.isEnabled = true
//                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
//                }
//                else -> {
//                    binding.progressBar.visibility = View.GONE
//                    binding.signInBtn.isEnabled = true
//                }
//            }
//        }
//    }

    private fun validateInput(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }
}