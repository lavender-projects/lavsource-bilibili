import android.annotation.SuppressLint

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    //这里不可使用alias来引用插件，会使下面的Android相关的DSL无法被识别
    //也不可先在root project中声明插件，然后再在这里引用，会导致此处的kotlin插件与server模块中的kotlin插件产生冲突
    id("com.android.application") version libs.versions.android.gradle.plugin
    kotlin("android") version libs.versions.android.kotlin
}

android {
    namespace = "de.honoka.lavender.lavsource.bilibili"
    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    defaultConfig {
        applicationId = "de.honoka.lavender.lavsource.bilibili"
        minSdk = libs.versions.android.sdk.min.get().toInt()
        @SuppressLint("OldTargetApi")
        targetSdk = libs.versions.android.sdk.compile.get().toInt()
        versionCode = libs.versions.app.version.code.get().toInt()
        versionName = libs.versions.app.version.name.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            @Suppress("UnstableApiUsage")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packaging {
        resources.excludes.addAll(listOf(
            "META-INF/INDEX.LIST",
            "META-INF/io.netty.versions.properties"
        ))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = sourceCompatibility
    }
    
    kotlinOptions {
        jvmTarget = compileOptions.sourceCompatibility.toString()
    }
}

@Suppress("GradleDependency")
//noinspection UseTomlInstead
dependencies {
    implementation(project(":business"))
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation(libs.lavsource.app.sdk)
    implementation("cn.hutool:hutool-all:5.8.18")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}