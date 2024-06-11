import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    `maven-publish`
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "gr.indice"
            artifactId = "identity"
            version = "0.0.1"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        /*maven {
            name = "Identity"
            url = uri("https://maven.pkg.github.com/indice-co/Indice.Identity.Android")
            credentials {
                val props = Properties()
                if (file("config/github.properties").exists()) {
                    props.load(FileInputStream(file("config/github.properties")))
                }
                username = props.getProperty("GITHUB_USER_NAME") ?: ""
                password = props.getProperty("GITHUB_TOKEN") ?: ""
            }
        }*/
    }
}

android {
    namespace = "gr.indice.identity"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    publishing {
        singleVariant("release") {
            withJavadocJar()
        }
    }
}

dependencies {
    api(libs.retrofit)
    api(libs.converter.moshi)
    api(libs.logging.interceptor)
    api(libs.moshi.kotlin)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.gson)
}