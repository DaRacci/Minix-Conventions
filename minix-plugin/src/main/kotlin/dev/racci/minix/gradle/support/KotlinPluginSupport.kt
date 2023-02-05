package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.Constants
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

public sealed class KotlinPluginSupport<T : KotlinProjectExtension>(
    pluginId: String,
) : PluginSupport(pluginId) {

    protected val Project.kotlin: T
        get() = kotlinExtension as T

    protected fun Project.kotlin(f: T.() -> Unit): Unit = kotlin.f()

    protected fun configureBaseExtension(ext: KotlinProjectExtension): Unit = with(ext) {
        explicitApi() // TODO: Check for known error causers and change to warning (e.g. koin)
        jvmToolchain(Constants.JDK_VERSION)
        coreLibrariesVersion = KotlinVersion.CURRENT.toString()
        sourceSets.all {
            languageSettings.apiVersion = KotlinVersion.CURRENT.let { ver -> "${ver.major}.${ver.minor}" }
            languageSettings.languageVersion = languageSettings.apiVersion
        }
    }
}
