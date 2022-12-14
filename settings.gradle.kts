@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Minix-Conventions"

includeBuild("Minix-Gradle")
includeBuild("Minix-Plugin")

include("catalog", "Minix-NMS")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.racci.dev/releases")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.racci.dev/releases/")
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}
