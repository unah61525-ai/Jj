plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.agung.settings"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.agung.settings"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
}
dependencies { implementation("dev.rikka.shizuku:api:13.1.5") }
