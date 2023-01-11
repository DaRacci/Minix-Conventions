package dev.racci.minix.gradle.support

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginAware
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import java.io.File
import kotlin.reflect.KClass

internal class WrappedExternalSupport(
    internal val pluginId: String,
    private val actualSupport: PluginSupport
) : PluginSupport(actualSupport.id, actualSupport.target) {
    override fun getName(): String = if (pluginId != "dev.racci.minix") {
        "$pluginId-$id"
    } else {
        super.getName()
    }

    override fun configureRoot(project: Project) {
        actualSupport.configureRoot(project)
    }

    override fun configureSub(project: Project) {
        actualSupport.configureSub(project)
    }

    override fun configureTarget(target: KotlinTarget) {
        actualSupport.configureTarget(target)
    }

    override fun configureSource(source: KotlinSourceSet) {
        actualSupport.configureSource(source)
    }

    override fun canConfigure(project: Project): Boolean {
        return project.plugins.hasPlugin(pluginId) && actualSupport.canConfigure(project)
    }

    data class WrappablePlugin(
        val elementFile: File,
        val pluginId: String,
        val classTarget: () -> KClass<out Plugin<out PluginAware>>
    )
}
