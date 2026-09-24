import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.aboutlibraries)
    alias(libs.plugins.android.room3)
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

// 配置版本信息
/* val commitHash by lazy { "git rev-parse --short HEAD".exec()}
val verCode = "git rev-list --count HEAD".exec().toInt() */

android {
    namespace = "cn.super12138.todo"
    compileSdk {
        version = release(37)
    }

    // 获取 Release 签名
    val releaseSigning = if (project.hasProperty("releaseStoreFile")) {
        signingConfigs.create("release") {
            storeFile = File(providers.gradleProperty("releaseStoreFile").get())
            storePassword = providers.gradleProperty("releaseStorePassword").get()
            keyAlias = providers.gradleProperty("releaseKeyAlias").get()
            keyPassword = providers.gradleProperty("releaseKeyPassword").get()
        }
    } else {
        signingConfigs.getByName("debug")
    }

    defaultConfig {
        applicationId = "cn.super12138.todo"
        minSdk = 26
        targetSdk = 37
        versionCode = 1272
        versionName = "3.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        base.archivesName.set("vervedo-${versionName}")
    }

    buildTypes {
        all {
            signingConfig = releaseSigning
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    androidResources {
        generateLocaleConfig = true
    }

    buildFeatures {
        compose = true
    }

    // F-Droid 构建无法检测依赖信息块，所以将其忽略
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    aboutLibraries {
        collect {
            configPath = file("$projectDir/licences")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {

    // Android X
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.animation)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.windowsizeclass)
    implementation(libs.androidx.material3.adaptive)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    // About Libraries
    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose)
    // M3 Color
    implementation(libs.kyant0.m3color)
    // Capsule
    // implementation(libs.kyant0.capsule)
    // Konfetti
    implementation(libs.nl.dionsegijn.konfetti.compose)
    // Kotlin
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    // Room
    implementation(libs.androidx.room3.runtime)
    ksp(libs.androidx.room3.compiler)
    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    // Kotlin Csv
    implementation(libs.jsoizo.kotlin.csv)
    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// 命令执行工具类
/*fun String.exec(): String = exec(this)

fun Project.exec(command: String): String = providers.exec {
    commandLine(command.split(" "))
}.standardOutput.asText.get().trim()*/
