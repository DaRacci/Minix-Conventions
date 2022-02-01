
val serverVersion: String by project

plugins {
    id("io.papermc.paperweight.userdev")
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
}

tasks.getByName("assemble").dependsOn("reobfJar")

dependencies {
    paperDevBundle(serverVersion)
}
