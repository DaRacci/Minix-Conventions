package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.MinixGradlePlugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

/**
 * A simple abstraction layer around the Multiplatform platform.
 *
 * @property supports The supported [KotlinPlatformType] for [KotlinTarget]'s.
 */
public abstract class AbstractMultiplatformSupport @JvmOverloads protected constructor(
    private vararg val supports: KotlinPlatformType,
    id: String = "dev.racci.minix",
    target: PluginTarget = { MinixGradlePlugin::class },
) : MultiplatformSupport, WrappedSupport(
    id,
    target,
    "org.jetbrains.kotlin.multiplatform",
) {

    /** Configure the project that has the multiplatform plugin applied. */
    protected open fun configure(project: Project): Unit = Unit

    /** Configures the filtered for the supported [KotlinPlatformType] for [KotlinTarget]'s. */
    protected open fun configureTargetFiltered(target: KotlinTarget): Unit = Unit

    /** There is no need to configure the root project. */
    final override fun configureRoot(project: Project): Unit = configure(project)

    /** There is no subproject hierarchy in a multiplatform project. */
    final override fun configureSub(project: Project): Unit = configure(project)

    /** Used to filter the supported [KotlinPlatformType] for [KotlinTarget]'s. */
    final override fun configureTarget(target: KotlinTarget) {
        if (supports.isEmpty() || target.platformType !in supports) return
        configureTargetFiltered(target)
    }
}
