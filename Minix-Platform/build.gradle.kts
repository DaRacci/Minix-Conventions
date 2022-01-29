import Dev_racci_minix_platform_gradle.Deps

java.util.Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { (key, value) -> project.ext["$key"] = value }

plugins {
    `java-platform`
    `maven-publish`
    id("com.github.ben-manes.versions") version "0.41.0"
    id("dev.racci.minix.platform")
}

val kotlinVersion: String by project
val runNumber = System.getenv("GITHUB_RUN_NUMBER") ?: "DEV"

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
}

dependencies {
    constraints {
        api("${Deps.adventure.api}:4.10.0-SNAPSHOT")
        api("${Deps.adventure.kotlin}:4.10.0-SNAPSHOT")
        api("${Deps.adventure.minimessage}:4.10.0-SNAPSHOT")

        api("${Deps.minecraft.authLib}:2.3.31")
        api("${Deps.minecraft.acfPaper}:0.5.1-SNAPSHOT")
        api("${Deps.minecraft.inventoryFramework}:0.10.4")

        api("${Deps.minecraft.apis.floodgate}:2.1.1-SNAPSHOT")
        api("${Deps.minecraft.apis.protocolLib}:4.7.0")
        api("${Deps.minecraft.apis.placeholderAPI}:2.11.1")

        api("${Deps.kotlin.stdlib}:$kotlinVersion")
        api("${Deps.kotlin.reflect}:$kotlinVersion")

        api("${Deps.kotlinx.coroutines}:1.6.0")
        api("${Deps.kotlinx.dateTime}:0.3.2")
        api("${Deps.kotlinx.immutableCollections}:0.3.5")
        api("${Deps.kotlinx.atomicFU}:0.17.0")

        api("${Deps.kotlinx.serialization.kaml}:0.40.0")
        api("${Deps.kotlinx.serialization.json}:1.3.2")
        api("${Deps.kotlinx.serialization.cbor}:1.3.2")
        api("${Deps.kotlinx.serialization.hocon}:1.3.2")
        api("${Deps.kotlinx.serialization.protobuf}:1.3.2")
        api("${Deps.kotlinx.serialization.properties}:1.3.2")

        api("${Deps.exposed.core}:0.37.3")
        api("${Deps.exposed.dao}:0.37.3")
        api("${Deps.exposed.jdbc}:0.37.3")
        api("${Deps.exposed.dateTime}:0.37.3")

        api("${Deps.kotlin_statistics}:1.2.1")
        api("${Deps.sqlite_jdbc}:3.36.0.3")
        api("${Deps.valiktor}:0.12.0")
        api("${Deps.hikariCP}:5.0.1")

        api("${Deps.logging.sentry}:6.0.0-alpha.1")
        api("${Deps.logging.slf4jAPI}:2.0.0-alpha6")
        api("${Deps.logging.kotlinLogger}:2.1.21")

        api("${Deps.koin.core}:3.1.5")
        api("${Deps.koin.ktor}:3.1.5")
        api("${Deps.koin.test}:3.1.4")
        api("${Deps.koin.testJunit5}:3.1.4")
    }
}

publishing {
    repositories {
        maven("https://repo.racci.dev/releases/") {
            name = "RacciRepo"
            credentials(PasswordCredentials::class)
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["javaPlatform"])
        }
    }
}

tasks {
    dependencyUpdates {
        checkForGradleUpdate = true
    }

    build {
        dependsOn(project(":shaded").tasks.build)
    }
}
