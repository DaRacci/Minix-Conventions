package dev.racci.minix.gradle

import dev.racci.minix.gradle.extensions.MinixBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

public class MinixGradlePlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        if (KotlinVersion.CURRENT.toString() != Constants.KOTLIN_VERSION) {
            logger.warn("WARNING: Unsupported kotlin version.")
            logger.warn(
                """
                The `minix-plugin` rely on features of Kotlin ${Constants.KOTLIN_VERSION},
                These features may work differently than in the requested version `${KotlinVersion.CURRENT}`
                """.trimIndent()
            )
        }

        val baseExtension = project.extensions.create<MinixBaseExtension>("minix", project)
        baseExtension.configure()
    }
}
