plugins {
    id("dev.racci.minix.kotlin")
    id("dev.racci.minix.nms")
    id("dev.racci.minix.purpurmc")
    id("dev.racci.minix.publication")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(":"))
}
