package dev.racci.minix.gradle.extensions

import org.gradle.api.Project

public abstract class ExtensionBase {
    internal abstract fun configure(project: Project)
}
