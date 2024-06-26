plugins {
    id("com.android.application")
    // Google services gradle plugin
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "it.unimib.readify"
    compileSdk = 34

    defaultConfig {
        applicationId = "it.unimib.readify"
        minSdk = 24
        targetSdk = 34
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    val navVersion = "2.7.7"
    val roomVersion = "2.6.1"
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.2")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.palette:palette:1.0.0")

    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.android.gms:play-services-base:18.5.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("commons-io:commons-io:2.15.0")
    implementation("commons-validator:commons-validator:1.7")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Module for loading images
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Module for rounded image views
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    // Analytics
    implementation("com.google.firebase:firebase-analytics")
    // Authentication
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.android.gms:play-services-auth:21.1.0")
    // Realtime Database library
    implementation("com.google.firebase:firebase-database")

    annotationProcessor("androidx.room:room-compiler:$roomVersion")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
