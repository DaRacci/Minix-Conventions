enableFeaturePreview("VERSION_CATALOGS")
pluginManagement {
    val kotlinVersion: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.racci.dev/releases/")
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
}

rootProject.name = "Minix-Conventions"

includeBuild("Minix-Gradle")

include(
    "Minix-NMS",
    "Minix-Platform-Loader",
    "Minix-Catalog-Shaded",
    "Minix-Catalog"
)
