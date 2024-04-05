plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    `maven-publish`
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "gr.indice.identity"
            artifactId = "identity"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "Identity"
            //url = uri("https://maven.pkg.github.com/{GIT_USER}/{GIT_REPO}")
            credentials {
                username = "{GIT_USER}"
                password = "{GIT_TOKEN}"
            }
        }
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
}