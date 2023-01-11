package dev.racci.minix.gradle

import dev.racci.minix.gradle.extensions.MinixBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

public class MinixGradlePlugin : Plugin<Project> {
    public companion object {
        private var trueInstance: MinixGradlePlugin? = null

        /** The highest order instance of a MinixGradlePlugin. */
        public fun trueInstance(): MinixGradlePlugin = trueInstance ?: error(
            "MinixGradlePlugin has not been applied yet."
        )
    }

    public lateinit var project: Project
        private set

    public lateinit var baseExtension: MinixBaseExtension
        private set

    override fun apply(project: Project): Unit = with(project) {
        if (trueInstance == null) {
            trueInstance = this@MinixGradlePlugin
        }

        if (KotlinVersion.CURRENT.toString() != Constants.KOTLIN_VERSION) {
            logger.warn("WARNING: Unsupported kotlin version.")
            logger.warn(
                """
                The `minix-plugin` rely on features of Kotlin ${Constants.KOTLIN_VERSION},
                These features may work differently than in the requested version `${KotlinVersion.CURRENT}`
                """.trimIndent()
            )
        }

        this@MinixGradlePlugin.project = this
        this@MinixGradlePlugin.baseExtension = project.extensions.create<MinixBaseExtension>(
            "minix",
            this@MinixGradlePlugin
        ).also(MinixBaseExtension::configure)
    }
}
