plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    defaultConfig {
        applicationId = "com.castlefrog.games.asg"
        compileSdk = 34
        minSdk = 30
        targetSdk = 32
        versionCode = 14
        versionName = "3.0.0"
    }
    buildTypes {
        release {
            // isMinifyEnabled = false
        }
    }
    buildFeatures {
        compose =  true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    namespace = "com.castlefrog.games.asg"
}

dependencies {
    implementation(files("libs/kotlin-age-0.3.0.jar"))

    implementation("androidx.recyclerview:recyclerview:1.3.2") // todo remove
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    val kotlin = "1.9.20"
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin")

    implementation(platform("com.google.firebase:firebase-bom:30.0.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.firebaseui:firebase-ui-auth:8.0.1")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    val compose = "1.6.8"
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation("androidx.compose.ui:ui:$compose")
    implementation("androidx.compose.material:material:$compose")
    implementation("androidx.compose.ui:ui-tooling:$compose")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")

    implementation("io.arrow-kt:arrow-core:1.2.1")

    implementation("com.jakewharton.timber:timber:4.6.1")

    val moshi = "1.9.2"
    implementation("com.squareup.moshi:moshi:$moshi")
    implementation("com.squareup.moshi:moshi-kotlin:$moshi")

    val retrofit = "2.8.1"
    implementation("com.squareup.retrofit2:retrofit:$retrofit")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofit")

    val room = "2.6.1"
    implementation("androidx.room:room-runtime:$room")
    annotationProcessor("androidx.room:room-compiler:$room")
    ksp("androidx.room:room-compiler:$room")
}
