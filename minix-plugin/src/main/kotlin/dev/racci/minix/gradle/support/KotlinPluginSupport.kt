package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.Constants
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.DefaultKotlinBasePlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.targets
import kotlin.reflect.KClass

public sealed class KotlinPluginSupport<T : KotlinProjectExtension>(
    id: String,
    target: () -> KClass<out DefaultKotlinBasePlugin>
) : PluginSupport(id, target) {

    protected val Project.kotlin: T
        get() = kotlinExtension as T

    protected fun configureBaseExtension(ext: KotlinProjectExtension): Unit = with(ext) {
        explicitApi() // TODO: Check for known error causers and change to warning (e.g. koin)
        jvmToolchain(Constants.JDK_VERSION)
        coreLibrariesVersion = KotlinVersion.CURRENT.toString()
        sourceSets.all {
            languageSettings.apiVersion = KotlinVersion.CURRENT.let { ver -> "${ver.major}.${ver.minor}" }
            languageSettings.languageVersion = languageSettings.apiVersion
        }
    }

    protected open fun addExtraSupport(ext: KotlinProjectExtension): Unit = with(ext) {
        targets.forEach { target -> addPluginSupport(target) }
        sourceSets.forEach { sourceSet -> addPluginSupport(sourceSet) }
    }
}
