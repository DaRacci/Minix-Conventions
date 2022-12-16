plugins {
    `kotlin-dsl`
    id("dev.racci.minix.common")
    id("dev.racci.minix.publication")
    alias(libs.plugins.kotlin.plugin.ktlint)
}

minixPublishing {
    this.noPublishing = true
}

subprojects {

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
