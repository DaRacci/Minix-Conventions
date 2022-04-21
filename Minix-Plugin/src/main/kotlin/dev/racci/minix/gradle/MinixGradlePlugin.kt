package dev.racci.minix.gradle

import dev.racci.minix.gradle.extensions.Extension
import dev.racci.minix.gradle.extensions.MinixKotlinExtension
import dev.racci.minix.gradle.extensions.MinixMinecraftExtension
import dev.racci.minix.gradle.extensions.MinixPublicationExtension
import dev.racci.minix.gradle.tasks.CopyJarTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.register

class MinixGradlePlugin : Plugin<Project> {

    @Input
    var kotlinExtension: Boolean = true

    @Input
    var minecraftExtension: Boolean = true

    @Input
    var publicationExtension: Boolean = true

    private val ext = mutableListOf<Extension>()

    override fun apply(project: Project) {
        project.run {
            pluginManager.apply {
                apply("org.gradle.java")
                println("abcaouhef")
                // apply(BukkitPlugin::class)
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
        }
    }
}
