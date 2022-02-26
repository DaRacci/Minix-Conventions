plugins {
    id("dev.racci.minix.kotlin")
    id("dev.racci.minix.nms")
    id("dev.racci.minix.purpurmc")
    kotlin("plugin.serialization")
    id("dev.racci.minix.publication")
}

dependencies {
    api(project(":"))
    compileOnly(libs.kotlinx.serialization.json)
}
