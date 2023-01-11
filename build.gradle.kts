import dev.racci.minix.gradle.data.MCTarget

// Fixes not being able to use the plugin since,
// for some reason it wants to use the unshaded one.
buildscript {
    dependencies {
        classpath(libs.classgraph)
    }
}

plugins {
    id("dev.racci.minix")
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.plugin.ktlint) apply false
    alias(libs.plugins.kotlin.plugin.dokka)
    alias(libs.plugins.minecraft.paperweight) apply false
}
val kotlinVersion: String by properties
allprojects {
    version = kotlinVersion
}

minix {
    publishing {
        runNumber = System.getenv("BUILD_NUMBER")
        val nms by creating {
            appendRunNumberOrSnapshot = true
        }
        val catalog by creating {
            appendRunNumberOrSnapshot = true
            componentName = "versionCatalog"
        }
    }

    minecraft.withMCTarget(
        project(":nms"),
        MCTarget.Platform.PAPER,
        "1.19.3",
        applyNMS = true,
        applyMinix = false
    )
}

tasks {
    val publish by registering {
        group = "publishing"
        description = "Publishes all publications produced by this project."

        dependsOn(
            gradle.includedBuilds
                .filterNot { it.name == "minix-plugin" }
                .mapNotNull { runCatching { it.task(":publish") }.getOrNull() }
        )
    }

    val publishToMavenLocal by registering {
        group = "publishing"
        description = "Publishes all publications produced by this project to the local Maven repository."

        dependsOn(
            gradle.includedBuilds
                .filterNot { it.name == "minix-plugin" }
                .mapNotNull { runCatching { it.task(":publishToMavenLocal") }.getOrNull() }
        )
    }
}
