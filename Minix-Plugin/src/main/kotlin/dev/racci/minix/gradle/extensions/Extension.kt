package dev.racci.minix.gradle.extensions

import org.gradle.api.Project

public interface Extension {
    public val project: Project

    public fun apply()
}
