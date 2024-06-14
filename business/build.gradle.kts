import de.honoka.gradle.buildsrc.kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.charset.StandardCharsets

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    alias(libs.plugins.jvm.kotlin)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = sourceCompatibility
}

@Suppress("GradleDependency")
//noinspection UseTomlInstead
dependencies {
    kotlin(project)
    implementation(libs.jvm.lavender.api)
    implementation("cn.hutool:hutool-all:5.8.18")
}

tasks {
    compileJava {
        options.encoding = StandardCharsets.UTF_8.name()
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = java.sourceCompatibility.toString()
    }

    test {
        useJUnitPlatform()
    }
}