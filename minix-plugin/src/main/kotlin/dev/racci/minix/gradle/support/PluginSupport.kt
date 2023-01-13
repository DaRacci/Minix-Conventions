package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.cast
import dev.racci.minix.gradle.ex.project
import dev.racci.minix.gradle.exceptions.PluginSupportException
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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KFunction2
import kotlin.reflect.full.memberFunctions

internal typealias PluginTarget = () -> KClass<out Plugin<out PluginAware>>

/**
 * Abstraction to support configuring plugins.
 *
 * @property id The plugin ID.
 * @property target The kClass of the required plugin. (Lambda is used to avoid class loading issues)
 */
public open class PluginSupport(
    public val id: String,
    public val target: PluginTarget
) : Named {
    override fun getName(): String {
        return id
    }

    /** Configure the root project for this plugin. */
    public open fun configureRoot(project: Project): Unit = configureSub(project)

    /** Configure a subproject for this plugin. */
    public open fun configureSub(project: Project): Unit = Unit

    /** Checks if the support is able to be called immediately or if it needs lazy delegation. */
    public open fun canConfigureNow(project: Project): Boolean {
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
            val targetName = formattedName(target)
            val (project, func) = funcPair(target)

            supportedPlugins.forEach { support ->
                if (support.actualSupport::class.memberFunctions
                    .none { it.name == func.name }
                ) { return@forEach }

                if (support.canConfigureNow(project)) {
                    logger.info("Configuring immediate `${support.name}` for `$targetName`.")
                    warnForMissingUsedPlugin(support.id) { func(support.actualSupport, target) }
                    return@forEach
                }

                logger.info("Adding possible `${support.name}` for `$targetName`.")
                project.plugins.withId(support.id) {
                    logger.info(
                        "The owning plugin for `${support.name}` was found, checking if the plugin can be configured."
                    )
                    project.plugins.withId(support.pluginId) {
                        logger.info("Configuring late `${support.name}` for `$targetName`.")
                        warnForMissingUsedPlugin(support.id) { func(support.actualSupport, target) }
                    }
                }
            }
        }

        /** Gets a formatting name for logging/debugging. */
        @OptIn(ExperimentalContracts::class)
        private fun formattedName(any: Any): String {
            contract {
                returns() implies (any is Project || any is KotlinTarget || any is KotlinSourceSet)
            }

            return when (any) {
                is Project -> any.name
                // Must keep these after to prevent class loading issues.
                is KotlinTarget -> "[kotlinTarget:${any.name}] of ${any.project.name}"
                is KotlinSourceSet -> "[sourceSet:${any.name}] of ${any.project().name}"
                else -> PluginSupportException.unsupportedType(any)
            }
        }

        /** Gets the objects project, and the configure function for its type. */
        private fun <T> funcPair(any: T): Pair<Project, KFunction2<PluginSupport, T, Unit>> {
            val pair = when (any) {
                // TODO: Relative plugin root.
                is Project -> any to if (any != any.rootProject) {
                    PluginSupport::configureSub
                } else {
                    PluginSupport::configureRoot
                }
                // Must keep these after to prevent class loading issues.
                is KotlinTarget -> any.project to MultiplatformSupport::configureTarget
                is KotlinSourceSet -> any.project() to MultiplatformSupport::configureSource
                else -> PluginSupportException.unsupportedType(any)
            }

            return pair.cast()
        }
    }
}
