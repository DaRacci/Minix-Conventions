package dev.racci.minix.gradle.extensions

import dev.racci.minix.gradle.Constants
import io.papermc.paperweight.tasks.RemapJar
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.Input
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

class MinixPublicationExtension(override val project: Project) : Extension {

    @Input
    var runNumber: String? = System.getenv("GITHUB_RUN_NUMBER")

    @Input
    var runNumberDelimiter: String? = project.findProperty("minix.runNumberDelimiter") as? String

    @Input
    var addRunNumber: String? = project.findProperty("minix.addRunNumber") as? String

    @Input
    var publishComponentName: String? = project.findProperty("minix.publishComponentName") as? String

    @Input
    var documentationDir: String? = project.findProperty("minix.buildDir") as? String

    @Input
    var configureMavenPublish: Boolean = true

    @Input
    var preRelease: Boolean = run {
        val preReleaseRegex = Regex("(?<![0-9])([a-zA-Z])(?![a-zA-Z])")
        preReleaseRegex.containsMatchIn(project.version.toString())
    }

    @Input
    var repository: (Project, RepositoryHandler) -> Unit =
        { project, handler -> // This should be changed if you aren't me since well only I have publishing credentials.
            val preRelease = this.preRelease || project.version.toString().endsWith("-SNAPSHOT", true)
            handler.maven("https://repo.racci.dev/${if (preRelease) "snapshots" else "releases"}") {
                name = "RacciRepo"
                credentials(PasswordCredentials::class)
            }
        }

    override fun apply() {
        with(project) {
            applyPlugins(this)

            afterEvaluate {
                if (addRunNumber != "false" && runNumber != null) {
                    version = "$version${runNumberDelimiter ?: "."}$runNumber"
                }
            }

            configureExtensions(this)
            configureTasks(this)
        }
    }

    private fun applyPlugins(project: Project) {
        project.pluginManager.apply(MavenPublishPlugin::class)
        project.pluginManager.apply(DokkaPlugin::class)
    }

    private fun configureTasks(project: Project) {
        if (documentationDir == null) return

        project.tasks.withType<DokkaTask>().whenTaskAdded { task ->
            task.outputDirectory.set(project.file(documentationDir!!))
            task.dokkaSourceSets.configureEach { builder ->
                builder.includeNonPublic.set(false)
                builder.skipEmptyPackages.set(true)
                builder.displayName.set(project.name.split("-").getOrElse(1) { project.name })
                builder.platform.set(Platform.jvm)
                builder.jdkVersion.set(Constants.JDK_VERSION)
                builder.sourceLink { link -> link.remoteLineSuffix.set("#L") }
            }
        }
    }

    private fun configureExtensions(project: Project) {
        project.extensions.configure<JavaPluginExtension>("java") { extension ->
            extension.withSourcesJar()
        }

        project.extensions.configure<PublishingExtension>("publishing") { extension ->
            this.repository(project, project.repositories)
            extension.publications.register("maven", MavenPublication::class) { publication ->
                publication.artifactId = project.rootProject.name
                publication.groupId = project.rootProject.group.toString()

                if (!configureMavenPublish) return@register

                if (this.publishComponentName != null) {
                    return@register publication.from(project.components[this.publishComponentName!!])
                }

                project.tasks.findByName("reobfJar")?.configure<RemapJar> {
                    publication.artifact(this.outputJar) { artifact ->
                        artifact.builtBy(this)
                        artifact.classifier = null
                    }
                }

                project.tasks.findByName("kotlinSourcesJar")?.configure<Jar> {
                    publication.artifact(this) { artifact ->
                        artifact.builtBy(this)
                        artifact.classifier = "sources"
                    }
                }

                if (publication.artifacts.isEmpty()) {
                    publication.from(project.components["kotlin"])
                }
            }
        }
    }
}
