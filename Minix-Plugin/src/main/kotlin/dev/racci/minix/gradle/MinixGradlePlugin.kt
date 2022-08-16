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
import org.gradle.kotlin.dsl.register
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

@Suppress("UNUSED")
class MinixGradlePlugin : Plugin<Project> {

    @Input
    var kotlinExtension: Boolean = true

    @Input
    var minecraftExtension: Boolean = true

    @Input
    var publicationExtension: Boolean = true

    @Input
    var copyJarTask: Boolean = true

    @Input
    var subprojectExtensions: MutableMap<Project, List<KClass<out Extension>>> = mutableMapOf()

    private var extensions = listOf(
        MinixStandardExtension::class,
        MinixMinecraftExtension::class,
        MinixPublicationExtension::class
    )

    override fun apply(project: Project) {
        project.run {
            pluginManager.apply {
                apply(JavaPlugin::class)
            }

            extensions.add("minix", this@MinixGradlePlugin)

            for (extension in this@MinixGradlePlugin.extensions) {
                val name = extension.simpleName!!.removeSuffix("Extension").replaceFirstChar(Char::lowercase)
                val func = this@MinixGradlePlugin::class.declaredMemberProperties.first { it.name == name }

                if (!(func.call(this@MinixGradlePlugin) as Boolean)) continue

                fun getInst(project: Project): Extension {
                    val inst = extension.primaryConstructor!!.call(project)
                    inst.apply()
                    return inst
                }

                this.extensions.add(name, getInst(this))
                for (subproject in this.subprojects) {
                    val extensions = subprojectExtensions[subproject]
                    if (!extensions.isNullOrEmpty() && !extensions.contains(extension)) continue

                    subproject.extensions.add(name, getInst(subproject))
                }
            }

            if (copyJarTask) {
                val copyJar = tasks.register<CopyJarTask>("copyJar")
                tasks.named("build").get().dependsOn(copyJar)
            }
        }
    }
}
