val serverVersion: String by project

plugins {
    id("io.papermc.paperweight.userdev")
}

repositories {
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    paperDevBundle(serverVersion)
}
