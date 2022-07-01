import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { key, value -> project.ext["$key"] = value }
val kotlinVersion: String by project

plugins {
    java
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.racci.dev/releases/")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    // Align the version of all kotlin components
    implementation(platform(kotlin("bom:$kotlinVersion")))
    implementation(kotlin("stdlib"))

    // All the plugins that are used to configure.
    // TODO: Figure out how to apply these without implementing specific versions
    compileOnly(gradleApi())
    implementation(libs.gradle.serialization)
    implementation(libs.gradle.pluginYML)
    implementation(libs.gradle.kotlin)
    implementation(libs.gradle.ktlint)
    implementation(libs.gradle.dokka)
    implementation(libs.gradle.shadow)
    implementation(libs.gradle.paperweight)
}

kotlin {
    sourceSets.all {
        languageSettings {
            languageVersion = "1.7"
        }
    }
}

tasks {

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            languageVersion = "1.7"
            freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn", "-Xcontext-receivers")
        }
    }
}

gradlePlugin {
    (plugins) {
        register("minixPlugin") {
            id = "dev.racci.minix.gradle"
            implementationClass = "dev.racci.minix.gradle.MinixGradlePlugin"
        }
    }
}
