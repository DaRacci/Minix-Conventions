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
    implementation(libs.bundles.kyori)
    implementation(libs.adventure.platform.bukkit)

    implementation(libs.bundles.cloud)
    implementation(libs.bundles.cloud.kotlin)

    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.exposed)

    implementation(libs.cloud.minecraft.brigadier)
    implementation(libs.cloud.minecraft.bukkit)
    implementation(libs.cloud.minecraft.bungee)
    implementation(libs.cloud.minecraft.cloudburst)
    implementation(libs.cloud.minecraft.extras)
    implementation(libs.cloud.minecraft.paper)
    implementation(libs.cloud.minecraft.sponge7)
    implementation(libs.cloud.minecraft.velocity)

    implementation(libs.minecraft.authLib)
    implementation(libs.minecraft.commandAPI)
    implementation(libs.minecraft.inventoryFramework)
    implementation(libs.minecraft.bstats)

    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.cbor)
    implementation(libs.kotlinx.serialization.hocon)
    implementation(libs.kotlinx.serialization.protobuf)
    implementation(libs.kotlinx.serialization.properties)
    implementation(libs.kotlinx.serialization.kaml)

    implementation(libs.kotlin.statistics)
    implementation(libs.valiktor)
    implementation(libs.mordant)
    implementation(libs.caffeine)

    implementation(libs.koin.core)
    implementation(libs.koin.ktor)

    implementation(libs.logging.slf4JAPI)
    implementation(libs.logging.kotlinLogging)

    implementation(libs.sentry.core)
    implementation(libs.sentry.kotlin)

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.jetty)
    implementation(libs.ktor.server.netty)

    implementation(libs.ktor.server.plugins.auth)
    implementation(libs.ktor.server.plugins.websockets)
    implementation(libs.ktor.server.plugins.sessions)
    implementation(libs.ktor.server.plugins.defaultHeaders)
    implementation(libs.ktor.server.plugins.callLogging)
    implementation(libs.ktor.server.plugins.locations)
    implementation(libs.ktor.server.plugins.contentNegotiation)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.apache)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.java)
    implementation(libs.ktor.client.jetty)
    implementation(libs.ktor.client.okhttp)

    implementation(libs.ktor.client.plugins.auth)
    implementation(libs.ktor.client.plugins.encoding)
    implementation(libs.ktor.client.plugins.gson)
    implementation(libs.ktor.client.plugins.serialization)
    implementation(libs.ktor.client.plugins.logging)
    implementation(libs.ktor.client.plugins.websockets)
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
