package dev.racci.minix.gradle.support

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

public object KotlinMultiplatformPluginSupport : KotlinPluginSupport<KotlinMultiplatformExtension>(
    "org.jetbrains.kotlin.multiplatform"
) {
    override fun configureRoot(project: Project): Unit = configureSub(project)

    override fun configureSub(project: Project) {
        configureBaseExtension(project.kotlin)

        project.kotlin {
            targets.all(::addPluginSupport)
            sourceSets.all(::addPluginSupport)
        }
    }
}
