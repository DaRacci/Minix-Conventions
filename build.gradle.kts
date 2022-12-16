plugins {
    `kotlin-dsl-base`
    id("dev.racci.minix.common")
    id("dev.racci.minix.publication")
    alias(libs.plugins.kotlin.plugin.ktlint)
}

val kotlinVersion: String by project
val runNumber: String = System.getenv("GITHUB_RUN_NUMBER") ?: "SNAPSHOT"
version = "$kotlinVersion-$runNumber"

minixPublishing {
    this.noPublishing = true
}

subprojects {
    this.version = rootProject.version

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
            gradle.includedBuilds.mapNotNull { runCatching { it.task(":publish") }.getOrNull() }
        )
    }

    val publishToMavenLocal by registering {
        group = "publishing"
        description = "Publishes all publications produced by this project to the local Maven repository."

        dependsOn(
            gradle.includedBuilds.mapNotNull { runCatching { it.task(":publishToMavenLocal") }.getOrNull() }
        )
    }

    ktlintFormat.recDep()
    build.recDep()
    clean.recDep()

    dokkaHtmlMultiModule {
        outputDirectory.set(File("$rootDir/docs"))
    }
}
