package dev.racci.minix.gradle.extensions

import dev.racci.minix.gradle.Constants
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

class MinixStandardExtension(override val project: Project) : Extension {
    private val actualEnableKotlin: Boolean get() = enableKotlin || enableKtlint || enableKotlinSerialization

    @Input
    var enableKotlin: Boolean = true

    @Input
    var enableKtlint: Boolean = true

    @Input
    var enableKotlinSerialization: Boolean = true

    override fun apply() {
        addRepositories()
        addDependencies()
        applyPlugins()

        configureKotlin()
        configureKtLint()
    }

    private fun addRepositories() {
        project.repositories {
            mavenCentral()
            maven(Constants.RACCI_REPO + "releases")
            maven(Constants.RACCI_REPO + "snapshots")
        }
    }

    private fun addDependencies() {
        val implementation = project.configurations.getByName("implementation")
        val compileClasspath = project.configurations.getByName("compileClasspath")

        project.dependencies {
            compileClasspath(platform(kotlin("bom:${Constants.KOTLIN_VERSION}")))
            compileClasspath(kotlin("stdlib-jdk8")) // Don't allow consumers to shadow jar this by default.
        }
    }

    private fun applyPlugins() = project.pluginManager.apply {
        if (actualEnableKotlin) apply(KotlinPlatformJvmPlugin::class)
        if (enableKtlint) apply(KtlintPlugin::class)
        if (enableKotlinSerialization) apply(SerializationGradleSubplugin::class)
    }

    private fun configureKotlin() {
        if (!actualEnableKotlin) return

        project.kotlinExtension.jvmToolchain(Constants.JDK_VERSION)

        project.tasks.withType<KotlinCompile> {
            kotlinOptions {
                languageVersion = Constants.KOTLIN_VERSION.substringBeforeLast(".")
                apiVersion = languageVersion
                jvmTarget = Constants.JDK_VERSION.toString()
                useK2 = false // TODO -> Enable when stable enough
            }
        }
    }

    private fun configureKtLint() {
        if (!enableKtlint) return

        project.extensions.configure<KtlintExtension> {
            version.set("0.45.2")
            enableExperimentalRules.set(false)
            reporters {
                it.reporter(ReporterType.PLAIN)
                it.reporter(ReporterType.HTML)
                it.reporter(ReporterType.CHECKSTYLE)
            }
        }
    }
}
