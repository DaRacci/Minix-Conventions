package dev.racci.minix.gradle.data

import dev.racci.minix.gradle.Constants
import dev.racci.minix.gradle.ex.project
import dev.racci.minix.gradle.ex.whenEvaluated
import dev.racci.minix.gradle.exceptions.MissingPluginException
import io.papermc.paperweight.util.constants.DEV_BUNDLE_CONFIG
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.maven
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.utils.COMPILE_ONLY

// TODO: PluginYML support
public data class MCTarget @PublishedApi internal constructor(
    val obj: Any,
    val platform: Platform,
    val applyDefaults: Boolean,
    val applyMinix: Boolean = true,
    val applyNMS: Boolean = false,
    val version: String? = null
) {
    private val logger = (obj as? Project)?.logger ?: (obj as KotlinSourceSet).project().logger

    internal fun configure() {
        // TODO: Move paperweight MPP to separate project so we can get the sourceSets config name.
        if (applyNMS && platform.supportsPaperweight) {
            logger.info("Configuring MCTarget $obj Paperweight for ${platform.name}")
            if (obj !is Project) throw UnsupportedOperationException("NMS is only supported for projects at this time.")

            // Tries to apply the plugin if present in the classpath already.
            runCatching { (obj as Project).apply(plugin = "io.papermc.paperweight.userdev") }
            if (!obj.plugins.hasPlugin("io.papermc.paperweight.userdev")) {
                throw MissingPluginException("io.papermc.paperweight.userdev")
            }

            obj.repositories.maven(platform.paperweightRepository)
            obj.dependencies.add(
                DEV_BUNDLE_CONFIG,
                "${platform.apiGroup}:dev-bundle:${Platform.getFullVersion(version) ?: platform.version}"
            )
        } else if (applyDefaults) {
            logger.info("Configuring MCTarget defaults for ${platform.name}")

            fun RepositoryHandler.addDefaultRepo() = maven(platform.apiRepository) {
                name = platform.name
            }

            when (obj) {
                is Project -> {
                    obj.repositories.addDefaultRepo()
                    obj.whenEvaluated { obj.dependencies.add(COMPILE_ONLY, platform.dependencyString(this@MCTarget.version)) }
                }

                is KotlinSourceSet -> {
                    obj.project().repositories.addDefaultRepo()
                    obj.dependencies { compileOnly(platform.dependencyString(version)) }
                }

                else -> throw IllegalArgumentException("Unknown target type: ${obj::class.simpleName}")
            }
        }

        if (!applyMinix || platform.minixDependency == null) return logger.info("Skipping Minix configuration for ${platform.name}")
        logger.info("Configuring MCTarget Minix for ${platform.name}")

        when (obj) {
            is Project -> {
                obj.repositories.minixRepo()
                obj.whenEvaluated { obj.dependencies.add(COMPILE_ONLY, platform.minixDependency!!) }
            }

            is KotlinSourceSet -> {
                obj.project().repositories.minixRepo()
                obj.dependencies { compileOnly(platform.minixDependency!!) }
            }
        }
    }

    private fun RepositoryHandler.minixRepo() = maven("${Constants.RACCI_REPO}/releases") {
        name = "Minix Repository"
        mavenContent {
            releasesOnly()
            includeGroup("dev.racci.minix")
        }
    }

    public enum class Platform(
        internal val apiGroup: String,
        internal val apiArtifact: String,
        internal val apiVersion: String,
        internal val apiRepository: String,
        internal val supportsPaperweight: Boolean = true,
        internal val minixModule: String? = null
    ) {
        PAPER(
            "io.papermc.paper",
            "paper-api",
            Constants.MC_VERSION,
            minixModule = "paper",
            apiRepository = "https://repo.papermc.io/repository/maven-public/"
        ) {
            override val version: String get() = getFullVersion(apiVersion)!!
        },
        PURPUR(
            "org.purpurmc.purpur", // Only valid for versions at or above 1.18; 1.17 and below use `net.pl3x.purpur`.
            "purpur-api",
            PAPER.apiVersion,
            minixModule = PAPER.minixModule,
            apiRepository = "https://repo.purpurmc.org/snapshots"
        ) {
            override val version: String get() = PAPER.version
        },
        TENTACLES(
            "dev.racci.tentacles",
            "tentacles-api",
            PURPUR.apiVersion,
            minixModule = PURPUR.minixModule,
            apiRepository = "https://repo.racci.dev/snapshots/"
        ) {
            override val version: String get() = PURPUR.version
        },
        VELOCITY(
            "com.velocitypowered",
            "velocity-api",
            "3.1.1", // TODO: Handle in catalog
            PAPER.apiRepository,
            false
        );

        public val minixDependency: String?
            get() = minixModule?.let { "dev.racci.minix:$it:$version" }

        public open val version: String
            get() = apiVersion

        internal open val paperweightRepository: String
            get() = apiRepository

        internal open fun dependencyString(providedVersion: String?): String =
            "$apiGroup:$apiArtifact:${getFullVersion(providedVersion) ?: version}"

        internal companion object {
            fun getFullVersion(version: String?): String? {
                if (version == null) return null
                if (version.endsWith("R0.1-SNAPSHOT")) return version

                val split = version.split('.', limit = 3)
                val major = split[0].toInt()
                val minor = split[1].toInt()
                val patch = split.getOrNull(2)?.toInt()

                return buildString {
                    append(major)
                    append('.')
                    append(minor)
                    if (patch != null) {
                        append('.')
                        append(patch)
                    }
                    append("-R0.1-SNAPSHOT")
                }
            }
        }
    }
}
