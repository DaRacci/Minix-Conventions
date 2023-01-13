package dev.racci.minix.gradle.exceptions

import org.gradle.api.GradleException

public sealed class MinixGradleException @JvmOverloads protected constructor(
    message: String,
    cause: Throwable? = null
) : GradleException(message, cause)
