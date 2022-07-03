import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

tasks {

    val relocateDependencies by creating {
        val target = getByName("shadowJar", ShadowJar::class)
        val prefix = "dev.racci.minix.libs"

        target.relocate("kotlin", "$prefix.kotlin")
        target.relocate("kotlinx", "$prefix.kotlinx")
        target.relocate("io.ktor", "$prefix.io.ktor")
        target.relocate("org.koin", "$prefix.org.koin")
        target.relocate("io.sentry", "$prefix.io.sentry")
        target.relocate("org.bstats", "$prefix.org.bstats")
        target.relocate("org.jetbrains.exposed", "$prefix.org.jetbrains.exposed")
        target.relocate("com.github.benmanes.caffeine", "$prefix.com.github.benmanes.caffeine")
        target.relocate("cloud.commandframework.kotlin", "$prefix.cloud.commandframework.kotlin")
        target.relocate("net.kyori.adventure.extra.kotlin", "$prefix.net.kyori.adventure.extra.kotlin")
        target.relocate("org.spongepowered.configurate.kotlin", "$prefix.org.spongepowered.configurate.kotlin")
    }

    getByName("shadowJar", ShadowJar::class).dependsOn(relocateDependencies)
}
