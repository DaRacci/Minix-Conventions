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
import org.jetbrains.kotlin.gradle.utils.loadPropertyFromResources
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
) : Named {
    override fun getName(): String {
        return id
    }

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

    internal open fun canConfigure(project: Project): Boolean {
        return runCatching { project.plugins.hasPlugin(target()) }.getOrDefault(false)
    }

    internal companion object {
        protected val logger: Logger = LoggerFactory.getLogger(PluginSupport::class.java)

        private val supportedPlugins: List<WrappedExternalSupport> = ClassGraph()
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
            .use { scan ->
                val externSupports = scan.getResourcesMatchingWildcard("META-INF/gradle-plugins/*.properties")
                    .map { resource ->
                        val pluginId = resource.path.substringAfterLast("/").substringBeforeLast(".")
                        val pluginClass = loadPropertyFromResources(resource.path, "implementation-class")

                        WrappedExternalSupport.WrappablePlugin(resource.classpathElementFile, pluginId) {
                            Class.forName(pluginClass).kotlin.cast()
                        }
                    }

                scan.getSubclasses(PluginSupport::class.java)
                    .filter { it.isFinal }
                    .mapNotNull { info ->
                        externSupports.find { it.elementFile == info.classpathElementFile }?.let { wrappable ->
                            val obj = info.loadClass().kotlin.objectInstance as? PluginSupport ?: return@mapNotNull null
                            WrappedExternalSupport(wrappable.pluginId, obj)
                        }
                    }
            }

        internal fun addPluginSupport(target: Any) {
            val targetName = when (target) {
                is Project -> target.name
                is KotlinTarget -> "[kotlinTarget:${target.name}] of ${target.project.name}"
                is KotlinSourceSet -> "[sourceSet:${target.name}] of ${target.project().name}"
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

            logger.info("Using name `$targetName`, with project `${project.name}`, and func `$func`")

            supportedPlugins.forEach { support ->
                if (support.canConfigure(project)) {
                    logger.info("Configuring immediate `${support.name}` for `$targetName`.")
                    warnForMissingUsedPlugin(support.id) { func(support, target.cast()) }
                    return@forEach
                }

                logger.info("Adding possible `${support.name}` for `$targetName`.")
                project.plugins.withId(support.id) {
                    logger.info(
                        "The owning plugin for `${support.name}` was found, checking if the plugin can be configured."
                    )
                    project.plugins.withId(support.pluginId) {
                        logger.info("Configuring late `${support.name}` for `$targetName`.")
                        warnForMissingUsedPlugin(support.id) { func(support, target.cast()) }
                    }
                }
            }
        }
    }
}
