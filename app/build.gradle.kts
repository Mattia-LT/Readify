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

    val navVersion ="2.7.7"
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")


    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.gms:play-services-base:18.3.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("commons-io:commons-io:2.15.0")
    implementation("commons-validator:commons-validator:1.7")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")

    // import firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    // TODO RIMUOVIMI PRIMA DI CONSEGNARE IL PROGETTO
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    // AUTENTICAZIONE
    implementation("com.google.firebase:firebase-auth")
    // Realtime Database library
    implementation("com.google.firebase:firebase-database")

    //ce l'aveva il prof (?)
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    // libreria per le imageview rotonde
    implementation("de.hdodenhof:circleimageview:3.1.0")

    annotationProcessor("androidx.room:room-compiler:2.6.1")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
