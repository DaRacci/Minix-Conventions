package dev.racci.minix.gradle.ex // ktlint-disable filename

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.racci.minix.gradle.data.MCTarget
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import kotlin.reflect.KClass

public fun <T : Task> KotlinTarget.targetTask(taskName: String, clazz: KClass<T>): TaskProvider<T> = project.tasks.named("${name}${taskName.capitalized()}", clazz)

/**
 * Gets a provider for the task prefixed with the targets name.
 *
 * @param taskName The name of the task to get.
 * @param T The type of the task.
 * @return The task provider.
 */
public inline fun <reified T : Task> KotlinTarget.targetTask(taskName: String): TaskProvider<T> = targetTask(taskName, T::class)

/**
 * Gets a provider for the task prefixed with the targets name.
 *
 * @param commonTask The common task that's name will be used.
 * @param T The type of the task.
 * @return The task provider.
 */
public inline fun <reified T : Task> KotlinTarget.targetTask(commonTask: T): TaskProvider<T> = targetTask(commonTask.name)

/**
 * Gets a provider for the task prefixed with the targets name.
 *
 * @param commonTask The common task that's name will be used.
 * @param T The type of the task.
 * @return The task provider.
 */
public inline fun <reified T : Task> KotlinTarget.targetTask(commonTask: TaskProvider<T>): TaskProvider<T> = targetTask(commonTask.get())

public inline fun <reified T : Task> KotlinTarget.nullableTargetTask(taskName: String): TaskProvider<T>? = runCatching { targetTask<T>(taskName) }.getOrNull()

public fun KotlinJvmTarget.shadowJar(block: ShadowJar.() -> Unit): Unit = targetTask("shadowJar", ShadowJar::class).configure(block)

// Copied from internal kotlin gradle plugin
public fun KotlinTarget.disambiguateName(simpleName: String): String {
    val nonEmptyParts = listOf(targetName, simpleName).mapNotNull { it.takeIf(String::isNotEmpty) }
    return nonEmptyParts.drop(1).joinToString(
        separator = "",
        prefix = nonEmptyParts.firstOrNull().orEmpty(),
        transform = String::capitalize
    )
}

public fun KotlinSourceSet.withMCTarget(
    platform: MCTarget.Platform,
    version: String? = null,
    applyDefaultDependencies: Boolean = true,
    applyMinix: Boolean = true,
    applyNMS: Boolean = false
) {
    project().highestOrderExtension().minecraft.mcTargets.add(MCTarget(this, platform, applyDefaultDependencies, applyMinix, applyNMS, version))
}

@PublishedApi
internal fun KotlinSourceSet.project(): Project {
    return this::class.java.getDeclaredField("project").apply { isAccessible = true }.get(this) as Project
}
