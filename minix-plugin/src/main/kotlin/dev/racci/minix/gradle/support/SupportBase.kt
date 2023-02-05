package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.warnForMissingUsedPlugin
import org.gradle.api.Named
import org.gradle.api.Project
import kotlin.reflect.KFunction2

public sealed interface SupportBase : Named {
    public val pluginId: String

    override fun getName(): String {
        return pluginId
    }

    /** Checks if the support is able to be called immediately or if it needs lazy delegation. */
    public fun canConfigureNow(project: Project): Boolean = project.plugins.hasPlugin(pluginId)

    public fun <T : Any> registerLazySupport(
        project: Project,
        target: T,
        func: KFunction2<SupportBase, T, Unit>,
    ): Unit = project.plugins.withId(pluginId) { func(this@SupportBase, target) }

    public operator fun <T : Any> invoke(
        target: T,
        func: KFunction2<SupportBase, T, Unit>
    ): Unit = warnForMissingUsedPlugin(pluginId) {
        PluginSupport.logger.info("Configuring lazy `${this.name}` for `$target` with `$func`")
        func.call(this, target)
    }
}
