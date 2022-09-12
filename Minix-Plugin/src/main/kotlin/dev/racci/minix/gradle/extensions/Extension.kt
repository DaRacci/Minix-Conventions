package dev.racci.minix.gradle.extensions

import org.gradle.api.Project

interface Extension {
    val project: Project

    fun apply()
}
