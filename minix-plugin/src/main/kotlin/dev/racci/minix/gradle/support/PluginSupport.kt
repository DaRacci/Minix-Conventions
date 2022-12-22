package dev.racci.minix.gradle.support

import org.gradle.api.Project

internal sealed interface PluginSupport {
    // TODO: KotlinTarget instead of project
    fun configure(target: Project)
}
