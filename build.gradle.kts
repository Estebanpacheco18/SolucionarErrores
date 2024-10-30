// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
<<<<<<< HEAD
    alias(libs.plugins.kotlin.android) apply false
}
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
    }
=======
    alias(libs.plugins.jetbrains.kotlin.android) apply false
>>>>>>> e1cc0e856e0aa64f442571383c1bd37f4226fe7c
}