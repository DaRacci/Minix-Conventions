
val serverVersion: String by project

plugins {
    id("io.papermc.paperweight.userdev")
}

repositories {
    maven("https://repo.purpurmc.org/snapshots")
}

tasks.getByName("assemble").dependsOn("reobfJar")

dependencies {
    paperweightDevBundle("org.purpurmc.purpur", serverVersion)
}
