package dev.racci.minix.gradle.extensions

import net.minecrell.pluginyml.bukkit.BukkitPlugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

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
                // dependencies.add("compile", "dev.racci:Minix:") // TODO: VERSION
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
