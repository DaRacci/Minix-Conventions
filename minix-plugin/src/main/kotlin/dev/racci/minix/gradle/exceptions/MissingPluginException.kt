package dev.racci.minix.gradle.exceptions

import org.gradle.api.GradleException

public class MissingPluginException(pluginId: String) : GradleException("Missing required plugin: $pluginId")
