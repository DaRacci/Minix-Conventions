package dev.racci.minix.gradle.ex // ktlint-disable filename

import dev.racci.minix.gradle.extensions.MinixBaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

/** Gets a deep recursive scan of subprojects. */
public fun Project.recursiveSubprojects(
    includeRoot: Boolean = false,
    depth: Int = 0
): Sequence<Project> = sequence {
    if (!includeRoot && depth != 0) yield(this@recursiveSubprojects)
    subprojects.forEach { innerSub -> yieldAll(innerSub.recursiveSubprojects(includeRoot, depth.inc())) }
}

/**
 * If this project is already evaluated, calls the function in place,
 * otherwise it will be called when the project is evaluated.
 *
 * @param fn The function to call.
 * @param T The return type of the function.
 * @receiver The project to call the function on.
 */
public fun <T> Project.whenEvaluated(fn: Project.() -> T) {
    if (state.executed) {
        fn()
        return
    }

    afterEvaluate { fn() }
}

@PublishedApi
internal fun Project.highestOrderExtension(): MinixBaseExtension {
    var current: Project? = project
    var extension: MinixBaseExtension? = null
    while (current != null) {
        current.extensions.findByType<MinixBaseExtension>()?.also { ext ->
            extension = ext
        }
        current = current.parent
    }

    return extension ?: throw IllegalStateException("No MinixBaseExtension found!")
}
