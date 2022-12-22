import java.util.*

Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { key, value -> project.ext["$key"] = value }

plugins {
    `maven-publish`
    alias(libs.plugins.kotlin.dsl)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.kotlin.plugin.ktlint)
}

val compileAndTest by configurations.creating
configurations {
    compileOnly.get().extendsFrom(compileAndTest)
    testImplementation.get().extendsFrom(compileAndTest, implementation.get())
}

@Suppress("UnstableApiUsage")
dependencies {
    // Align the version of all kotlin components
    implementation(platform(kotlin("bom", libs.versions.kotlin.asProvider().get())))
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.immutableCollections)
    implementation(libs.arrow.core)

    // All the plugins that are used to configure.
    // TODO: Figure out how to apply these without implementing specific versions
    compileAndTest(gradleApi())
    compileAndTest(gradleKotlinDsl())
    compileAndTest(libs.gradle.kotlin.plugin.serialization)
    compileAndTest(libs.gradle.minecraft.pluginYML)
    compileAndTest(libs.gradle.kotlin.jvm)
    compileAndTest(libs.gradle.kotlin.plugin.ktlint)
    compileAndTest(libs.gradle.kotlin.plugin.dokka)
    compileAndTest(libs.gradle.shadow)
    compileAndTest(libs.gradle.minecraft.paperweight)
    compileAndTest(libs.gradle.kotlin.mpp)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.testing.junit5)
    testImplementation(libs.testing.kotest.junit5)
    testImplementation(libs.testing.kotest.properties)
    testImplementation(libs.testing.kotest.assertions)
    testImplementation(libs.testing.mockK)
    testImplementation(libs.testing.strikt)
    testImplementation(gradleTestKit())

    configurations.configureEach {
        exclude(group = "org.apache.logging.log4j", module = "log4j-core")
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
        exclude(group = "org.apache.logging.log4j", module = "log4j-slf4j-impl")
    }
}

kotlin {
    jvmToolchain(17)
    sourceSets.configureEach {
        explicitApi()
        languageSettings {
            languageVersion = "1.7"
            apiVersion = "1.7"
        }
    }
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
