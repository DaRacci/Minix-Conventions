package dev.racci.minix.gradle.extensions

import dev.racci.minix.gradle.MinixGradlePlugin
import dev.racci.minix.gradle.access
import dev.racci.minix.gradle.annotations.TopLevelDSLMarker
import dev.racci.minix.gradle.ex.recursiveSubprojects
import dev.racci.minix.gradle.ex.whenEvaluated
import dev.racci.minix.gradle.support.PluginSupport
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.provideDelegate
import org.slf4j.Logger
import kotlin.reflect.KProperty0

@Suppress("UnnecessaryAbstractClass") // Gradle requires this to be abstract.
public abstract class MinixBaseExtension(private val plugin: MinixGradlePlugin) {
    /**
     * A list of the subprojects and kotlin mpp targets that aren't touched by the plugin.
     */
    @get:Input
    @get:Optional
    public val ignoredTargets: MutableList<String> = mutableListOf("metadata")

    @get:Input
    public val minecraft: MinixMinecraftExtension by lazy { MinixMinecraftExtension(plugin.project) }

    @get:Input
    public val publishing: MinixPublishingExtension by lazy { MinixPublishingExtension(plugin.project) }

    @TopLevelDSLMarker
    public inline fun minecraft(block: MinixMinecraftExtension.() -> Unit) {
        block(minecraft)
    }

    @TopLevelDSLMarker
    public inline fun publishing(block: MinixPublishingExtension.() -> Unit) {
        block(publishing)
    }

    internal fun configure(): Unit = with(plugin.project) {
        if (rootProject != this) {
            buildDir = rootProject.buildDir.resolve(project.name.lowercase())
        }

        PluginSupport.addPluginSupport(project)

        recursiveSubprojects().forEach { subproject ->
            subproject.beforeEvaluate {
                buildDir = rootProject.buildDir.resolve(project.name.lowercase())

                if (subproject.name in ignoredTargets) {
                    return@beforeEvaluate logger.prInfo(
                        "Ignoring subproject: ${subproject.name}"
                    )
                }

                PluginSupport.addPluginSupport(subproject)
            }
        }

        whenEvaluated {
            @Suppress("UNCHECKED_CAST")
            fun maybeLazyConfigure(prop: KProperty0<ExtensionBase>) {
                val lazy = prop.access { getDelegate() as Lazy<ExtensionBase> }
                if (!lazy.isInitialized()) {
                    return logger.prInfo("Not configuring ${prop.name}.")
                }

                logger.prInfo("Configuring ${prop.name}...")
                lazy.value.configure(project)
            }

            maybeLazyConfigure(::minecraft)
            maybeLazyConfigure(::publishing)
        }
    }

    private companion object {
        fun Logger.prInfo(message: String) = info(":baseExtension $message")
    }
}
