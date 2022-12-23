package dev.racci.minix.gradle

import dev.racci.minix.gradle.extensions.MinixBaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

internal val Project.minix get() = extensions.getByName("minix") as MinixBaseExtension

internal fun Project.applyBase() {
    plugins.apply(MinixGradlePlugin::class)
}

internal fun setupEnvImpl() = System.setProperty("MINIX_TESTING_ENV", "true")

internal fun teardownEnvImpl() = System.clearProperty("MINIX_TESTING_ENV")
