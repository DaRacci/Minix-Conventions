package dev.racci.minix.gradle.extensions

import dev.racci.minix.gradle.Constants.RACCI_REPO
import io.papermc.paperweight.userdev.PaperweightUser
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
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType
import paperweightDevBundle
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

public class MinixMinecraftExtension(override val project: Project) : Extension {

    @Input
    public var useTentacles: Boolean = false

    @Input
    public var mcVersion: String = "1.19.2-R0.1-SNAPSHOT"

    @Input
    public var useNMS: Boolean = false

    @Input
    public var addMinixDependency: Boolean = true

    @Input
    public var projectType: MinecraftProjectType = MinecraftProjectType.BUKKIT

    override fun apply() {
        addMinecraftDependency()
        addMinixDependencies()
        configurePluginYML()
    }

    private fun addMinecraftDependency() {
        val group = this.getAPIGroup()
        this.addRepositories(group)
        this.applyNMS(group)
        this.applyAPI(group)
    }

    private fun getAPIGroup(): String {
        val (major, minor) = mcVersion.split('.').take(2).map(String::toInt)

        return when {
            useTentacles -> TENTACLES_MODULE
            major == 1 && minor < 18 -> OLD_PURPUR_MODULE
            else -> NEW_PURPUR_MODULE
        }
    }

    private fun addRepositories(group: String) {
        project.beforeEvaluate {
            project.buildscript.dependencies.add("classpath", PLUGIN_YML)
        }

        when (group) {
            TENTACLES_MODULE -> project.repositories.maven("$RACCI_REPO/snapshots")
            else -> project.repositories.maven(PURPUR_REPO)
        }
    }

    private fun applyNMS(group: String) {
        project.pluginManager.apply(PaperweightUser::class)

        project.tasks.apply {
            named("assemble") { dependsOn("reobfJar") }
            withType<PublishToMavenLocal> { dependsOn("reobfJar") }
        }

        project.dependencies {
            this.paperweightDevBundle(group, mcVersion)
        }
    }

    private fun applyAPI(group: String) {
        // We already have API through UserDev
        if (this.useNMS) return

        val dependency = buildString {
            append(group)
            append(":")

            if (useTentacles) {
                append("tentacles")
            } else append("purpur")

            append(":")
            append(mcVersion)
        }

        project.dependencies.add("compileOnly", dependency)
    }

    private fun addMinixDependencies() {
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

    private fun configurePluginYML() {
        val lib: Configuration = project.configurations.maybeCreate("lib")
        project.extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
            project.configurations.getByName(compileClasspathConfigurationName).extendsFrom(lib)
            project.configurations.getByName(runtimeClasspathConfigurationName).extendsFrom(lib)
            project.configurations.getByName(apiElementsConfigurationName).extendsFrom(lib)
        }

        when (projectType) {
            MinecraftProjectType.BUKKIT -> {
                project.afterEvaluate {
                    val ext = findHighestExtension<BukkitPluginDescription>("bukkit") ?: return@afterEvaluate
                    val mappedDeps = lib.dependencies.map { "${it.group}:${it.name}:${it.version}" }

                    if (ext.libraries == null) ext.libraries = emptyList()
                    ext.libraries = ext.libraries!! + mappedDeps
                }

                BukkitPlugin::class
            }

            MinecraftProjectType.BUNGEECORD -> BungeePlugin::class
        }.apply(project.pluginManager::apply)
    }

    private fun getRealRoot(): Project {
        var root = project.rootProject
        var attempts = -1
        while (root.project != root || attempts++ < 5) {
            root = root.project
        }

        return root
    }

    private inline fun <reified T> findHighestExtension(extension: String): T? {
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

        const val PURPUR_REPO = "https://repo.purpurmc.org/snapshots"

        // TODO -> Dynamic versions.
        const val PLUGIN_YML = "net.minecrell:plugin-yml:0.5.2"
    }

    public enum class MinecraftProjectType { BUKKIT, BUNGEECORD }
}
