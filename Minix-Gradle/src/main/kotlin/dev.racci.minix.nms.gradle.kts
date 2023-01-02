val serverVersion: String by project
val useTentacles: String? by project
val removeDev: String? by project

gradle.settingsEvaluated {
    pluginManagement {
        repositories {
            maven("https://papermc.io/repo/repository/maven-public/") {
                content {
                    includeGroup("io.papermc.paperweight")
                }
            }

            if (useTentacles.toBoolean()) {
                maven("https://repo.racci.dev/snapshots") {
                    content { includeGroup("dev.racci.tentacles") }
                }
            } else {
                maven("https://repo.purpurmc.org/snapshots") {
                    content { includeGroup("org.purpurmc.purpur") }
                }
            }
        }
    }
}

plugins {
    java
    id("io.papermc.paperweight.userdev")
}

tasks {
    named("assemble") { dependsOn(named("reobfJar")) }
}

dependencies {
    if (useTentacles.toBoolean()) {
        paperweightDevBundle("dev.racci.tentacles", serverVersion)
    } else paperweightDevBundle("org.purpurmc.purpur", serverVersion)
}
