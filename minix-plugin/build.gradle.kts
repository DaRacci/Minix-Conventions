import java.util.Properties

Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { key, value -> project.ext["$key"] = value }

plugins {
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin.dsl)
    alias(libs.plugins.gradle.publish)
    alias(libs.plugins.kotlin.plugin.ktlint)
}

buildDir = file("../build/$name")

val shadowImplementation: Configuration by configurations.creating
val compileAndTest: Configuration by configurations.creating
configurations {
    compileAndTest.extendsFrom(shadowImplementation)
    compileOnly.get().extendsFrom(compileAndTest)
    testImplementation.get().extendsFrom(compileAndTest)
}

@Suppress("UnstableApiUsage")
dependencies {
    shadowImplementation(libs.classgraph)

    compileAndTest(gradleApi())
    compileAndTest(gradleKotlinDsl())
    compileAndTest(libs.gradle.shadow)
    compileAndTest(libs.gradle.kotlin.jvm)
    compileAndTest(libs.gradle.kotlin.mpp)
    compileAndTest(libs.gradle.kotlin.plugin.dokka)
    compileAndTest(libs.gradle.kotlin.plugin.ktlint)
    compileAndTest(libs.gradle.minecraft.pluginYML)
    compileAndTest(libs.gradle.minecraft.paperweight)

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

tasks {
    test {
        enabled = false
        useJUnitPlatform()
    }

    processResources {
        filesMatching("minix.properties") {
            expand(
                "jdk" to java.sourceCompatibility.toString(),
                "kotlin" to libs.versions.kotlin.asProvider().get(),
                "mc" to libs.versions.minecraft.get(),
                "minix" to libs.versions.minix.get()
            )
        }
    }

    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(shadowImplementation)

        listOf(
            "io.github.classgraph",
            "nonapi.io.github.classgraph",
        ).map { it to it.split('.').last() }.forEach { (original, last) ->
            relocate(original, "dev.racci.classgraph.libs.$last")
        }
    }
}

// Work around publishing shadow jars
publishing.publications
    .withType<MavenPublication>()
    .filter { it.name == "pluginMaven" }
    .forEach { publication -> publication.setArtifacts(listOf(tasks.shadowJar)) }

publishing.repositories.maven("https://repo.racci.dev/") {
    url = if (version.toString().endsWith("SNAPSHOT")) {
        url.resolve("snapshots")
    } else {
        url.resolve("releases")
    }

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
