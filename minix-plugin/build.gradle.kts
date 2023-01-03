import java.util.Properties

Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { key, value -> project.ext["$key"] = value }

plugins {
    `maven-publish`
    alias(libs.plugins.kotlin.dsl)
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.kotlin.plugin.ktlint)
    alias(libs.plugins.shadow)
}

buildDir = file("../build/$name")

val compileAndTest: Configuration by configurations.creating
val shadowImpl: Configuration by configurations.creating
configurations {
    compileOnly.get().extendsFrom(shadowImpl, compileAndTest)
    testImplementation.get().extendsFrom(shadowImpl, compileAndTest)
}

@Suppress("UnstableApiUsage")
dependencies {
    shadowImpl(libs.arrow.core)
    shadowImpl(libs.kotlinx.immutableCollections)

    // All the plugins that are used to configure.
    compileAndTest(gradleApi())
    compileAndTest(gradleKotlinDsl())
    compileAndTest(libs.gradle.minecraft.pluginYML)
    compileAndTest(libs.gradle.kotlin.jvm)
    compileAndTest(libs.gradle.kotlin.plugin.ktlint)
    compileAndTest(libs.gradle.kotlin.plugin.dokka)
    compileAndTest(libs.gradle.shadow)
    compileAndTest(libs.gradle.minecraft.paperweight)
    compileAndTest(libs.gradle.kotlin.mpp)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.testing.junit5)
    testImplementation(libs.testing.strikt)
    testImplementation(libs.testing.strikt.arrow)
    testImplementation(gradleTestKit())

    configurations.configureEach {
        exclude(group = "org.apache.logging.log4j", module = "log4j-core")
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
        exclude(group = "org.apache.logging.log4j", module = "log4j-slf4j-impl")
    }
}

kotlin {
    jvmToolchain(17)
    explicitApi()
}

// Fixes being able to apply this as a plugin to the root since the jar task is disabled.
artifacts {
    default(tasks.shadowJar.get().archiveFile)
}

tasks {
    jar { enabled = false }
    test { useJUnitPlatform() }

    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(shadowImpl)

        exclude("kotlin/**")
        listOf(
            "arrow",
            "kotlinx.collections",
            "org.codehaus.mojo.animal_sniffer",
            "org.intellij.lang.annotations",
            "org.jetbrains.annotations"
        ).map { it to it.split('.').last() }.forEach { (original, last) ->
            relocate(original, "dev.racci.minix.gradle.libs.$last")
        }
    }

    processResources {
        filesMatching("minix.properties") {
            expand(
                "jdk" to java.sourceCompatibility.toString(),
                "kotlin" to libs.versions.kotlin.asProvider().get(),
                "mc" to "1.19.3-R0.1-SNAPSHOT", // TODO: Get this from the paperweight plugin.
                "minix" to libs.versions.minix.get()
            )
        }
    }
}

publishing.repositories.maven("https://repo.racci.dev/") {
    url = if (version.toString().endsWith("SNAPSHOT")) {
        url.resolve("snapshots")
    } else url.resolve("releases")

    name = "RacciRepo"
    credentials(PasswordCredentials::class)
}

gradlePlugin {
    plugins {
        create("minix-gradle") {
            id = group.toString()
            displayName = "Minix-Gradle"
            description = "Kotlin and Minecraft conventions and helper."
            implementationClass = "dev.racci.minix.gradle.MinixGradlePlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/DaRacci/Minix-Conventions"
    vcsUrl = "https://github.com/DaRacci/Minix-Conventions"
    tags = listOf("setup", "helper", "conventions")
    description = "Kotlin and Minecraft conventions and helper."
}
