<div align="center">

# Minix-Gradle
[![Publish Packages](https://github.com/DaRacci/Minix-Conventions/actions/workflows/publish-packages.yml/badge.svg)](https://github.com/DaRacci/Minix-Conventions/actions/workflows/publish-packages.yml)
[![Maven](https://badgen.net/maven/v/metadata-url/repo.racci.dev/releases/dev/racci/Minix-Gradle/maven-metadata.xml)](https://repo.racci.dev/releases/dev/racci/Minix-Gradle)

</div>
Code that helps me share common shortcuts for my buildscripts. The project is a plugin itself which provides some
shared functions I may want to reuse, as well as several other conventions plugins that apply common build logic.

## Usage

Add my repo to `settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        // Add my repository to be able to access the plugin
        maven("https://repo.racci.dev/releases/")
    }    
    
    //Use same version across all conventions
    val minixConventions: String by settings
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("dev.racci.minix"))
                useVersion(minixConventions)
        }
    }
}
```

Apply a plugin in your `plugins { }` block. All of them start with `dev.racci.minix`

```kotlin
plugins {
  id("dev.racci.minix.kotlin")
}
```

Some conventions have extra config options that may be specified in your gradle.properties, they are explained further down.

If you're using the `resolutionStrategy` block, be sure to specify the `minixConventionsVersion` in `gradle.properties`:

```properties
minixConventionsVersion=<Kotlin Version>-<build number>
```

See maven badge at the top for the latest version.

## Conventions

### dev.racci.minix.copyjar

Copies a generated `shadowJar` artifact to a specified path.

- `pluginPath: String` The path to copy the jar to. (should be set in global gradle.properties.)
- `copyJar: Boolean?` if false, will not run.

### dev.racci.minix.kotlin

Adds Kotlin, Detekt Linter and shadowJar plugins. Applies a Java platform of the common dependencies.

Adds a `kotlinVersion` property to the project and warns if the project already has such a property that doesn't match.

### dev.racci.minix.purpurmc

Adds purpur dependencies, process resources task which replaces `${version}` in plugin.yml with the project's `version`. Targets JVM 17. Adds copyJar plugin.

- `serverVersion: String` the full Minecraft server version name.

### dev.racci.minix.nms

Adds paper nms resources, Make sure to add this to your `settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}
```

### dev.racci.minix.publication

Publishes to my maven repo with sources.
If you have credentials to do this add these to the file located at `/home/USER/.gradle/gradle.properties`.
Adds GitHub run number to the end of version.

```properties
RacciRepoUsername: USERNAME
RacciRepoPassword: PASSWORD
```
and obviously replace these with your correct credentials.

Adding these to your `gradle.properties` will define certain areas.
- `runNumberDelimiter` the characters to put in between the version and run number.
- `addRunNumber` if false, will not add run number.
- `publishComponentName` the name of the component to be published.

### dev.racci.minix.testing

Uses jUnit platform for testing, adds kotest and mockk dependencies.
