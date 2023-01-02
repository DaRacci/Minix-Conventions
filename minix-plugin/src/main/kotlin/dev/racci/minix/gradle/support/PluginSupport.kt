package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.cast
import io.papermc.paperweight.tasks.ScanJarForBadCalls
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.hasPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import kotlin.reflect.KClass

/**
 * Abstraction to support configuring plugins.
 *
 * @property id The plugin ID.
 * @property target The kClass of the required plugin. (Lambda is used to avoid class loading issues)
 */
public sealed class PluginSupport(
    public val id: String,
    public val target: () -> KClass<out Plugin<*>>
) {

    /** Configure the root project for this plugin. */
    public open fun configure(project: Project): Unit = Unit

    /** Configure a subproject for this plugin. */
    public open fun configureSub(project: Project): Unit = Unit

    /** Configure a specific [KotlinTarget] for this plugin. */
    public open fun configureTarget(target: KotlinTarget): Unit = Unit

    internal fun canConfigure(project: Project): Boolean {
        return runCatching { project.plugins.hasPlugin(target()) }.getOrDefault(false)
    }

    internal companion object {
        private val supportedPlugins = PluginSupport::class.sealedSubclasses.map { it.objectInstance!!.cast<PluginSupport>() }

        fun addPluginSupport(target: Any) {
            val (project, func) = when (target) {
                is Project -> target to if (target != target.rootProject) PluginSupport::configureSub else PluginSupport::configure
                is KotlinTarget -> target.project to PluginSupport::configureTarget
                else -> throw IllegalArgumentException("Unsupported target type: ${target::class}")
            }

            supportedPlugins.forEach { support ->
                if (support.canConfigure(project)) {
                    ScanJarForBadCalls.logger.info("Configuring immediate ${support::class.simpleName} for ${project.name}")
                    func(support, target.cast())
                    return@forEach
                }

                ScanJarForBadCalls.logger.info("Adding possible configuration for ${support::class.simpleName} for ${project.name}")
                project.plugins.withId(support.id) {
                    ScanJarForBadCalls.logger.info("Configuring late ${support::class.simpleName} for ${project.name}")
                    func(support, target.cast())
                }
            }
        }
    }
}
