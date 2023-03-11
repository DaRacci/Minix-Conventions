package dev.racci.minix.gradle.exceptions

public class PluginSupportException private constructor(
    pluginId: String,
    err: Throwable? = null
) : MinixGradleException("Failed to configure plugin: $pluginId", err) {

    internal companion object {
        private val supportTypes = arrayOf("Project", "KotlinTarget", "KotlinSourceSet")

        @Throws(PluginSupportException::class)
        fun unsupportedType(supplied: Any?): Nothing = throw PluginSupportException(
            """
                Error while configuring plugin support.
                Unsupported type: ${supplied?.javaClass?.simpleName}!
                Supported types: ${supportTypes.joinToString()}
            """.trimMargin()
        )
    }
}
