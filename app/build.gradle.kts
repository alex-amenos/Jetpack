plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = AppConfig.COMPILE_SDK_VERSION
    buildToolsVersion = AppConfig.BUILD_TOOLS_VERSION

    defaultConfig {
        applicationId = AppConfig.APPLICATION_ID
        minSdk = AppConfig.MIN_SDK_VERSION
        targetSdk = AppConfig.TARGET_SDK_VERSION
        versionCode = AppConfig.VERSION_CODE
        versionName = AppConfig.VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = AppConfig.sourceCompatJavaVersion
        targetCompatibility = AppConfig.targetCompatJavaVersion
    }

    kotlinOptions {
        jvmTarget = AppConfig.kotlinOptionJavaVersion
        useIR = true
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Version.ANDROIDX_COMPOSE
    }
}

dependencies {
    implementation(Dep.ANDROIDX_ACTIVITY_COMPOSE)
    implementation(Dep.ANDROIDX_APP_COMPAT)
    implementation(Dep.ANDROIDX_COMPOSE_UI)
    implementation(Dep.ANDROIDX_COMPOSE_MATERIAL)
    implementation(Dep.ANDROIDX_COMPOSE_UI_TOOLING)
    implementation(Dep.ANDROIDX_CORE_KTX)
    implementation(Dep.ANDROIDX_LIFECYCLE)
    implementation(Dep.GOOGLE_MATERIAL)
    implementation(Dep.KOTLIN_STDLIB)

    testImplementation(Dep.JUNIT)

    androidTestImplementation(Dep.ANDROIDX_COMPOSE_TEST)
    androidTestImplementation(Dep.ANDROIDX_ESPRESSO)
    androidTestImplementation(Dep.ANDROIDX_JUNIT)
}
