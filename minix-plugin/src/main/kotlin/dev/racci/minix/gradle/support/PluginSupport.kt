package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.cast
import dev.racci.minix.gradle.warnForMissingUsedPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.hasPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    public open fun configureTrueRoot(project: Project): Unit = configureRelativeRoot(project)

    /**
     * Configures the root plugin of which applied this plugin.
     * In this case the “Real Root” isn't this project.
     */
    public open fun configureRelativeRoot(project: Project): Unit = configureSub(project)

    /** Configure a subproject for this plugin. */
    public open fun configureSub(project: Project): Unit = Unit

    /** Configure a specific [KotlinTarget] for this plugin. */
    public open fun configureTarget(target: KotlinTarget): Unit = Unit

    internal fun canConfigure(project: Project): Boolean {
        return runCatching { project.plugins.hasPlugin(target()) }.getOrDefault(false)
    }

    internal companion object {
        protected val logger: Logger = LoggerFactory.getLogger(PluginSupport::class.java)
        private val supportedPlugins =
            PluginSupport::class.sealedSubclasses.map { it.objectInstance!!.cast<PluginSupport>() }

        fun addPluginSupport(
            target: Any,
            relativeRoot: Boolean
        ) {
            val (project, func) = when (target) {
                is Project -> {
                    target to when {
                        relativeRoot -> PluginSupport::configureRelativeRoot
                        target != target.rootProject -> PluginSupport::configureSub
                        else -> PluginSupport::configureTrueRoot
                    }
                }
                is KotlinTarget -> target.project to PluginSupport::configureTarget
                else -> throw IllegalArgumentException("Unsupported target type: ${target::class}")
            }

            supportedPlugins.forEach { support ->
                if (support.canConfigure(project)) {
                    logger.info("Configuring immediate ${support::class.simpleName} for ${project.name}")
                    warnForMissingUsedPlugin(support.id) { func(support, target.cast()) }
                    return@forEach
                }

                logger.info("Adding possible configuration for ${support::class.simpleName} for ${project.name}")
                project.plugins.withId(support.id) {
                    logger.info("Configuring late ${support::class.simpleName} for ${project.name}")
                    warnForMissingUsedPlugin(support.id) { func(support, target.cast()) }
                }
            }
        }
    }
}
