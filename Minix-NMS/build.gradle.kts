plugins {
    id("dev.racci.minix.common")
    id("dev.racci.minix.nms")
    id("dev.racci.minix.purpurmc")
    id("dev.racci.minix.publication")
    id(libs.plugins.kotlin.jvm.get().pluginId)
}

dependencies {
    api(project(":"))
}
