
val serverVersion: String by project
val useTentacles: String? by project
val removeDev: String? by project

plugins {
    java
    id("io.papermc.paperweight.userdev")
}

repositories {
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://repo.racci.dev/snapshots")
    maven("https://papermc.io/repo/repository/maven-public/")
}

tasks.getByName("assemble").dependsOn("reobfJar")

tasks.reobfJar {
    doLast {
        configurations.all {
            artifacts.removeIf {
                it.file == inputJar.orNull?.asFile
            }
        }
        if (removeDev.toBoolean()) { inputJar.orNull?.asFile?.delete() }
        artifacts {
            apiElements(outputJar)
            runtimeElements(outputJar)
        }
    }
}

dependencies {
    if (useTentacles.toBoolean()) {
        paperweightDevBundle("dev.racci.tentacles", serverVersion)
    } else paperweightDevBundle("org.purpurmc.purpur", serverVersion)
}
