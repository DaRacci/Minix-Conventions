package dev.racci.minix.gradle // ktlint-disable filename

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import java.io.File

// public fun Project.emptySources() = project.allprojects.sourceSets.none { set -> set.allSource.any { file -> file.extension == "kt" } }

internal fun isTestEnvironment(): Boolean = System.getProperty("MINIX_TESTING_ENV") == "true"

public fun SourceDirectorySet.maybeExtend(
    project: Project,
    vararg objects: Any
) {
    objects.map {
        when (it) {
            is File -> it
            is String -> project.file(it)
            else -> error("Unknown type: $it")
        }
    }.filter { it.exists() }.also { srcDirs(it) }
}
