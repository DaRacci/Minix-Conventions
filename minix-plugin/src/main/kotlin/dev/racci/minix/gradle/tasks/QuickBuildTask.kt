package dev.racci.minix.gradle.tasks

import dev.racci.minix.gradle.ex.nullableTargetTask
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.work.DisableCachingByDefault
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import javax.inject.Inject

// TODO: This shit just straight up doesn't work
@DisableCachingByDefault(because = "Not worth caching")
public open class QuickBuildTask @Inject constructor(
    target: KotlinTarget
) : DefaultTask() {
    public companion object {
        public const val TASK_NAME: String = "quickBuild"
    }

    init {
        enabled = false
        group = "minix"
        description = "Builds the target [${target.name}] with minimal tasks (No testing or documentation, etc.)"

        listOfNotNull<TaskProvider<Task>>(
            target.nullableTargetTask("compileKotlin"),
            target.nullableTargetTask("processResources"),
            target.nullableTargetTask("jar"),
            target.nullableTargetTask("shadowJar"),
            target.nullableTargetTask("reobfJar")
        ).takeIf(List<*>::isEmpty)?.let(List<*>::toTypedArray)?.let(::dependsOn)
    }
}
