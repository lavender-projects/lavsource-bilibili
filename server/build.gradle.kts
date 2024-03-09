import de.honoka.gradle.buildsrc.Versions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.charset.StandardCharsets

plugins {
    //plugins块中不会读取import语句中导入的类
    //引入Versions对象时，必须像这样引入（https://github.com/gradle/gradle/issues/9270）
    @Suppress("RemoveRedundantQualifierName")
    val versions = de.honoka.gradle.buildsrc.Versions
    //plugins
    java
    id("org.springframework.boot") version versions.springBoot
    kotlin("jvm") version versions.kotlin
    kotlin("plugin.spring") version versions.kotlin
    kotlin("plugin.jpa") version versions.kotlin
}

version = "1.0.0-dev"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = sourceCompatibility
}

dependencies {
    implementation(project(":business"))
    implementation("de.honoka.lavender:lavsource-spring-boot-starter:${Versions.lavsourceSpringBootStarter}")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
    implementation("com.baomidou:mybatis-plus-boot-starter:3.4.3.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")
    implementation("de.honoka.sdk:honoka-utils:${Versions.honokaUtils}")
    implementation("de.honoka.sdk:honoka-framework-utils:${Versions.honokaFrameworkUtils}")
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
        archiveFileName.set("lavsource-bilibili-server.jar")
    }

    test {
        useJUnitPlatform()
    }
}