plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-kapt")
    id ("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.projects.practicemultimodulexml"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.projects.practicemultimodulexml"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("app1") {
            dimension = "version"
            applicationId = "com.projects.practicemultimodulexml"
            manifestPlaceholders["appName"] = "App1"
            buildConfigField("String", "APP_THEME", "\"APP1\"")
        }

        create("app2") {
            dimension = "version"
            applicationId = "com.projects.app2"
            manifestPlaceholders["appName"] = "App2"
            buildConfigField("String", "APP_THEME", "\"APP2\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(":shared:core"))
    implementation (project(":shared:ui"))
    implementation (project(":shared:authsignin"))
    implementation (project(":shared:authsignup"))


    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
}