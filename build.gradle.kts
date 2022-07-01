import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("dev.racci.minix.kotlin")
    id("dev.racci.minix.purpurmc")
    id("dev.racci.minix.publication")
    kotlin("plugin.serialization")
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
}

subprojects {

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-opt-in=kotlin.RequiresOptIn"
            )
        }
    }

    if (buildscript.sourceFile?.extension?.toLowerCase() == "kts" &&
        parent != rootProject
    ) {
        generateSequence(parent) { project ->
            project.parent.takeIf { it != rootProject }
        }.forEach { evaluationDependsOn(it.path) }
    }
}

fun included(build: String, task: String) = gradle.includedBuild(build).task(task)

tasks {

    publish {
        dependsOn(gradle.includedBuilds.map { it.task(":publish") })
    }

    publishToMavenLocal {
        dependsOn(gradle.includedBuilds.map { it.task(":publishToMavenLocal") })
    }

    ktlintFormat {
        dependsOn(gradle.includedBuilds.map { it.task(":ktlintFormat") })
    }

    build {
        dependsOn(gradle.includedBuilds.map { it.task(":build") })
    }

    clean {
        dependsOn(gradle.includedBuilds.map { it.task(":clean") })
    }

    dokkaHtmlMultiModule {
        outputDirectory.set(File("$rootDir/docs"))
    }
}
