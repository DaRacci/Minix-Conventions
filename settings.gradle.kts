@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Minix-Conventions"

listOf("plugin", "Gradle").forEach { projectName ->
    val prefix = if (projectName.first().isUpperCase()) {
        "Minix"
    } else "minix" // Temp way to support both conventions
    includeBuild("$prefix-$projectName")
}

include("catalog", "Minix-NMS")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.racci.dev/releases")
        maven("https://papermc.io/repo/repository/maven-public/")
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
