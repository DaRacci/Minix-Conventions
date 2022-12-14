plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    id("dev.racci.minix.common")
    id("dev.racci.minix.purpurmc")
    id("dev.racci.minix.publication")
    // Fix for "Multiple incompatible variants of org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.7.22 were selected". Should be fixed in 1.8.20
    // See: https://youtrack.jetbrains.com/issue/KT-54691/Kotlin-Gradle-Plugin-libraries-alignment-platform
    id("org.jetbrains.kotlin.plugin.sam.with.receiver") version "1.7.22"
    alias(libs.plugins.kotlin.plugin.serialization)
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

fun included(build: String, task: String) = gradle.includedBuild(build).task(task)

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
