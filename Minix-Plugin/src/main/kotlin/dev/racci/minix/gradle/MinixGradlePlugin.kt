package dev.racci.minix.gradle

import dev.racci.minix.gradle.extensions.Extension
import dev.racci.minix.gradle.extensions.MinixKotlinExtension
import dev.racci.minix.gradle.extensions.MinixMinecraftExtension
import dev.racci.minix.gradle.extensions.MinixPublicationExtension
import dev.racci.minix.gradle.tasks.CopyJarTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.register

class MinixGradlePlugin : Plugin<Project> {

    @Input
    var kotlinExtension: Boolean = true

    @Input
    var minecraftExtension: Boolean = true

    @Input
    var publicationExtension: Boolean = true

    @Input
    var subprojectExtensions: MutableMap<String, List<String>> =
        mutableMapOf("ALL" to listOf("ALL")) // Apply all enabled extensions to all subprojects

    private val ext = mutableListOf<Extension>()

    override fun apply(project: Project) {
        project.run {
            pluginManager.apply {
                apply(JavaPlugin::class)
            }

            extensions.add("minix", this@MinixGradlePlugin)

            if (kotlinExtension) {
                ext += MinixKotlinExtension(this)
            }

            if (minecraftExtension) {
                ext += MinixMinecraftExtension(this)
            }

            if (publicationExtension) {
                ext += MinixPublicationExtension(this)
            }

            ext.forEach {
                val name = it::class.simpleName!!.substringAfter("Minix").substringBefore("Extension")
                extensions.add("minix$name", it)
                it.apply()
            }

            val copyJar = tasks.register<CopyJarTask>("copyJar")

            tasks.named("build").configure {
                dependsOn(copyJar)
            }

            for (subproject in project.subprojects) {
                val upperName = subproject.name.toUpperCase()
                if (!subprojectExtensions.contains("ALL") ||
                    upperName !in subprojectExtensions.keys ||
                    subprojectExtensions[upperName]!!.isEmpty()
                ) continue

                if (subprojectExtensions[upperName]!!.contains("ALL")) {
                    extensions.add("minixKotlin", MinixKotlinExtension(subproject))
                    extensions.add("minixMinecraft", MinixMinecraftExtension(subproject))
                    extensions.add("minixPublication", MinixPublicationExtension(subproject))
                } else {
                    for (extension in subprojectExtensions[subproject.name.toUpperCase()] ?: continue) {
                        when (extension.toUpperCase()) {
                            "KOTLIN" -> extensions.add("minixKotlin", MinixKotlinExtension(subproject))
                            "MINECRAFT" -> extensions.add("minixMinecraft", MinixMinecraftExtension(subproject))
                            "PUBLICATION" -> extensions.add("minixPublication", MinixPublicationExtension(subproject))
                        }
                    }
                }
            }
        }
    }
}
