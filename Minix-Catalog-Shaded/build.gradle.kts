plugins {
    id("dev.racci.minix.kotlin")
    id("dev.racci.minix.copyjar")
    id("com.github.johnrengelman.shadow")
    id("com.github.ben-manes.versions") version "0.42.0"
}

val runNumber: String = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"
val kotlinVersion: String by project

version = "$kotlinVersion-$runNumber"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(libs.adventure.api)
    implementation(libs.adventure.kotlin)
    implementation(libs.adventure.minimessage)

    implementation(libs.minecraft.authLib)
    implementation(libs.minecraft.acfPaper)
    implementation(libs.minecraft.inventoryFramework)

    implementation(libs.minecraft.api.floodgate)
    implementation(libs.minecraft.api.protoclLib)
    implementation(libs.minecraft.api.placeholderAPI)
    implementation(libs.minecraft.api.landsAPI)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.dateTime)
    implementation(libs.hikariCP)

    implementation(libs.valiktor)
    implementation(libs.mordant)
    implementation(libs.caffeine)
    implementation(libs.kotlin.statistics)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.dateTime)
    implementation(libs.kotlinx.immutableCollections)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.cbor)
    implementation(libs.kotlinx.serialization.hocon)
    implementation(libs.kotlinx.serialization.protobuf)
    implementation(libs.kotlinx.serialization.properties)
    implementation(libs.kotlinx.serialization.kaml)

    implementation(libs.koin.core)
    implementation(libs.koin.ktor)

    implementation(libs.logging.sentry)
    implementation(libs.logging.slf4JAPI)
    implementation(libs.logging.kotlinLogging)
}

tasks {

    dependencyUpdates {
        checkForGradleUpdate = true
        outputFormatter = "json"
    }

    shadowJar {
        archiveBaseName.set("minix")
        archiveClassifier.set("")
        archiveExtension.set("platform")
    }
}
