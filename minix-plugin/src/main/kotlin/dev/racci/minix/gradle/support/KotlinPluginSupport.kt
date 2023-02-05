package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.Constants
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.kpm.external.ExternalVariantApi
import org.jetbrains.kotlin.gradle.kpm.external.project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

public sealed class KotlinPluginSupport<T : KotlinProjectExtension>(
    pluginId: String,
) : PluginSupport(pluginId) {

    protected val Project.kotlin: T
        get() = kotlinExtension as T

    protected fun Project.kotlin(f: T.() -> Unit): Unit = kotlin.f()

    @OptIn(ExternalVariantApi::class)
    protected fun configureBase(ext: KotlinProjectExtension) {
        ext.project.tasks.withType<KotlinCompile> {
            kotlinOptions.freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
        }

        with(ext) {
            explicitApi() // TODO: Check for known error causers and change to warning (e.g. koin)
            jvmToolchain(Constants.JDK_VERSION)
            coreLibrariesVersion = KotlinVersion.CURRENT.toString()
            sourceSets.all {
                languageSettings.apiVersion = KotlinVersion.CURRENT.let { ver -> "${ver.major}.${ver.minor}" }
                languageSettings.languageVersion = languageSettings.apiVersion
            }
        }
    }
}
