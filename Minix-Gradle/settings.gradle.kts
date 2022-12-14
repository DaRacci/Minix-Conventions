@file:Suppress("UnstableApiUsage")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.racci.dev/releases")
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    versionCatalogs.create("libs") {
        from(files("../gradle/libs.versions.toml"))
    }

    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.racci.dev/releases/")
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}
