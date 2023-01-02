package dev.racci.minix.gradle.extensions

import dev.racci.minix.gradle.data.MCTarget
import dev.racci.minix.gradle.ex.highestOrderExtension
import org.gradle.api.Project
import org.gradle.api.tasks.Input

public class MinixMinecraftExtension(override val rootProject: Project) : ExtensionBase() {
    @[Input PublishedApi]
    internal val mcTargets: MutableSet<MCTarget> = mutableSetOf()

    @JvmName("withMCTargetReceiver")
    public fun Project.withMCTarget(
        platform: MCTarget.Platform,
        version: String? = null,
        applyDefaultDependencies: Boolean = true,
        applyMinix: Boolean = true,
        applyNMS: Boolean = false
    ) {
        highestOrderExtension().minecraft.mcTargets.add(MCTarget(this, platform, applyDefaultDependencies, applyMinix, applyNMS, version))
    }

    public fun withMCTarget(
        project: Project,
        platform: MCTarget.Platform,
        version: String? = null,
        applyDefaultDependencies: Boolean = true,
        applyMinix: Boolean = true,
        applyNMS: Boolean = false
    ) { project.withMCTarget(platform, version, applyDefaultDependencies, applyMinix, applyNMS) }

    override fun configure(project: Project) = with(project) {
        mcTargets.forEach(MCTarget::configure)
    }

//    private fun applyNMS(group: String) {
//        project.pluginManager.apply(PaperweightUser::class)
//
//        project.tasks.apply {
//            named("assemble") { dependsOn("reobfJar") }
//            withType<PublishToMavenLocal> { dependsOn("reobfJar") }
//        }
//
//        project.dependencies {
//            this.paperweightDevBundle(group, mcVersion)
//        }
//    }
//
//    private fun applyAPI(group: String) {
//        // We already have API through UserDev
//        if (this.useNMS) return
//
//        val dependency = buildString {
//            append(group)
//            append(":")
//
//            if (useTentacles) {
//                append("tentacles")
//            } else append("purpur")
//
//            append(":")
//            append(mcVersion)
//        }
//
//        project.dependencies.add("compileOnly", dependency)
//    }
//
//    private fun addMinixDependencies() {
//        if (!addMinixDependency) return
//
//        project.repositories.maven("$RACCI_REPO/releases")
//
//        try {
//            val clazz = project.extensions.getByName("libs")::class
//            val inst = clazz.declaredMemberProperties.first { it.name == "vaccForVersionAccessors" }.let {
//                it.isAccessible = true
//                it.call(project.extensions.getByName("libs")) as VersionFactory
//            }
//            val version = inst::class.declaredFunctions.first { it.name == "getMinix" }.call(inst) as Provider<String>
//            project.dependencies.add("compileOnly", "dev.racci:Minix:${version.get()}")
//        } catch (e: Exception) {
//            println("Failed to add Minix dependency")
//        }
//    }
//
//    private fun configurePluginYML() {
//        val lib: Configuration = project.configurations.maybeCreate("lib")
//        project.extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
//            project.configurations.getByName(compileClasspathConfigurationName).extendsFrom(lib)
//            project.configurations.getByName(runtimeClasspathConfigurationName).extendsFrom(lib)
//            project.configurations.getByName(apiElementsConfigurationName).extendsFrom(lib)
//        }
//
//        when (projectType) {
//            MinecraftProjectType.BUKKIT -> {
//                project.afterEvaluate {
//                    val ext = findHighestExtension<BukkitPluginDescription>("bukkit") ?: return@afterEvaluate
//                    val mappedDeps = lib.dependencies.map { "${it.group}:${it.name}:${it.version}" }
//
//                    if (ext.libraries == null) ext.libraries = emptyList()
//                    ext.libraries = ext.libraries!! + mappedDeps
//                }
//
//                BukkitPlugin::class
//            }
//
//            MinecraftProjectType.BUNGEECORD -> BungeePlugin::class
//        }.apply(project.pluginManager::apply)
//    }
//
//    private fun getRealRoot(): Project {
//        var root = project.rootProject
//        var attempts = -1
//        while (root.project != root || attempts++ < 5) {
//            root = root.project
//        }
//
//        return root
//    }
//
//    private inline fun <reified T> findHighestExtension(extension: String): T? {
//        val roots = mutableListOf(project)
//
//        var attempts = -1
//        while (roots.last().parent != null || attempts++ < 5) {
//            roots.add(roots.last().parent!!)
//        }
//
//        roots.reverse()
//
//        for (root in roots) {
//            val ext = root.extensions.findByName(extension)
//            if (ext == null || ext !is T) continue
//
//            return ext
//        }
//
//        return null
//    }
//
//    private companion object {
//        const val TENTACLES_MODULE = "dev.racci.tentacles"
//        const val OLD_PURPUR_MODULE = "net.pl3x.purpur"
//        const val NEW_PURPUR_MODULE = "org.purpurmc.purpur"
//
//        const val PURPUR_REPO = "https://repo.purpurmc.org/snapshots"
//
//        // TODO -> Dynamic versions.
//        const val PLUGIN_YML = "net.minecrell:plugin-yml:0.5.2"
//    }
}
