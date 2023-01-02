plugins {
    id("dev.racci.minix.nms")
    id("dev.racci.minix.publication")
    id(libs.plugins.kotlin.jvm.get().pluginId)
}

repositories {
    maven("https://repo.purpurmc.org/snapshots") {
        content { includeGroup("org.purpurmc.purpur") }
    }
}

dependencies {
    api(project(":"))
}
