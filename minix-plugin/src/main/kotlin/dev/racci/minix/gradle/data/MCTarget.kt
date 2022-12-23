package dev.racci.minix.gradle.data

import dev.racci.minix.gradle.Constants
import dev.racci.minix.gradle.ex.project
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
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
    internal fun configure() {
        if (applyNMS) {
            throw UnsupportedOperationException("NMS is not supported yet.")
        }

        if (applyDefaults) {
            val dependencies = platform.getDefaultDependencies(version)
            if (dependencies.isEmpty()) return

            when (obj) {
                is Project -> {
                    obj.repositories.maven(platform.defaultRepository)
                    dependencies.forEach { dep -> obj.dependencies.add(COMPILE_ONLY, dep) }
                }
                is KotlinSourceSet -> {
                    obj.project().repositories.maven(platform.defaultRepository)
                    obj.dependencies { dependencies.forEach(::compileOnly) }
                }
                else -> throw IllegalArgumentException("Unknown target type: ${obj::class.simpleName}")
            }
        }

        if (applyMinix) {
            val minix = platform.getMinixDependencies()
            when (obj) {
                is Project -> {
                    obj.repositories.minixRepo()
                    obj.dependencies.add(COMPILE_ONLY, minix)
                }

                is KotlinSourceSet -> {
                    obj.project().repositories.minixRepo()
                    obj.dependencies { compileOnly(minix) }
                }
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

    public enum class Platform(public val defaultRepository: String) {
        PAPER("https://papermc.io/repository/maven-snapshots/") {
            override fun getDefaultDependencies(version: String?) = listOf("io.papermc.paper:paper-api:${getFullVersion(version) ?: Constants.LATEST_MC_VERSION}")
            override fun getMinixDependencies() = listOf("dev.racci.minix:minix-paper:${Constants.Dependencies.MINIX_VERSION}")
        },
        TENTACLES("https://repo.racci.dev/snapshots/") {
            override fun getDefaultDependencies(version: String?) = listOf("dev.racci.tentacles:tentacles-api:${getFullVersion(version) ?: Constants.LATEST_MC_VERSION}")
            override fun getMinixDependencies() = PAPER.getMinixDependencies()
        },
        VELOCITY("https://repo.velocitypowered.com/snapshots/") {
            override fun getDefaultDependencies(version: String?): Collection<String> {
                TODO("Not yet implemented")
            }
        };

        internal abstract fun getDefaultDependencies(version: String?): Collection<String>

        internal open fun getMinixDependencies(): Collection<String> {
            throw UnsupportedOperationException("Minix is not supported for platform: ${this.name}")
        }

        protected fun getFullVersion(version: String?): String? {
            if (version == null) return null

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
