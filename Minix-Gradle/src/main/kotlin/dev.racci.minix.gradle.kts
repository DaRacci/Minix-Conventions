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
        target.relocate("org.bstats", "$prefix.org.bstats")
        target.relocate("io.sentry", "$prefix.io.sentry")
        target.relocate("org.jetbrains.kotlin", "$prefix.org.jetbrains.kotlin")
        target.relocate("org.jetbrains.kotlinx", "$prefix.org.jetbrains.kotlinx")
        target.relocate("org.jetbrains.exposed", "$prefix.org.jetbrains.exposed")
        target.relocate("com.github.benmanes.caffeine", "$prefix.com.github.benmanes.caffeine")
    }

    getByName("shadowJar", ShadowJar::class).dependsOn(relocateDependencies)
}
