package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.cast
import dev.racci.minix.gradle.ex.project
import dev.racci.minix.gradle.exceptions.PluginSupportException
import io.github.classgraph.ClassGraph
import org.gradle.api.Named
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.utils.loadPropertyFromResources
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KFunction2
import kotlin.reflect.full.memberFunctions

/**
 * Abstraction to support configuring plugins.
 *
 * @property pluginId The plugin ID.
 */
public abstract class PluginSupport(
    override val pluginId: String
) : Named, SupportBase {

    /** Configure the root project for this plugin. */
    public open fun configureRoot(project: Project): Unit = Unit

    /** Configure a subproject for this plugin. */
    public open fun configureSub(project: Project): Unit = Unit

    internal companion object {
        private val backingLogger = LoggerFactory.getLogger(PluginSupport::class.java)
        internal val logger: Logger = object : Logger by backingLogger {
            private val prefix = ":PluginSupport "
            override fun info(msg: String) = backingLogger.info(prefix + msg)
            override fun warn(msg: String) = backingLogger.warn(prefix + msg)
            override fun debug(msg: String) = backingLogger.debug(prefix + msg)
            override fun error(msg: String) = backingLogger.error(prefix + msg)
        }

        // TODO: Limit search to only the classpath of the root project.
        private val supportedPlugins: List<WrappedSupport> = ClassGraph()
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
                            WrappedSupport.of(wrappable.pluginId, obj)
                        }
                    }
            }

        internal fun addPluginSupport(target: Any) {
            val targetName = formattedName(target)
            val (project, func) = funcPair(target)

            logger.info("Adding plugin support for `$targetName` with `${func.name}`.")

            @Suppress("LoopWithTooManyJumpStatements")
            for (support in supportedPlugins) {
                if (support.delegate::class.memberFunctions.none { it.name == func.name }) {
                    logger.info(
                        "Skipping support `${support.name}` for `$targetName` as it doesn't have the correct function."
                    )
                    continue
                }

                if (support.canConfigureNow(project)) {
                    logger.info("Configuring instant support `${support.name}` for `$targetName` with `${func.name}`.")
                    support(target, func)
                    continue
                }

                logger.info("Adding lazy support for `${support.name}` for `$targetName` with `${func.name}`.")
                support.registerLazySupport(project, target, func)
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
        private fun <T> funcPair(any: T): Pair<Project, KFunction2<SupportBase, T, Unit>> {
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
