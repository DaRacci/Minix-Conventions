import Dev_racci_minix_platform_gradle.Deps

plugins {
    id("dev.racci.minix.kotlin")
    id("com.github.johnrengelman.shadow")
}

val runNumber: String = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"
val kotlinVersion: String by project

version = "$kotlinVersion-$runNumber"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://erethon.de/repo/")
    maven("https://repo.racci.dev/releases")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://minecraft.curseforge.com/api/maven/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(Deps.adventure.api)
    implementation(Deps.adventure.kotlin)
    implementation(Deps.adventure.minimessage)

    implementation(Deps.minecraft.authLib)
    implementation(Deps.minecraft.acfPaper)
    implementation(Deps.minecraft.inventoryFramework)

    implementation(Deps.minecraft.apis.floodgate)
    implementation(Deps.minecraft.apis.protocolLib)
    implementation(Deps.minecraft.apis.placeholderAPI)

    implementation(Deps.kotlin.stdlib)
    implementation(Deps.kotlin.reflect)

    implementation(Deps.kotlinx.coroutines)
    implementation(Deps.kotlinx.dateTime)
    implementation(Deps.kotlinx.immutableCollections)

    implementation(Deps.kotlinx.serialization.kaml)
    implementation(Deps.kotlinx.serialization.json)
    implementation(Deps.kotlinx.serialization.cbor)
    implementation(Deps.kotlinx.serialization.hocon)
    implementation(Deps.kotlinx.serialization.protobuf)
    implementation(Deps.kotlinx.serialization.properties)

    implementation(Deps.exposed.core)
    implementation(Deps.exposed.dao)
    implementation(Deps.exposed.jdbc)
    implementation(Deps.exposed.dateTime)

    implementation(Deps.kotlin_statistics)
    implementation(Deps.sqlite_jdbc)
    implementation(Deps.valiktor)
    implementation(Deps.hikariCP)
    implementation(Deps.mordant)

    implementation(Deps.logging.sentry)
    implementation(Deps.logging.slf4jAPI)
    implementation(Deps.logging.kotlinLogger)

    implementation(Deps.koin.core) { exclude("org.jetbrains.kotlin") }
    implementation(Deps.koin.ktor) { exclude("org.jetbrains.kotlin") ; exclude("org.jetbrains.kotlinx") }
}

tasks {
    shadowJar {
        archiveBaseName.set("racci")
        archiveClassifier.set("")
        archiveExtension.set("platform")
    }
    build {
        dependsOn(shadowJar)
    }
}
