import de.honoka.gradle.buildsrc.Android

plugins {
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

version = "1.0.0-dev"
Android.versionCode = 1

allprojects {
    group = "de.honoka.lavender"

    apply(plugin = "io.spring.dependency-management")
}