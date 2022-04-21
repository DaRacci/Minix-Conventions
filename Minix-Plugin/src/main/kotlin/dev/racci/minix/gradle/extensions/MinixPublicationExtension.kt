package dev.racci.minix.gradle.extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.register
import org.jetbrains.dokka.gradle.DokkaTask

class MinixPublicationExtension(private val project: Project) : Extension {

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
    var preRelease: Boolean = run {
        val preReleaseRegex = Regex("(?<![0-9])([a-zA-Z])(?![a-zA-Z])")
        preReleaseRegex.containsMatchIn(project.version.toString())
    }

    @Input
    var repository: RepositoryHandler.() -> Unit =
        { // This should be changed if you aren't me since well only i have publishing credentials.
            maven("https://repo.racci.dev/${if (preRelease) "snapshots" else "releases"}") {
                name = "RacciRepo"
                credentials(PasswordCredentials::class)
            }
        }

    override fun apply() {
        project.run {
            // beforeEvaluate {
            //     val lib = configurations.create("lib")
            //     plugins.withType<JavaPlugin> {
            //         extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
            //             configurations.getByName(compileClasspathConfigurationName).extendsFrom(lib)
            //             configurations.getByName(runtimeClasspathConfigurationName).extendsFrom(lib)
            //         }
            //     }
            // }

            pluginManager.apply(MavenPublishPlugin::class)

            if (addRunNumber != "false" &&
                runNumber != null
            ) version = "$version${runNumberDelimiter ?: '.'}$runNumber"

            extensions.configure<JavaPluginExtension>("java") {
                withSourcesJar()
            }

            if (documentationDir != null) {
                tasks.getByName<DokkaTask>("dokkaHtml") {
                    outputDirectory.set(file(documentationDir!!)) // This is for github pages.
                }
            }

            extensions.configure<PublishingExtension>("publishing") {
                repositories.repository()

                publications {
                    register("maven", MavenPublication::class) {
                        from(components[publishComponentName ?: "java"])
                    }
                }
            }
        }
    }
}
