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
    id("com.gradle.plugin-publish") version "1.0.0"
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
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.immutableCollections)

    // All the plugins that are used to configure.
    // TODO: Figure out how to apply these without implementing specific versions
    compileOnly(gradleApi())
    compileOnly(gradleKotlinDsl())

    compileOnly(libs.gradle.serialization)
    compileOnly(libs.gradle.pluginYML)
    compileOnly(libs.gradle.kotlin)
    compileOnly(libs.gradle.ktlint)
    compileOnly(libs.gradle.dokka)
    compileOnly(libs.gradle.shadow)
    compileOnly(libs.gradle.paperweight)

    testImplementation(libs.bundles.kotlin)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.testing.junit5)
    testImplementation(libs.testing.kotest.junit5)
    testImplementation(libs.testing.kotest.properties)
    testImplementation(libs.testing.kotest.assertions)
    testImplementation(libs.testing.mockK)
    testImplementation(libs.testing.strikt)
    testImplementation(gradleTestKit())

    configurations.forEach {
        // it.exclude(group = "")
    }
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

    withType<Zip> {
        isZip64 = true
    }
}

gradlePlugin {
    plugins {
        create("minix-gradle") {
            id = group.toString()
            displayName = "Minix-Gradle"
            description = "Kotlin and Minecraft conventions and helper."
            implementationClass = "dev.racci.minix.gradle.MinixGradlePlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/DaRacci/Minix-Conventions"
    vcsUrl = "https://github.com/DaRacci/Minix-Conventions"
    tags = listOf("setup", "helper", "conventions")
    description = "Kotlin and Minecraft conventions and helper."
}
