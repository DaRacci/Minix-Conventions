plugins {
    id("dev.racci.minix.kotlin")
    id("dev.racci.minix.nms")
    id("dev.racci.minix.purpurmc")
    id("dev.racci.minix.publication")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(":"))
    compileOnly(libs.kotlinx.serialization.json)
}

artifacts {
    apiElements(file("build/libs/${project.name}-${project.version}.jar"))
    runtimeElements(file("build/libs/${project.name}-${project.version}.jar"))
}

tasks.getByName("generateMetadataFileForMavenPublication") {
    dependsOn(tasks.reobfJar)
}
