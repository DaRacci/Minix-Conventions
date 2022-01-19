import java.util.Properties

Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { (key, value) -> project.ext["$key"] = value }

plugins {
    `kotlin-dsl`
    `maven-publish`
}

val kotlinVersion: String by project
val runNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"
val minixVersion = "${project.ext["version"]}.$runNumber"
version = "$kotlinVersion-$runNumber"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.racci.dev/releases/")
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("serialization", kotlinVersion))
    implementation("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.17.0")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.0")
    implementation("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.19.0")
    implementation("io.papermc.paperweight.userdev:io.papermc.paperweight.userdev.gradle.plugin:1.3.3")
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation(kotlin("reflect", kotlinVersion))
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
                    "minixVersion" to minixVersion
                )
            )
        }
    }

    publish { dependsOn("check") }

    build { dependsOn(processResources) }
}
