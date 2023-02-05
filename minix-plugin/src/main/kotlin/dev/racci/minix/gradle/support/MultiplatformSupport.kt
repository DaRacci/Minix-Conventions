package dev.racci.minix.gradle.support

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

public interface MultiplatformSupport : SupportBase {

    public companion object {
        public const val MULTIPLATFORM_ID: String = "org.jetbrains.kotlin.multiplatform"
    }

    /** Configures the project that has the multiplatform plugin applied. */
    public fun configure(project: Project): Unit = Unit

    /** Configure a specific [KotlinTarget] for this plugin. */
    public fun configureTarget(target: KotlinTarget): Unit = Unit

    /**
     * Configure a specific [KotlinSourceSet] for this plugin.
     */
    public fun configureSource(source: KotlinSourceSet): Unit = Unit
}
