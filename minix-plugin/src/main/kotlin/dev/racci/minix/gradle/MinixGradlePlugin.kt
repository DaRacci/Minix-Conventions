package dev.racci.minix.gradle

import dev.racci.minix.gradle.extensions.MinixBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

public class MinixGradlePlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        require(project.rootProject == project) {
            "MinixGradlePlugin must be applied to the root project! (Currently)"
        }

        val baseExtension = project.extensions.create<MinixBaseExtension>("minix", project)
        baseExtension.configure()
    }
}
