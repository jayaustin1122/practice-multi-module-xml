pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Practice Multi Module XML"
include(":app")
include(":app2")
include(":feature:auth-signin")
include(":core")
include(":shared:authsignin")
include(":shared:authsignup")
include(":shared:core")
include(":shared:ui")
