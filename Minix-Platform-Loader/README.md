<div align="center" style="padding:5px">
  
# Minix-Platform-Loader

[![Maven](https://badgen.net/maven/v/metadata-url/repo.racci.dev/releases/dev/racci/Minix-Platform-Loader/maven-metadata.xml)](https://repo.racci.dev/#/releases/dev/racci/Minix-Platform-Loader)
</div>

A tiny kotlin lib that allows plugins to load and share jar files.

Under the hood, it's really just injecting a classloader into Spigot's library loader system.

# Usage

#### build.gradle.kts
```kotlin
repositories {
    maven("https://repo.racci.dev/releases")
}

dependencies {
    implementation("dev.racci:Minix-Platform-Loader:VERSION")
}
```
Make sure to use the shadowJar plugin to shade this dependency.

#### Plugin
```kotlin
override suspend fun handleLoad() {
    // Load a .platform file in the plugin folder that starts with "racci"
    MinixPlatforms.load(this, "minix")
    
    // Alternatively, write your own predicate to check which file to load (remember you can't use Kotlin stdlib until after this line)
    MinixPlatforms.load(this) { file -> Boolean }
}
```
