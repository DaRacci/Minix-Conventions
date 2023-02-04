package dev.racci.minix.gradle.tasks

import org.gradle.api.DefaultTask
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

public abstract class TargetTask<T : KotlinTarget>(
    public val target: T
) : DefaultTask()
