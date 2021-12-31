<div align="center">

# Minix-Platform
[![Package](https://badgen.net/maven/v/metadata-url/repo.racci.dev/releases/dev/racci/Minix-Platform/maven-metadata.xml)](https://repo.racci.dev/releases/dev/racci/Minix-Gradle-Platform)
</div>

A Gradle Java-platform for common libraries I use.

## Deps class

When applied through my convention [dev.racci.minix.platform](https://github.com/DaRacci/Minix-Conventions/tree/master/Minix-Gradle), you may use typesafe groupId variables in the [Deps class](https://github.com/DaRacci/Minix-Conventions/blob/master/Minix-Gradle/src/main/kotlin/dev.racci.minix.platform.gradle.kts)

### For example
```kotlin
implementation(Deps.kotlinx.serialization.json)
```

## Including dependencies

The `shaded` subproject generates a jar file with a `.platform` extension which can be loaded on plugin startup using `Minix-Platform-Loader`. You may download the platform from this project's releases page.
