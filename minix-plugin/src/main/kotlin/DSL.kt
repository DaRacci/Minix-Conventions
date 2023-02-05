import dev.racci.minix.gradle.data.TargetTaskProvider
import org.gradle.api.Task
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.TaskContainerScope
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

public typealias TargetTaskContainerScope = TaskContainerScope

public typealias TargetTask<T> = Provider<T>

public val KotlinTarget.tasks: TargetTaskProvider
    get() = TargetTaskProvider(this)

public fun KotlinTarget.tasks(scope: TargetTaskContainerScope.() -> Unit) {
    TargetTaskContainerScope.of(tasks).scope()
}

// Known tasks for Kotlin targets
// FIXME: This is a hacky way to get the tasks, but it works for now
// TODO: How to have this generated with kotlin dsl?
public operator fun <T : Task> TargetTask<T>.invoke(f: T.() -> Unit): T {
    return get().also(f::invoke)
}
