import dev.racci.minix.gradle.data.MCTarget

plugins {
    id("dev.racci.minix")
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.plugin.ktlint) apply false
    alias(libs.plugins.kotlin.plugin.dokka) apply false
    alias(libs.plugins.minecraft.paperweight) apply false
}

val kotlinVersion: String by project
version = kotlinVersion

minix {
    publishing {
        val nms by creating
        val catalog by creating {
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

subprojects {
    version = rootProject.version

    if (buildscript.sourceFile?.extension?.toLowerCase() == "kts" &&
        parent != rootProject
    ) {
        generateSequence(parent) { project ->
            project.parent.takeIf { it != rootProject }
        }.forEach { evaluationDependsOn(it.path) }
    }
}

fun TaskProvider<*>.recDep() {
    this {
        dependsOn(gradle.includedBuilds.mapNotNull { runCatching { it.task(":$name") }.getOrNull() })
    }
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

    build.recDep()
    clean.recDep()
}
