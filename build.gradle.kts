plugins {
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
}

buildscript {
    val kotlin = "1.9.0"
    val firebaseCrashlyticsGradle = "2.9.9"
    val gradle = "8.1.4"
    val googleServices = "4.4.1"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$gradle")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin")
        classpath("com.google.gms:google-services:$googleServices")
        classpath("com.google.firebase:firebase-crashlytics-gradle:$firebaseCrashlyticsGradle")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
