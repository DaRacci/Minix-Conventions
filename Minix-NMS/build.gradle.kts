plugins {
    id("dev.racci.minix.kotlin")
    id("dev.racci.minix.nms")
    id("dev.racci.minix.purpurmc")
    kotlin("plugin.serialization")
    `maven-publish`
}

dependencies {
    api(project(":"))
    compileOnly(libs.kotlinx.serialization.json)
}

publishing {
    repositories {
        maven("https://repo.racci.dev/releases") {
            name = "RacciRepo"
            credentials(PasswordCredentials::class)
        }
    }
    publications {
        register("maven", MavenPublication::class) {
            artifact(tasks.reobfJar.get().outputJar) {
                classifier = null
                builtBy(tasks.reobfJar)
            }
            artifact(tasks.kotlinSourcesJar) {
                classifier = "sources"
                builtBy(tasks.kotlinSourcesJar)
            }
        }
    }
}
