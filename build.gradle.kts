plugins {
    `kotlin-dsl`
    id("dev.racci.minix.common")
    id("dev.racci.minix.publication")
    alias(libs.plugins.kotlin.plugin.ktlint)
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

    publish.recDep()
    publishToMavenLocal.recDep()
    ktlintFormat.recDep()
    build.recDep()
    clean.recDep()

    dokkaHtmlMultiModule {
        outputDirectory.set(File("$rootDir/docs"))
    }
}
