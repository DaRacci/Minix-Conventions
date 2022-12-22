package dev.racci.minix.gradle.exceptions

import org.gradle.api.GradleException

public class MissingPluginException : GradleException {
    public constructor(message: String) : super(message)
    public constructor(message: String, cause: Throwable) : super(message, cause)
}
