import de.honoka.gradle.buildsrc.kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.charset.StandardCharsets

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.jvm.kotlin)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}

version = libs.versions.server.get()

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = sourceCompatibility
}

//noinspection UseTomlInstead
dependencies {
    implementation(project(":business"))
    kotlin(project)
    implementation(libs.lavsource.spring.boot.starter)
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
    implementation("com.baomidou:mybatis-plus-boot-starter:3.4.3.4")
    implementation(libs.jvm.honoka.kotlin.utils)
    implementation(libs.jvm.honoka.framework.utils)
    implementation("cn.hutool:hutool-all:5.8.18")
    //Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    compileJava {
        options.encoding = StandardCharsets.UTF_8.name()
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = java.sourceCompatibility.toString()
    }

    bootJar {
        archiveFileName.set("${rootProject.name}-server.jar")
    }

    test {
        useJUnitPlatform()
    }
}