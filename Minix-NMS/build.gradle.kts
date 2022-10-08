plugins {
    id("dev.racci.minix.kotlin")
    id("dev.racci.minix.nms")
    id("dev.racci.minix.purpurmc")
    id("dev.racci.minix.publication")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(project(":"))
    compileOnly(libs.kotlinx.serialization.json)
}
