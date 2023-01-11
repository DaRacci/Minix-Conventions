package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.cast
import dev.racci.minix.gradle.ex.project
import dev.racci.minix.gradle.warnForMissingUsedPlugin
import io.github.classgraph.ClassGraph
import org.gradle.api.Named
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.hasPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
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
// TODO: Subsupport based on support type (e.g. KotlinJvmSupport, KotlinMultiplatformSupport)
public open class PluginSupport(
    public val id: String,
    public val target: () -> KClass<out Plugin<out PluginAware>>
) {
    /** Configure the root project for this plugin. */
    public open fun configureRoot(project: Project): Unit = configureSub(project)

    /** Configure a subproject for this plugin. */
    public open fun configureSub(project: Project): Unit = Unit

    /** Configure a specific [KotlinTarget] for this plugin. */
    public open fun configureTarget(target: KotlinTarget): Unit = Unit

    /**
     * Configure a specific [KotlinSourceSet] for this plugin.
     */
    public open fun configureSource(source: KotlinSourceSet): Unit = Unit

    internal fun canConfigure(project: Project): Boolean {
        return runCatching { project.plugins.hasPlugin(target()) }.getOrDefault(false)
    }

    internal companion object {
        protected val logger: Logger = LoggerFactory.getLogger(PluginSupport::class.java)

        private val supportedPlugins: List<PluginSupport> = ClassGraph()
            .removeTemporaryFilesAfterScan()
            .enableClassInfo()
            .ignoreParentClassLoaders()
            .disableModuleScanning()
            .disableNestedJarScanning()
            .disableRuntimeInvisibleAnnotations()
            // Reject paths and jars that we know aren't using PluginSupport.
            .rejectPaths(
                "org/apache/*",
                "org/jetbrains/*",
                "kotlin/*",
                "org/gradle/*",
                "paper/libs*"
            )
            .rejectJars(
                "kotlin-*.jar",
                "kotlinx-*.jar",
                "jackson-*.jar",
                "io.papermc.*.jar"
            )
            .scan()
            .getSubclasses(PluginSupport::class.java)
            .filter { it.isFinal }
            .map { it.loadClass().kotlin.objectInstance as PluginSupport }

        fun addPluginSupport(target: Any) {
            val name = when (target) {
                is Named -> target.name
                is Project -> target.name
                else -> error("Unknown type: $target")
            }

            val (project, func) = when (target) {
                is Project -> target to if (target != target.rootProject) {
                    PluginSupport::configureSub
                } else {
                    PluginSupport::configureRoot
                }
                // Must keep these after to prevent class loading issues
                is KotlinTarget -> target.project to PluginSupport::configureTarget
                is KotlinSourceSet -> target.project() to PluginSupport::configureSource
                else -> throw IllegalArgumentException("Unsupported target type: ${target::class}")
            }

            supportedPlugins.forEach { support ->
                if (support.canConfigure(project)) {
                    logger.info("Configuring immediate `${support::class.simpleName}` for `$name`.")
                    warnForMissingUsedPlugin(support.id) { func(support, target.cast()) }
                    return@forEach
                }

                logger.info("Adding possible `${support::class.simpleName}` for `$name`.")
                project.plugins.withId(support.id) {
                    logger.info("Configuring late `${support::class.simpleName}` for `$name`.")
                    warnForMissingUsedPlugin(support.id) { func(support, target.cast()) }
                }
            }
        }
    }
}
