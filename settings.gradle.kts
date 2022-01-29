pluginManagement {
    val kotlinVersion: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.racci.dev/releases/")
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    plugins {
        kotlin("plugin.serialization") version kotlinVersion
    }
}

rootProject.name = "Minix-Conventions"

includeBuild("Minix-Gradle")
includeBuild("Minix-Platform")

include(
    "Minix-NMS",
    "Minix-Platform-Loader"
)
