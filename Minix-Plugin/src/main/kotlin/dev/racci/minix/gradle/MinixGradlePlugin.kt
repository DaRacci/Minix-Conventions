package dev.racci.minix.gradle

import dev.racci.minix.gradle.extensions.Extension
import dev.racci.minix.gradle.extensions.MinixMinecraftExtension
import dev.racci.minix.gradle.extensions.MinixPublicationExtension
import dev.racci.minix.gradle.extensions.MinixStandardExtension
import dev.racci.minix.gradle.tasks.CopyJarTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

public class MinixGradlePlugin : Plugin<Project> {
    private lateinit var project: Project

    @Input
    public var standardExtension: Boolean = true

    @Input
    public var minecraftExtension: Boolean = true

    @Input
    public var publicationExtension: Boolean = true

    @Input
    public var copyJarTask: Boolean = true

    @Input
    public var subprojectExtensions: MutableMap<Project, List<KClass<out Extension>>> = mutableMapOf()

    private var minixExtensions = listOf(
        MinixStandardExtension::class,
        MinixMinecraftExtension::class,
        MinixPublicationExtension::class
    )

    public fun MinixGradlePlugin.options(block: MinixStandardExtension.() -> Unit) {
        if (!standardExtension) return

        project.extensions.getByType<MinixStandardExtension>().block()
    }

    override fun apply(project: Project) {
        this.project = project
        project.pluginManager.apply(JavaPlugin::class)
        project.extensions.add("minix", this@MinixGradlePlugin)

        project.afterEvaluate {
            for (extension in minixExtensions) {
                if (!shouldEnable(extension)) {
                    println("Skipping extension ${extension.simpleName}")
                    continue
                }

                val name = extension.simpleName!!.removeSuffix("Extension").decapitalize()

                println("Applying extension $name")
                extension.primaryConstructor!!.call(project).also { ext ->
                    project.extensions.add(name, ext)
                    ext.apply()
                }

                for (subproject in project.subprojects) {
                    val extensions = subprojectExtensions[subproject]
                    if (!extensions.isNullOrEmpty() && !extensions.contains(extension)) continue

                    println("Applying extension $name to subproject ${subproject.name}")
                    extension.primaryConstructor!!.call(subproject).also { ext ->
                        subproject.extensions.add(name, ext)
                        ext.apply()
                    }
                }
            }

            if (!copyJarTask) return@afterEvaluate
            project.tasks.apply {
                val copyJar = project.tasks.register<CopyJarTask>("copyJar") {
                    onlyIf { System.getenv("CI") != "true" }
                }
                named("build").get().dependsOn(copyJar)
            }
        }
    }

    private fun shouldEnable(instance: KClass<out Extension>) = when (instance) {
        MinixStandardExtension::class -> standardExtension
        MinixMinecraftExtension::class -> minecraftExtension
        MinixPublicationExtension::class -> publicationExtension
        else -> throw IllegalArgumentException("Unknown extension type: ${instance::class.simpleName}")
    }
}
