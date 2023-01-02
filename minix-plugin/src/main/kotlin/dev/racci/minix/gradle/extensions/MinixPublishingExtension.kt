package dev.racci.minix.gradle.extensions

import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.container
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.maven

public class MinixPublishingExtension(override val rootProject: Project) :
    ExtensionBase(),
    NamedDomainObjectContainer<MinixPublishingExtension.PublicationSpec> by rootProject.container({
        PublicationSpec(rootProject, it)
    }) {
    public class PublicationSpec(
        project: Project,
        public val name: String
    ) {
        @get:Input
        @get:Optional
        public var componentName: String? = null

        @get:Input
        @get:Optional
        public var rawVersion: String = project.version.toString()

        public var version: Provider<Version> = project.provider { Version.parse(rawVersion) }; internal set

        @get:Input
        @get:Optional
        public var useRunNumberAsPatch: Boolean = project.properties["minix.addRunNumber"].toString().toBoolean()

        /**
         * This should be changed if you aren't me since well only I have
         * publishing credentials.
         */
        @get:Input
        public var repository: (RepositoryHandler) -> Unit = { handler ->
            val preRelease = version.get().isPreRelease
            handler.maven("https://repo.racci.dev/${if (preRelease) "snapshots" else "releases"}") {
                name = "RacciRepo"
                credentials(PasswordCredentials::class)
            }
        }

        public data class Version(
            public val major: Int,
            public val minor: Int,
            public val patch: Int?,
            public val snapshotType: String? = null,
            public val snapshotRevision: String? = null
        ) {
            public val isPreRelease: Boolean = snapshotType != null

            public companion object {
                private val VERSION_REGEX =
                    Regex("^(?<version>(?<major>\\d+)\\.(?<minor>\\d+)\\.?(?<patch>\\d*))-(?<snapshotType>\\w+)\\.?(?<snapshotRevision>\\d*)\$")

                public fun parse(version: String): Version {
                    val match = VERSION_REGEX.matchEntire(version) ?: throw GradleException(
                        """
                        |Invalid version string: $version
                        |Expected format: <major>.<minor>.<patch>-<snapshotType>.<snapshotRevision>
                        |Example: 1.0.0-SNAPSHOT.1
                        """.trimIndent()
                    )

                    return Version(
                        major = match.groups["major"]!!.value.toInt(),
                        minor = match.groups["minor"]!!.value.toInt(),
                        patch = match.groups["patch"]?.value?.toInt(),
                        snapshotType = match.groups["preRelease"]?.value,
                        snapshotRevision = match.groups["build"]?.value
                    )
                }
            }
        }
    }

    @get:Input
    @get:Optional
    public var runNumber: String? = System.getenv("GITHUB_RUN_NUMBER")

    @get:Input
    public var configureMavenPublish: Boolean = true

    override fun configure(project: Project) = with(project) {
        afterEvaluate {
            configureEach {
                if (useRunNumberAsPatch) {
                    version = version.map { version ->
                        val runNumber = runNumber?.toIntOrNull()
                        if (runNumber == null) {
                            version.copy(patch = null, snapshotType = "SNAPSHOT")
                        } else version.copy(patch = runNumber)
                    }
                }
            }
        }
    }

    // private fun applyPlugins(project: Project) {
    //     project.pluginManager.apply(MavenPublishPlugin::class)
    //     project.pluginManager.apply(DokkaPlugin::class)
    // }
    //
    // private fun configureTasks(project: Project) {
    //     if (documentationDir == null) return
    //
    //     project.tasks.withType<DokkaTask>().whenTaskAdded {
    //         outputDirectory.set(project.file(documentationDir))
    //         dokkaSourceSets.configureEach {
    //             includeNonPublic.set(false)
    //             skipEmptyPackages.set(true)
    //             displayName.set(project.name.split("-").getOrElse(1) { project.name })
    //             platform.set(Platform.jvm)
    //             jdkVersion.set(Constants.JDK_VERSION)
    //             sourceLink { remoteLineSuffix.set("#L") }
    //         }
    //     }
    // }
    //
    // private fun configureExtensions(project: Project) {
    //     project.extensions.configure<JavaPluginExtension>("java") { withSourcesJar() }
    //
    //     project.extensions.configure<PublishingExtension>("publishing") {
    //         repository(project, project.repositories)
    //         publications.register("maven", MavenPublication::class) {
    //             artifactId = project.rootProject.name
    //             groupId = project.rootProject.group.toString()
    //
    //             if (!configureMavenPublish) return@register
    //
    //             if (publishComponentName != null) {
    //                 return@register from(project.components[publishComponentName!!])
    //             }
    //
    //             project.tasks.findByName("reobfJar")?.configure<RemapJar> {
    //                 artifact(this.outputJar) {
    //                     builtBy(this)
    //                     classifier = null
    //                 }
    //             }
    //
    //             project.tasks.findByName("kotlinSourcesJar")?.configure<Jar> {
    //                 artifact(this) {
    //                     builtBy(this)
    //                     classifier = "sources"
    //                 }
    //             }
    //
    //             if (artifacts.isEmpty()) {
    //                 from(project.components["kotlin"])
    //             }
    //         }
    //     }
    // }
}
