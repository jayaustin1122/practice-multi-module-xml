package com.projects.ui.theme

import com.projects.ui.R
import javax.inject.Inject
import javax.inject.Singleton
enum class AppTheme {
    APP1, APP2
}

@Singleton
class AppThemeProvider @Inject constructor() {

    fun getThemeResources(appTheme: AppTheme): ThemeResources {
        return when (appTheme) {
            AppTheme.APP1 -> ThemeResources(
                primaryColor = R.color.teal_200,
                accentColor = R.color.teal_200,
                logoRes = R.drawable.ic_launcher_background
            )
            AppTheme.APP2 -> ThemeResources(
                primaryColor = R.color.teal_200,
                accentColor = R.color.teal_200,
                logoRes = R.drawable.ic_launcher_foreground
            )
        }
    }
}

data class ThemeResources(
    val primaryColor: Int,
    val accentColor: Int,
    val logoRes: Int
)