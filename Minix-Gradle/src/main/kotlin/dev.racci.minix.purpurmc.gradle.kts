import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val serverVersion: String by project

plugins {
    java
}

repositories {
    maven("https://repo.racci.dev/snapshots")
    maven("https://repo.purpurmc.org/snapshots")
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    val (major, minor) = serverVersion.split('.').take(2).map { it.toInt() }
    // Use old purpurmc groupId with versions below 1.18
    val purpurGroup = if(major == 1 && minor < 18) {
        "net.pl3x.purpur"
    } else "org.purpurmc.purpur"

    compileOnly("$purpurGroup:purpur-api:$serverVersion")
}

tasks {

    processResources {
        filesMatching("plugin.yml") {
            expand(mutableMapOf("version" to version))
        }
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

}
