package dev.racci.minix.gradle.extensions

import org.gradle.api.Project

// TODO: Add dynamic extensions to subprojects which are scoped in to only configure the subproject.
public sealed class ExtensionBase {
    public abstract val rootProject: Project

    internal abstract fun configure(project: Project)
}
