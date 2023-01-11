@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Minix-Conventions"

includeBuild("minix-plugin")
include("catalog", "nms")

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.racci.dev/releases")
        maven("https://papermc.io/repo/repository/maven-public/") { name = "Paper Repository" }
    }

    // Fixes not being able to use the plugin since,
    // for some reason it wants to use the unshaded one.
    buildscript {
        repositories {
            mavenCentral()
        }

        dependencies {
            classpath("io.github.classgraph:classgraph:4.8.154")
        }
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
