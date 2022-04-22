package dev.racci.minix.gradle.extensions

import net.minecrell.pluginyml.bukkit.BukkitPlugin
import org.gradle.api.Project
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory.VersionFactory
import org.gradle.api.provider.Provider
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.apply
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
    var mcVersion: String = "1.18.2-R0.1-SNAPSHOT"

    @Input
    var useNMS: Boolean = false

    @Input
    var addMinixDependency: Boolean = true

    override fun apply() {
        project.run {
            val dependencyStr = StringBuilder()
            val (major, minor) = mcVersion.split('.').take(2).map(String::toInt)

            if (useTentacles) {
                dependencyStr.append("dev.racci.tentacles")
                repositories.maven("https://repo.racci.dev/snapshots")
            } else {
                repositories.maven("https://repo.purpurmc.org/snapshots")

                if (major == 1 && minor < 18) {
                    dependencyStr.append("net.pl3x.purpur")
                } else {
                    dependencyStr.append("org.purpurmc.purpur")
                }
            }

            if (addMinixDependency) {
                repositories.maven("https://repo.racci.dev/releases")
                try {
                    val clazz = extensions.getByName("libs")::class
                    val inst = clazz.declaredMemberProperties.first { it.name == "vaccForVersionAccessors" }.let {
                        it.isAccessible = true
                        it.call(extensions.getByName("libs")) as VersionFactory
                    }
                    val version =
                        inst::class.declaredFunctions.first { it.name == "getMinix" }.call(inst) as Provider<String>
                    dependencies.add("compileOnly", "dev.racci:Minix:${version.get()}")
                } catch (e: Exception) {
                    println("Failed to add Minix dependency")
                }
            }

            if (useNMS) {
                plugins.apply("io.papermc.paperweight.userdev")

                tasks.named("assemble") { dependsOn("reobfJar") }

                tasks.withType<PublishToMavenLocal> { dependsOn("reobfJar") }

                dependencies.apply {
                    val paperweightDevelopmentBundle =
                        configurations.getByName("paperweightDevelopmentBundle").dependencies

                    dependencyStr.append(":$mcVersion")

                    paperweightDevelopmentBundle.add(dependencies.create(dependencyStr.toString()))
                }
            } else {
                if (useTentacles) {
                    dependencyStr.append(":tentacles-api")
                } else dependencyStr.append(":purpur-api")
                dependencyStr.append(":$mcVersion")

                dependencies.add("compileOnly", dependencyStr.toString())
            }

            pluginManager.apply(BukkitPlugin::class)

            tasks.named<ProcessResources>("processResources") {
                filesMatching("plugin.yml") {
                    expand(mutableMapOf("version" to version))
                }
            }
        }
    }
}
