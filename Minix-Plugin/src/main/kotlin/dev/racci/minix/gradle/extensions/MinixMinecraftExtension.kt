package dev.racci.minix.gradle.extensions

import net.minecrell.pluginyml.bukkit.BukkitPlugin
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.bungee.BungeePlugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory.VersionFactory
import org.gradle.api.provider.Provider
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

class MinixMinecraftExtension(private val project: Project) : Extension {

    @Input
    var useTentacles: Boolean = false

    @Input
    var mcVersion: String = "1.19.2-R0.1-SNAPSHOT"

    @Input
    var useNMS: Boolean = false

    @Input
    var addMinixDependency: Boolean = true

    @Input
    var projectType: MinecraftProjectType = MinecraftProjectType.BUKKIT

    override fun apply() {
        with(project) {
            addMinecraftDependency(this)
            addMinixDependencies(this)

            val lib: Configuration = configurations.maybeCreate("lib")
            extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
                configurations.getByName(it.compileClasspathConfigurationName).extendsFrom(lib)
                configurations.getByName(it.runtimeClasspathConfigurationName).extendsFrom(lib)
                configurations.getByName(it.apiElementsConfigurationName).extendsFrom(lib)
            }

            when (projectType) {
                MinecraftProjectType.BUKKIT -> {
                    this.afterEvaluate { project ->
                        val ext =
                            findHighestExtension<BukkitPluginDescription>(project, "bukkit") ?: return@afterEvaluate
                        val mappedDeps = lib.dependencies.map { "${it.group}:${it.name}:${it.version}" }

                        if (ext.libraries == null) ext.libraries = emptyList()
                        ext.libraries = ext.libraries!! + mappedDeps
                    }

                    BukkitPlugin::class
                }

                MinecraftProjectType.BUNGEECORD -> BungeePlugin::class
            }.apply(pluginManager::apply)

            @Suppress("UnstableApiUsage")
            tasks.named<ProcessResources>("processResources") {
                filesMatching("plugin.yml") {
                    expand(mutableMapOf("version" to version))
                }
            }
        }
    }

    private fun addMinecraftDependency(project: Project) {
        val dependencyStr = StringBuilder()
        val (major, minor) = mcVersion.split('.').take(2).map(String::toInt)

        val module = when {
            useTentacles -> TENTACLES_MODULE
            major == 1 && minor < 18 -> OLD_PURPUR_MODULE
            else -> NEW_PURPUR_MODULE
        }

        when (module) {
            TENTACLES_MODULE -> project.repositories.maven("$RACCI_REPO/snapshots")
            else -> project.repositories.maven(PURPUR_REPO)
        }

        if (useNMS) {
            project.plugins.apply("io.papermc.paperweight.userdev")
            project.tasks.named("assemble") { it.dependsOn("reobfJar") }
            project.tasks.withType<PublishToMavenLocal> { dependsOn("reobfJar") }

            project.dependencies.apply {
                val paperweightDevelopmentBundle =
                    project.configurations.getByName("paperweightDevelopmentBundle").dependencies
                dependencyStr.append(":$mcVersion")
                paperweightDevelopmentBundle.add(project.dependencies.create(dependencyStr.toString()))
            }

            return
        }

        dependencyStr.append(':')
        dependencyStr.append(if (useTentacles) "tentacles" else "purpur")
        dependencyStr.append(':')
        dependencyStr.append(mcVersion)

        project.dependencies.add("compileOnly", dependencyStr.toString())
    }

    private fun addMinixDependencies(project: Project) {
        if (!addMinixDependency) return

        project.repositories.maven("$RACCI_REPO/releases")

        try {
            val clazz = project.extensions.getByName("libs")::class
            val inst = clazz.declaredMemberProperties.first { it.name == "vaccForVersionAccessors" }.let {
                it.isAccessible = true
                it.call(project.extensions.getByName("libs")) as VersionFactory
            }
            val version = inst::class.declaredFunctions.first { it.name == "getMinix" }.call(inst) as Provider<String>
            project.dependencies.add("compileOnly", "dev.racci:Minix:${version.get()}")
        } catch (e: Exception) {
            println("Failed to add Minix dependency")
        }
    }

    private fun getRealRoot(project: Project): Project {
        var root = project.rootProject
        var attempts = -1
        while (root.project != root || attempts++ < 5) {
            root = root.project
        }

        return root
    }

    private inline fun <reified T> findHighestExtension(
        project: Project,
        extension: String
    ): T? {
        val roots = mutableListOf(project)

        var attempts = -1
        while (roots.last().parent != null || attempts++ < 5) {
            roots.add(roots.last().parent!!)
        }

        roots.reverse()

        for (root in roots) {
            val ext = root.extensions.findByName(extension)
            if (ext == null || ext !is T) continue

            return ext
        }

        return null
    }

    private companion object {
        const val TENTACLES_MODULE = "dev.racci.tentacles"
        const val OLD_PURPUR_MODULE = "net.pl3x.purpur"
        const val NEW_PURPUR_MODULE = "org.purpurmc.purpur"

        const val RACCI_REPO = "https://repo.racci.dev"
        const val PURPUR_REPO = "https://repo.purpurmc.org/snapshots"
    }

    enum class MinecraftProjectType { BUKKIT, BUNGEECORD }
}
