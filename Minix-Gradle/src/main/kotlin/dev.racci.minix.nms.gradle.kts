
val serverVersion: String by project
val useTentacles: String? by project

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

configurations.all {
    artifacts.removeIf {
        it.file.name == tasks.jar.get().outputs.files.singleFile.name
    }
}

artifacts {
    apiElements(project.buildDir.resolve("libs").resolve("${project.name}-${rootProject.version}.jar"))
    runtimeElements(project.buildDir.resolve("libs").resolve("${project.name}-${rootProject.version}.jar"))
}

dependencies {
    if (useTentacles.toBoolean()) {
        paperweightDevBundle("dev.racci.tentacles", serverVersion)
    } else paperweightDevBundle("org.purpurmc.purpur", serverVersion)
}
