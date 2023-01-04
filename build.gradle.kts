import dev.racci.minix.gradle.data.MCTarget

plugins {
    id("dev.racci.minix.publication")
    id("dev.racci.minix")
    alias(libs.plugins.kotlin.plugin.ktlint)
    alias(libs.plugins.minecraft.paperweight) apply false
}

val kotlinVersion: String by project
val runNumber: String = System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"
version = "$kotlinVersion-$runNumber"

minix.minecraft {
    withMCTarget(
        project(":Minix-NMS"),
        MCTarget.Platform.PAPER,
        "1.19.3",
        applyNMS = true,
        applyMinix = false
    )
}

minixPublishing {
    noPublishing = true
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

    ktlintFormat.recDep()
    build.recDep()
    clean.recDep()

    dokkaHtmlMultiModule {
        outputDirectory.set(File("$rootDir/docs"))
    }
}
