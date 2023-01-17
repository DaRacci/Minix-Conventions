package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.Constants
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure

public object JavaBasePluginSupport : PluginSupport(
    "java-base",
    { JavaBasePlugin::class }
) {
    override fun configureSub(project: Project): Unit = project.extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(Constants.JDK_VERSION))
    }
}
