package dev.racci.minix.gradle.support

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

public object KotlinMultiplatformPluginSupport : KotlinPluginSupport<KotlinMultiplatformExtension>(
    "org.jetbrains.kotlin.multiplatform",
    { KotlinMultiplatformPluginWrapper::class }
) {
    override fun configureRoot(project: Project): Unit = with(project) {
        configureBaseExtension(project.kotlin)
        addExtraSupport(project.kotlin)
    }
}
