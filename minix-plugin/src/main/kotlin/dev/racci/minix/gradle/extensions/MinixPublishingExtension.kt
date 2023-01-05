package dev.racci.minix.gradle.extensions

import dev.racci.minix.gradle.ex.recursiveSubprojects
import org.gradle.api.GradleException
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.container
import org.gradle.kotlin.dsl.credentials
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.maybeCreate
import org.gradle.kotlin.dsl.named
import org.slf4j.Logger

public class MinixPublishingExtension(override val rootProject: Project) :
    ExtensionBase(),
    NamedDomainObjectContainer<MinixPublishingExtension.PublicationSpec> by rootProject.container({
        PublicationSpec(rootProject, it)
    }) {

    @get:Input
    @get:Optional
    public var runNumber: String? = System.getenv("GITHUB_RUN_NUMBER")

    @get:Input
    public var configureMavenPublish: Boolean = true

    override fun configure(project: Project) = forEach { spec ->
        project.logger.prInfo("Configuring publication for `${spec.name}`.")

        with(spec.relatedProject.get()) {
            afterEvaluate {
                project.logger.prInfo("Configuring publication after evaluation for `$name`.")

                if (spec.appendRunNumberOrSnapshot) {
                    project.logger.prInfo("Adding run number or marking as snapshot for `$name`.")

                    project.version = spec.version.map { version ->
                        val runNumber = runNumber
                        if (runNumber == null) {
                            version.copy(snapshotType = "SNAPSHOT")
                        } else version.copy(snapshotType = runNumber)
                    }.get().toString()
                }

                plugins.withId("java") {
                    project.logger.prInfo("Configuring java for $name.")
                    extensions.getByName<JavaPluginExtension>("java").withSourcesJar()
                }

                if (!configureMavenPublish) {
                    return@afterEvaluate project.logger.prInfo("Skipping maven publish for $name.")
                } else project.logger.prInfo("Configuring maven publish for $name.")

                apply<MavenPublishPlugin>()

                extensions.configure<PublishingExtension> {
                    spec.repository(repositories)

                    publications {
                        maybeCreate<MavenPublication>(spec.publicationName)
                        named<MavenPublication>(spec.publicationName) {
                            version = project.version.toString()
                            from(components[spec.componentName])
                        }
                    }
                }
            }
        }
    }

    public class PublicationSpec(
        project: Project,
        private val name: String
    ) : Named {

        @Input
        @Optional
        public var componentName: String =
            runCatching { project.components["kotlin"] }.getOrElse { project.components["java"] }.name

        @Input
        private val rawVersion: String = project.version.toString()
        public val version: Provider<Version> = project.provider { Version.parse(rawVersion) }

        @Input
        @Optional
        public var appendRunNumberOrSnapshot: Boolean = project.properties["minix.appendRunNumberOrSnapshot"].toString().toBoolean()

        @Input
        @Optional
        public var publicationName: String = "maven"

        /**
         * This should be changed if you aren't me since well only I have
         * publishing credentials.
         */
        @Input
        public var repository: (RepositoryHandler) -> Unit = { handler ->
            val preRelease = version.get().isPreRelease
            handler.maven("https://repo.racci.dev/${if (preRelease) "snapshots" else "releases"}") {
                name = "RacciRepo"
                credentials(PasswordCredentials::class)
            }
        }

        @Input
        @Optional
        public val extraConfigure: (MavenPublication) -> Unit = {}

        internal val relatedProject: Provider<Project> = project.provider {
            project.recursiveSubprojects(true).firstOrNull { it.name.equals(name, true) }
                ?: throw GradleException("Could not find subproject with name `$name` to configure publishing for.")
        }

        override fun getName(): String = name

        public data class Version(
            public val major: Int,
            public val minor: Int,
            public val patch: Int?,
            public val snapshotType: String? = null,
            public val snapshotRevision: String? = null
        ) {
            public val isPreRelease: Boolean = snapshotType != null

            override fun toString(): String = buildString {
                append("$major.$minor")
                if (patch != null) append(".$patch")
                if (!isPreRelease) return@buildString
                append("-$snapshotType")
                if (snapshotRevision != null) append(".$snapshotRevision")
            }

            public companion object {
                private val SEMVER_REGEX =
                    Regex("^(?<major>\\d+)\\.(?<minor>\\d+)(?:\\.(?<patch>\\d+))?(?:-(?<release>\\w\\d+)(\\.(?<revision>\\w+))?)?$")

                public fun parse(version: String): Version {
                    val match = SEMVER_REGEX.matchEntire(version) ?: throw GradleException(
                        """
                        |Invalid version string: $version
                        |Expected format: <major>.<minor>.<patch>-<release>.<revision>
                        |Example: 1.0.0-SNAPSHOT.1
                        """.trimIndent()
                    )

                    return Version(
                        major = match.groups["major"]!!.value.toInt(),
                        minor = match.groups["minor"]!!.value.toInt(),
                        patch = match.groups["patch"]?.value?.toInt(),
                        snapshotType = match.groups["release"]?.value,
                        snapshotRevision = match.groups["revision"]?.value
                    )
                }
            }
        }
    }

    private companion object {
        fun Logger.prInfo(message: String) = info(":publishingExtension $message")
    }
}
