buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.google.services)
    }
}
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.20" apply false
}
