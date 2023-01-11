package dev.racci.minix.gradle // ktlint-disable filename

import dev.racci.minix.gradle.exceptions.MissingPluginException
import org.gradle.api.GradleException
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

internal fun warnForMissingUsedPlugin(
    pluginId: String,
    classUsage: () -> Unit
) = try {
    classUsage()
} catch (err: ClassCastException) {
    throw MissingPluginException(
        """
        Plugin `$pluginId` is used to configure subprojects but is not present in root classpath,
        Please add it to the root build.gradle.kts like so:
        plugins {
            id("$pluginId") version "x.x.x" apply false
        }
        """.trimIndent(),
        err
    )
} catch (@Suppress("TooGenericExceptionCaught") err: Throwable) {
    throw GradleException("There was an error while configuring support for $pluginId", err)
}

@PublishedApi
internal fun isTestEnvironment(): Boolean = System.getProperty("MINIX_TESTING_ENV") == "true"
