import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("dev.racci.minix.kotlin")
    id("dev.racci.minix.purpurmc")
    id("dev.racci.minix.publication")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
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

fun Task.recDepend(task: String) {
    this.dependsOn(gradle.includedBuilds.mapNotNull { runCatching { it.task(":$task") }.getOrNull() })
}

tasks {

    publish {
        recDepend("publish")
    }

    publishToMavenLocal {
        recDepend("publishToMavenLocal")
    }

    ktlintFormat {
        recDepend("ktlintFormat")
    }

    build {
        recDepend("build")
    }

    clean {
        dependsOn(gradle.includedBuilds.map { it.task(":clean") })
    }

    dokkaHtmlMultiModule {
        outputDirectory.set(File("$rootDir/docs"))
    }
}
