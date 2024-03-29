package dev.racci.minix.gradle.support

import org.gradle.api.Project
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

public object KotlinJvmPluginSupport : KotlinPluginSupport<KotlinJvmProjectExtension>(
    "org.jetbrains.kotlin.jvm"
) {
    override fun configureRoot(project: Project): Unit = configureSub(project)

    override fun configureSub(project: Project): Unit = with(project) {
        configureBase(project.kotlin)

        dependencies.constraints {
            enforcedPlatform(dependencies.kotlin("bom", KotlinVersion.CURRENT.toString())) {
                because("Enforce all Kotlin dependencies to be the same version.")
            }
        }

        kotlin {
            addPluginSupport(target)
            sourceSets.all(::addPluginSupport)
        }
    }
}
