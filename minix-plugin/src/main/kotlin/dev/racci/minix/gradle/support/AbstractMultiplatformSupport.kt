package dev.racci.minix.gradle.support

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

/**
 * A simple abstraction layer around the Multiplatform platform.
 *
 * @property supports The supported [KotlinPlatformType] for [KotlinTarget]'s.
 */
public abstract class AbstractMultiplatformSupport protected constructor(
    private vararg val supports: KotlinPlatformType,
    override val pluginId: String,
) : MultiplatformSupport {

    override fun getName(): String {
        return "MultiplatformSupport:[$pluginId]"
    }

    final override fun canConfigureNow(project: Project): Boolean {
        return project.plugins.hasPlugin(MultiplatformSupport.MULTIPLATFORM_ID) && super.canConfigureNow(project)
    }

    /** Configures the filtered for the supported [KotlinPlatformType] for [KotlinTarget]'s. */
    protected open fun configureTargetFiltered(target: KotlinTarget): Unit = Unit

    /** Used to filter the supported [KotlinPlatformType] for [KotlinTarget]'s. */
    final override fun configureTarget(target: KotlinTarget) {
        if (supports.isEmpty() || target.platformType !in supports) return
        configureTargetFiltered(target)
    }
}
