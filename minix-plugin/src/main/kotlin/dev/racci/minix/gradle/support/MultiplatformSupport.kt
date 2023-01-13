package dev.racci.minix.gradle.support

import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

public interface MultiplatformSupport {
    /** Configure a specific [KotlinTarget] for this plugin. */
    public fun configureTarget(target: KotlinTarget): Unit = Unit

    /**
     * Configure a specific [KotlinSourceSet] for this plugin.
     */
    public fun configureSource(source: KotlinSourceSet): Unit = Unit
}
