import java.util.Properties

Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { key, value -> project.ext["$key"] = value }

plugins {
    `kotlin-dsl`
    `maven-publish`
    `kotlin-dsl-precompiled-script-plugins`
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    id("com.github.ben-manes.versions") version "0.42.0"
}

val kotlinVersion: String by project
val runNumber: String = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"
val minixVersion: String = "${project.ext["version"]}.$runNumber"
version = "$kotlinVersion-$runNumber"

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["runtimeElements"]) {
    mapToOptional()
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.racci.dev/releases/")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(libs.gradle.kotlin)
    implementation(libs.gradle.serialization)
    implementation(libs.gradle.ktlint)
    implementation(libs.gradle.dokka)
    implementation(libs.gradle.shadow)
    implementation(libs.gradle.paperweight)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

publishing {
    repositories {
        maven("https://repo.racci.dev/releases/") {
            name = "RacciRepo"
            credentials(PasswordCredentials::class)
        }
    }
}

tasks {

    processResources {
        filesMatching("Minix-Conventions.properties") {
            expand(
                mutableMapOf(
                    "minixConventionsVersion" to version,
                    "minixConventionsKotlinVersion" to kotlinVersion,
                    "minixVersion" to minixVersion,
                )
            )
        }
    }

    publish { dependsOn("check") }

    build { dependsOn(processResources) }
}
