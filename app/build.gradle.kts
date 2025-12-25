plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.oyn.rencangku"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.oyn.rencangku"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "WEATHER_API_KEY",
            "\"${project.findProperty("WEATHER_API_KEY")}\""
        )
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
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation (libs.jetbrains.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.places)
    implementation(libs.places)
    implementation(libs.play.services.fitness)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.androidx.cardview)
    implementation (libs.material.v1100)
    implementation (libs.github.glide)

    //Reforfit
    implementation (libs.retrofit)
    implementation (libs.retrofit2.converter.gson)

    //Maps
    implementation (libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation (libs.android.maps.utils)
    implementation (libs.play.services.tasks)

    //Animasi dan splasscreen
    implementation (libs.androidx.core.splashscreen)
    implementation (libs.github.glide)
    annotationProcessor (libs.compiler)
}