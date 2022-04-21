import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm") version "1.6.21"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1"
    id("com.github.johnrengelman.shadow") version "7.0.0"
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
    implementation(platform(kotlin("stdlib")))

    // All the plugins that are used to configure.
    // TODO: Figure out how to apply these without implementing specific versions
    compileOnly(gradleApi())
    implementation("net.minecrell:plugin-yml:0.5.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:10.2.1")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.20")
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
    implementation("io.papermc.paperweight.userdev:io.papermc.paperweight.userdev.gradle.plugin:1.3.5")
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
