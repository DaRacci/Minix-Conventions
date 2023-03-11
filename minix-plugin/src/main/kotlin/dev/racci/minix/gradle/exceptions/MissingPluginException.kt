package dev.racci.minix.gradle.exceptions

public class MissingPluginException @JvmOverloads constructor(
    pluginId: String,
    err: Throwable? = null
) : MinixGradleException("Missing required plugin: $pluginId", err)
