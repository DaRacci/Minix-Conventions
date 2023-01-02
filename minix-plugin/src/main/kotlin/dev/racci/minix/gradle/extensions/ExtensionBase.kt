package dev.racci.minix.gradle.extensions

import org.gradle.api.Project

public sealed class ExtensionBase {
    public abstract val rootProject: Project

    internal abstract fun configure(project: Project)
}
