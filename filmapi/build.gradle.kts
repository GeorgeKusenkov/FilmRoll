import java.util.Properties

plugins {
//    alias(libs.plugins.jetbrains.kotlin.jvm)
//
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.news.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        buildFeatures {
            buildConfig = true
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "API_KEY", properties.getProperty("API_KEY"))
        buildConfigField("String", "BASE_URL", properties.getProperty("BASE_URL"))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

//java {
//    sourceCompatibility = JavaVersion.VERSION_17
//    targetCompatibility = JavaVersion.VERSION_17
//}

//android {
//    defaultConfig {
//        // Добавление полей в BuildConfig
//        buildConfigField "String", "API_KEY", "\"${API_KEY}\""
//        buildConfigField "String", "BASE_URL", "\"${BASE_URL}\""
//    }
//}

dependencies {
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation(libs.androidx.core.ktx)
    implementation(libs.retrofit)
    implementation (libs.retrofit.adapters.result)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.annotation)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation (libs.retrofit.adapters.result)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

}