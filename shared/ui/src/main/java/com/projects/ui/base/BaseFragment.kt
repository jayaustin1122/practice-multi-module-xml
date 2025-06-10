package com.projects.ui.base

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnAttach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType


abstract class BaseFragment<VB : ViewBinding, VM : ViewModel> : Fragment() {

    private var _binding: VB? = null
    val binding: VB
        get() = _binding ?: throw IllegalStateException("Binding has been destroyed")

    lateinit var viewModel: VM
    private val type = (javaClass.genericSuperclass as ParameterizedType)
    private val classVB = type.actualTypeArguments[0] as Class<VB>
    private val classVM = type.actualTypeArguments[1] as Class<VM>
    private val inflateMethod = classVB.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateMethod.invoke(null, inflater, container, false) as VB
        viewModel = ViewModelProvider(requireActivity())[classVM]
        initViews()
        subscribe()
        return _binding?.root
    }

    fun hideSystemUI() {
        requireActivity().window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        requireActivity().window.statusBarColor = Color.TRANSPARENT
    }

    fun showSystemUI() {
        requireActivity().window.decorView.systemUiVisibility
    }

    fun onBackPress(
        closeActivity: Boolean = false,
        disableBack: Boolean = false,
        block: () -> Unit
    ) {
        requireActivity().onBackPressedDispatcher.addCallback(this, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (closeActivity) requireActivity().finish()

                if (isEnabled && !closeActivity && !disableBack) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
                block()
            }
        })
    }

    @SuppressLint("ObsoleteSdkInt")
    fun setStatusBarItemsColorBasedOnTheme() {
        val context = requireContext()
        val isNightMode =
            when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                else -> false
            }
        setNavigationBarItemsColor(!isNightMode)
        setStatusBarItemsColor(!isNightMode)
    }

    @SuppressLint("ObsoleteSdkInt")
    fun setCustomStatusBarColor(@ColorRes colorId: Int) {
        activity?.window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            val color = ContextCompat.getColor(requireContext(), colorId)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = color
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            if (Build.VERSION.SDK_INT >= 35) {
                statusBarColor = color
            }
        }
    }

    /**
     * Changes the color of status bar icons and text
     *
     * @param isLight true for dark icons (for light backgrounds), false for light icons (for dark backgrounds)
     */
    @SuppressLint("ObsoleteSdkInt")
    fun setStatusBarItemsColor(isLight: Boolean) {
        activity?.window?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val decorView = decorView
                var flags = decorView.systemUiVisibility

                if (isLight) {
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }

                decorView.systemUiVisibility = flags
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = decorView.windowInsetsController
                if (isLight) {
                    controller?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                } else {
                    controller?.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }
            }

            if (Build.VERSION.SDK_INT >= 35) {
                val controller = decorView.windowInsetsController
                if (isLight) {
                    controller?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                } else {
                    controller?.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }
            }
        }
    }

    /**
     * Handles window insets for edge-to-edge content
     * Applies appropriate padding to prevent content from being hidden behind system bars
     *
     * @param view The view to which insets should be applied
     */
    @SuppressLint("ObsoleteSdkInt")
    fun handleWindowInsets(view: View?) {
        if (view == null) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.setOnApplyWindowInsetsListener { v, insets ->
                val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
                val navigationBarInsets = insets.getInsets(WindowInsets.Type.navigationBars())
                v.setPadding(
                    v.paddingLeft,
                    statusBarInsets.top,
                    v.paddingRight,
                    navigationBarInsets.bottom
                )

                insets
            }
        } else {
            // For older versions
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val statusBarInsets = insets.systemWindowInsetTop
                val navigationBarInsets = insets.systemWindowInsetBottom
                v.setPadding(
                    v.paddingLeft,
                    statusBarInsets,
                    v.paddingRight,
                    navigationBarInsets
                )
                insets
            }
        }

        // Request insets to be applied
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.doOnAttach {
                it.requestApplyInsets()
            }
        } else {
            ViewCompat.requestApplyInsets(view)
        }
    }

    /**
     * Enables edge-to-edge display for the fragment
     * Works across different Android versions
     */
    @SuppressLint("ObsoleteSdkInt")
    fun enableEdgeToEdgeDisplay() {
        activity?.window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // For Android 5.0 (API 21) and higher
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = Color.TRANSPARENT
                navigationBarColor = Color.TRANSPARENT
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // For Android 6.0 (API 23) and higher
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // For Android 11 (API 30) and higher
                setDecorFitsSystemWindows(false)

                // Additional option if you want to make the status bar overlay semitransparent
                // decorView.windowInsetsController?.setSystemBarsAppearance(
                //     0,
                //     WindowInsetsController.APPEARANCE_OPAQUE_STATUS_BARS
                // )
            }

            // For Android 15 (API 35) specific handling
            if (Build.VERSION.SDK_INT >= 35) {
                setDecorFitsSystemWindows(false)
                decorView.windowInsetsController?.apply {
                    systemBarsBehavior = WindowInsetsController.BEHAVIOR_DEFAULT
                    show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                }
            }
        }
    }

    /**
     * Handles bottom navigation padding and appearance
     * Configures padding to prevent content from being hidden behind bottom navigation
     *
     * @param view The view to which bottom navigation padding should be applied
     * @param applyPadding Whether to apply padding (true) or not (false)
     */
    @SuppressLint("ObsoleteSdkInt")
    fun handleBottomNavigation(view: View?, applyPadding: Boolean = true) {
        if (view == null) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.setOnApplyWindowInsetsListener { v, insets ->
                val navigationBarInsets = insets.getInsets(WindowInsets.Type.navigationBars())

                if (applyPadding) {
                    v.setPadding(
                        v.paddingLeft,
                        v.paddingTop,
                        v.paddingRight,
                        navigationBarInsets.bottom
                    )
                }

                // Set navigation bar appearance
                val controller = requireActivity().window.decorView.windowInsetsController
                controller?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )

                insets
            }
        } else {
            // For older versions
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val navigationBarInsets = insets.systemWindowInsetBottom

                if (applyPadding) {
                    v.setPadding(
                        v.paddingLeft,
                        v.paddingTop,
                        v.paddingRight,
                        navigationBarInsets
                    )
                }

                insets
            }

            // For Android 6.0+ but below Android 11
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.R
            ) {
                requireActivity().window.decorView.apply {
                    systemUiVisibility =
                        systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }
        }

        // Request insets to be applied
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.doOnAttach {
                it.requestApplyInsets()
            }
        } else {
            ViewCompat.requestApplyInsets(view)
        }
    }

    /**
     * Changes the color of navigation bar icons and buttons
     *
     * @param isLight true for dark icons (for light backgrounds), false for light icons (for dark backgrounds)
     */
    @SuppressLint("ObsoleteSdkInt")
    fun setNavigationBarItemsColor(isLight: Boolean) {
        activity?.window?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val decorView = decorView
                var flags = decorView.systemUiVisibility

                if (isLight) {
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    flags = flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                }

                decorView.systemUiVisibility = flags
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val controller = decorView.windowInsetsController
                if (isLight) {
                    controller?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                } else {
                    controller?.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                }
            }

            if (Build.VERSION.SDK_INT >= 35) {
                val controller = decorView.windowInsetsController
                if (isLight) {
                    controller?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                } else {
                    controller?.setSystemBarsAppearance(
                        0,
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                    )
                }
            }
        }
    }

    /**
     * Sets the background color of the bottom navigation bar
     *
     * @param colorId Resource ID of the color to set for the navigation bar
     */
    @SuppressLint("ObsoleteSdkInt")
    fun setNavigationBarColor(@ColorRes colorId: Int) {
        activity?.window?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // For Android 5.0 (API 21) and higher
                navigationBarColor = ContextCompat.getColor(requireContext(), colorId)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For Android 10 (API 29) and higher, can also set navigation bar divider color
                navigationBarDividerColor = Color.TRANSPARENT
            }

            // For Android 15 (API 35) specific handling
            if (Build.VERSION.SDK_INT >= 35) {
                navigationBarColor = ContextCompat.getColor(requireContext(), colorId)
            }
        }
    }

    open fun initViews() {}

    open fun subscribe() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}