package dev.racci.minix.gradle.exceptions

import org.gradle.api.GradleException

public class MissingPluginException internal constructor(
    pluginId: String,
    err: Throwable? = null,
) : GradleException("Missing required plugin: $pluginId", err)
