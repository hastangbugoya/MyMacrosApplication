plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.example.mymacrosapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mymacrosapplication"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        debug {
//            val key: String = project.findProperty("USDA_FDA_API_KEY") as? String ?: ""
            buildConfigField("String", "USDA_FDA_API_KEY", "\"0seh3Ezb6GrQW1JpdU6RVlV9lpcDkcCZSrxkxTdh\"")
            buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"AIzaSyC2s-ZMnSSIU3at93hIei0OVnxYDmimfYA\"")

            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
//            val key: String = project.findProperty("USDA_FDA_API_KEY") as? String ?: ""
            buildConfigField("String", "USDA_FDA_API_KEY", "\"seh3Ezb6GrQW1JpdU6RVlV9lpcDkcCZSrxkxTdh\"")
            buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"AIzaSyC2s-ZMnSSIU3at93hIei0OVnxYDmimfYA\"")

            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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

    composeOptions {
        // optionally set compiler extension version if needed
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM ensures all compose libs align
    implementation(platform(libs.androidx.compose.bom))

    // UI, graphics, tooling
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Material3
    implementation(libs.androidx.compose.material3)

    // (Optional) Material icons
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    implementation("androidx.compose.material:material-icons-extended-android:1.7.8")

    // Hilt & integration
    implementation("com.google.dagger:hilt-android:2.57.2")
    implementation(libs.androidx.foundation)
    kapt("com.google.dagger:hilt-compiler:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // CameraX, ML Kit, etc.
    val cameraXVersion = "1.5.0"
    implementation("androidx.camera:camera-core:$cameraXVersion")
    implementation("androidx.camera:camera-camera2:$cameraXVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraXVersion")
    implementation("androidx.camera:camera-view:1.3.4")

    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    // Permission handling / other libs
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // Room
    val roomVersion = "2.8.1"
    implementation("androidx.room:room-runtime:$roomVersion") // or latest stable
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Google Maps
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

    // Splashscreen
    implementation("androidx.core:core-splashscreen:1.0.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
