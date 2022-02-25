import io.papermc.paperweight.tasks.RemapJar

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

tasks {
    named("assemble") { dependsOn(reobfJar) }

    withType<GenerateModuleMetadata> { dependsOn(reobfJar) }

    withType<RemapJar> {
        doLast {
            try {
                configurations.all {
                    artifacts.removeIf {
                        it.file == inputJar.orNull?.asFile
                    }
                }
            } catch (e: Exception) {
                println("Failed to remove input jar from configuration")
            }
            if (removeDev.toBoolean()) {
                inputJar.orNull?.asFile?.delete()
            }
            try {
                artifacts {
                    apiElements(outputJar)
                    runtimeElements(outputJar)
                }
            } catch (e: Exception) {
                println("Failed to add output jar to artifacts")
            }
        }
    }
}

dependencies {
    if (useTentacles.toBoolean()) {
        paperweightDevBundle("dev.racci.tentacles", serverVersion)
    } else paperweightDevBundle("org.purpurmc.purpur", serverVersion)
}
