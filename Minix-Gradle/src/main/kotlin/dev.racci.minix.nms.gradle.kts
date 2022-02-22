
val serverVersion: String by project
val useTentacles: Boolean? by project

plugins {
    id("io.papermc.paperweight.userdev")
}

repositories {
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://repo.racci.dev/snapshots")
    maven("https://papermc.io/repo/repository/maven-public/")
}

tasks.getByName("assemble").dependsOn("reobfJar")

dependencies {
    if (useTentacles == true) {
        paperDevBundle("dev.racci.tentacles", serverVersion)
    } else paperweightDevBundle("org.purpurmc.purpur", serverVersion)
}
