package dev.racci.minix.gradle.extensions

import dev.racci.minix.gradle.data.MCTarget
import dev.racci.minix.gradle.ex.highestOrderExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Input

public class MinixMinecraftExtension(override val rootProject: Project) : ExtensionBase() {
    @[Input PublishedApi]
    internal val mcTargets: MutableSet<MCTarget> = mutableSetOf()

    @JvmName("withMCTargetReceiver")
    public fun Project.withMCTarget(
        platform: MCTarget.Platform,
        version: String? = null,
        applyDefaultDependencies: Boolean = true,
        applyMinix: Boolean = true,
        applyNMS: Boolean = false
    ) {
        highestOrderExtension().minecraft.mcTargets.add(
            MCTarget(
                this,
                platform,
                applyDefaults = applyDefaultDependencies,
                applyMinix = applyMinix,
                applyNMS = applyNMS,
                version = version
            )
        )
    }

    public fun withMCTarget(
        project: Project,
        platform: MCTarget.Platform,
        version: String? = null,
        applyDefaultDependencies: Boolean = true,
        applyMinix: Boolean = true,
        applyNMS: Boolean = false
    ) {
        project.withMCTarget(platform, version, applyDefaultDependencies, applyMinix, applyNMS)
    }

    override fun configure(project: Project) = with(project) {
        mcTargets.forEach(MCTarget::configure)
    }
}
