plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")

}

android {
    compileSdk = 36
    namespace = "com.example.alcoholtracker"

    defaultConfig {
        applicationId = "com.example.alcoholtracker"
        minSdk = 32
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    kotlin{
        jvmToolchain(11)
    }

}

dependencies {
    implementation("androidx.room:room-runtime:2.8.1")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.9.2")
    implementation("com.google.firebase:firebase-auth:24.0.1")
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("androidx.media3:media3-common-ktx:1.8.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("androidx.compose.ui:ui-graphics:1.9.2")
    testImplementation("junit:junit:4.13.2")
    ksp("androidx.room:room-compiler:2.8.1")
    implementation("com.google.code.gson:gson:2.13.2")

    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("com.seanproctor:data-table-material3:0.11.4")
    implementation("androidx.compose.ui:ui:1.9.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("com.google.dagger:hilt-android:2.57.1")
    implementation("androidx.hilt:hilt-navigation-fragment:1.3.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    implementation("androidx.room:room-ktx:2.8.1")
    implementation("io.github.ivamsi:snapnotify:1.0.6")

    // Jetpack Compose Integration
    implementation("androidx.navigation:navigation-compose:2.9.5")

    // Views/Fragments Integration
    implementation("androidx.navigation:navigation-fragment:2.9.5")
    implementation( "androidx.navigation:navigation-ui:2.9.5")

    // Feature module support for Fragments
    implementation( "androidx.navigation:navigation-dynamic-features-fragment:2.9.5")

    // Testing Navigation
    androidTestImplementation( "androidx.navigation:navigation-testing:2.9.5")

    // JSON serialization library, works with the Kotlin serialization plugin.
    implementation( "org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")



    ksp("com.google.dagger:hilt-android-compiler:2.57.1")
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("androidx.room:room-common:2.8.1")
    implementation(platform("androidx.compose:compose-bom:2025.09.01"))
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-android:1.4.0")
    implementation("com.google.android.material:material:1.13.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.09.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")


}

