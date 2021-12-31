import Dev_racci_minix_platform_gradle.Deps
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("dev.racci.minix.kotlin")
    id("dev.racci.minix.purpurmc")
    id("dev.racci.minix.publication")
    id("dev.racci.minix.testing")
    kotlin("plugin.serialization")
    id("io.gitlab.arturbosch.detekt")
}

dependencies {
    compileOnly(Deps.kotlinx.serialization.json)
    compileOnly(Deps.kotlinx.serialization.kaml)

    api(project("Minix-Platform-Loader"))
}

allprojects {

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
            )
        }
    }

    if (buildscript.sourceFile?.extension?.toLowerCase() == "kts"
        && parent != rootProject
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

    build {
        dependsOn(gradle.includedBuilds.map { it.task(":build") })
        dependsOn(subprojects.map { it.tasks.build })
    }

    clean {
        dependsOn(gradle.includedBuilds.map { it.task(":clean") })
        dependsOn(subprojects.map { it.tasks.clean })
    }

    dokkaHtmlMultiModule {
        outputDirectory.set(File("$rootDir/docs"))
    }
}
