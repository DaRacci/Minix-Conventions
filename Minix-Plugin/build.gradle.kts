import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm") version "1.6.21"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
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
    implementation(platform(kotlin("bom")))
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
    implementation(libs.gradle.usedev)
}

kotlin {
    sourceSets.all {
        languageSettings {
            languageVersion = "1.6"
        }
    }
}

tasks {

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            languageVersion = "1.6"
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xcontext-receivers")
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
