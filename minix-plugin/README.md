<div align="center">

# Catalog
[![Publish Packages](https://img.shields.io/github/actions/workflow/status/DaRacci/Minix-Conventions/gradle-plugin-ci.yml?color=purple&style=for-the-badge)](https://github.com/DaRacci/Minix-Conventions/actions/workflows/gradle-plugin-ci.yml)
[![Maven](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.racci.dev%2Freleases%2Fdev%2Fracci%2Fminix%2Fminix-plugin%2Fmaven-metadata.xml&color=purple&style=for-the-badge)](https://repo.racci.dev/#/releases/dev/racci/minix/minix-plugin)

##### A Gradle plugin, which simplifies the process of creating, keeping up to date, and being consistent in your projects.
</div>

## Usage

The easiest way of using the plugin is to first apply the catalog module, which will provide the latest version of the plugin.

Using the catalog:
```kotlin
plugins {
    alias(libs.plugins.minix) // There's no need to specify the version here since it's managed by the catalog.
}
```

Or you can specify the version manually:
```kotlin
plugins {
    id("dev.racci.minix") version "0.1.13"
}
```

## Plugin support
There is support and configuration for the following plugins,
- [Java]
- [Kotlin-Jvm]
- [Kotlin-Multiplatform]
- [Dokka]
- [Ktlint]
