plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.myfaceapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myfaceapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {
    val camerax_version = "1.4.0-alpha05"
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.kroegerama:bottomsheet-imagepicker:1.1.2")


    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")

    implementation("androidx.camera:camera-lifecycle:${camerax_version}")

    implementation("androidx.camera:camera-video:${camerax_version}")

    implementation("androidx.camera:camera-view:${camerax_version}")

    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")

    implementation("androidx.camera:camera-extensions:${camerax_version}")



    implementation("com.github.dhaval2404:imagepicker:2.1")

    implementation ("commons-io:commons-io:2.4")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")


    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


}