package dev.racci.minix.gradle // ktlint-disable filename

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import java.io.File
import kotlin.reflect.KCallable
import kotlin.reflect.jvm.isAccessible

// public fun Project.emptySources() = project.allprojects.sourceSets.none { set -> set.allSource.any { file -> file.extension == "kt" } }

public val isCI: Boolean
    get() = System.getenv("CI") == "true"

public fun <T : KCallable<*>, R> T.access(fn: T.() -> R): R {
    val originalState = this.isAccessible
    this.isAccessible = true
    val value = fn()
    this.isAccessible = originalState
    return value
}

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

public inline fun <reified T> Any.cast(): T {
    return this as T
}

internal fun isTestEnvironment(): Boolean = System.getProperty("MINIX_TESTING_ENV") == "true"
