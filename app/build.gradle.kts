plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.duridudu.oneone2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.duridudu.oneone2"
        minSdk = 26
        targetSdk = 34
        versionCode = 1003
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
//    signingConfigs {
//        release {
//            keyAlias = "oneone2"
//            keyPassword = "931026"
//            storeFile =  file("oneone2keystore.jks")
//            storePassword = "931026"
//        }
//    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            release {
//                signingConfig = signingConfigs.release
//                // 다른 release build 설정들...
//            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth-ktx")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:20.7.0")


    //Room
    implementation("androidx.room:room-runtime:2.4.3")
    kapt ("androidx.room:room-compiler:2.4.3")
    implementation("androidx.room:room-ktx:2.4.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
    kapt("org.xerial:sqlite-jdbc:3.34.0")

    //viewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")

    // firebase real time database
    implementation("com.google.firebase:firebase-database-ktx:20.0.4")

    // Toast
    implementation("io.github.muddz:styleabletoast:2.4.0")
}