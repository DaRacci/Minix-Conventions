import java.util.Properties

Properties()
    .apply { load(rootDir.toPath().resolveSibling(Project.GRADLE_PROPERTIES).toFile().inputStream()) }
    .forEach { key, value -> project.ext["$key"] = value }

plugins {
    `kotlin-dsl`
    `maven-publish`
    `kotlin-dsl-precompiled-script-plugins`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.ktlint)
}

buildDir = file("../build/${name.toLowerCase()}")

val kotlinVersion: String by project
val runNumber: String = System.getenv("GITHUB_RUN_NUMBER") ?: "SNAPSHOT"
val minixVersion: String = "${project.ext["version"]}.$runNumber"
version = "$kotlinVersion-$runNumber"

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["runtimeElements"]) {
    mapToOptional()
}

dependencies {
    implementation(libs.gradle.kotlin.jvm)
    implementation(libs.gradle.kotlin.plugin.serialization)
    implementation(libs.gradle.kotlin.plugin.ktlint)
    implementation(libs.gradle.kotlin.plugin.dokka)
    implementation(libs.gradle.minecraft.paperweight)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

java {
    withSourcesJar()
}

publishing {
    repositories {
        maven("https://repo.racci.dev/") {
            url = if (version.toString().endsWith("SNAPSHOT")) {
                url.resolve("snapshots")
            } else url.resolve("releases")

            name = "RacciRepo"
            credentials(PasswordCredentials::class)
        }
    }
}

tasks {

    processResources {
        filesMatching(listOf("Minix-Conventions.properties", "dev.racci.minix.*.gradle.kts")) {
            expand(
                mutableMapOf(
                    "minixConventionsVersion" to version,
                    "minixConventionsKotlinVersion" to kotlinVersion,
                    "minixVersion" to minixVersion
                )
            )
        }
    }

    withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask> {
        workerMaxHeapSize.set("1024m")
    }

    publish { dependsOn("check") }

    build { dependsOn(processResources) }
}
