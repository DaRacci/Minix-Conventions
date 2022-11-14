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

    withType<PublishToMavenLocal> { dependsOn(reobfJar) }
}

dependencies {
    if (useTentacles.toBoolean()) {
        paperweightDevBundle("dev.racci.tentacles", serverVersion)
    } else paperweightDevBundle("org.purpurmc.purpur", serverVersion)
}
