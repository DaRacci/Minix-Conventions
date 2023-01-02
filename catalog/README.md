<div align="center">

# Catalog
[![Publish Packages](https://img.shields.io/github/actions/workflow/status/DaRacci/Minix-Conventions/catalog.yml?color=purple&style=for-the-badge)](https://github.com/DaRacci/Minix-Conventions/actions/workflows/catalog.yml)
[![Maven](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.racci.dev%2Freleases%2Fdev%2Fracci%2Fcatalog%2Fmaven-metadata.xml&color=purple&style=for-the-badge)](https://repo.racci.dev/#/releases/dev/racci/catalog)

</div>
The Catalog is a simple collection of common libraries that is kept up to date and auto-published when there's a newer version.

## Usage

To specify the version that is used add the following to your `gradle.properties`
```properties
kotlinVersion=1.8.0 # The kotlin version that is used.
build=454           # Which build to use, note that when kotlin is updated packages aren't updated for old versions.
```

Add this to your `settings.gradle.kts`
```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        // Used to resolve the catalog
        maven("https://repo.racci.dev/releases") { mavenContent { releasesOnly() } }
    }

    // You can change `libs` to whatever you want to prefix your dependencies with.
    versionCatalogs.create("libs") {
        val build: String by settings
        val kotlinVersion: String by settings
        val conventions = "$kotlinVersion-$build"
        from("dev.racci:catalog:$conventions")
    }
}
```

Using the catalog is as simple as that, now you can use it like this inside your `build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.kotlin.jvm) // There's no need to specify the version here since it's managed by the catalog.
}

depedencies {
    alias(libs.arrow.core)
    alias(libs.bundles.kotlinx) // Adds all the kotlinx libraries.
}
```
