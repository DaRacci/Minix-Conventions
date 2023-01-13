package dev.racci.minix.gradle.support

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginAware
import java.io.File
import kotlin.reflect.KClass

public sealed class WrappedSupport protected constructor(
    actualId: String,
    actualTarget: PluginTarget,
    private val backingId: String,
) : PluginSupport(actualId, actualTarget) {
    override fun getName(): String {
        return "$backingId:${super.getName()}"
    }

    override fun canConfigureNow(project: Project): Boolean {
        return project.plugins.hasPlugin(backingId) && super.canConfigureNow(project)
    }
}

internal class WrappedExternalSupport(
    internal val pluginId: String,
    internal val actualSupport: PluginSupport
) : WrappedSupport(actualSupport.id, actualSupport.target, pluginId) {
    override fun getName(): String = if (pluginId != "dev.racci.minix") {
        "$pluginId-${super.getName()}"
    } else {
        super.getName()
    }

    override fun configureRoot(project: Project): Nothing {
        throw UnsupportedOperationException("Use the actualSupport variable to configure.")
    }

    override fun configureSub(project: Project): Nothing {
        throw UnsupportedOperationException("Use the actualSupport variable to configure.")
    }

    override fun canConfigureNow(project: Project): Boolean {
        return project.plugins.hasPlugin(pluginId) && actualSupport.canConfigureNow(project)
    }

    data class WrappablePlugin(
        val elementFile: File,
        val pluginId: String,
        val classTarget: () -> KClass<out Plugin<out PluginAware>>
    )
}
