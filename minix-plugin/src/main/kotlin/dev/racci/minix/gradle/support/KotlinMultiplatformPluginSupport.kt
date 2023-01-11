package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.ex.disambiguateName
import dev.racci.minix.gradle.tasks.QuickBuildTask
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

public object KotlinMultiplatformPluginSupport : KotlinPluginSupport<KotlinMultiplatformExtension>(
    "org.jetbrains.kotlin.multiplatform",
    { KotlinMultiplatformPluginWrapper::class }
) {
    override fun configureRoot(project: Project): Unit = with(project) {
        configureBaseExtension(project.kotlin)
        addExtraSupport(project.kotlin)
    }

    override fun configureTarget(target: KotlinTarget): Unit = with(target) {
        project.tasks.register<QuickBuildTask>(disambiguateName(QuickBuildTask.TASK_NAME), this)
    }
}
