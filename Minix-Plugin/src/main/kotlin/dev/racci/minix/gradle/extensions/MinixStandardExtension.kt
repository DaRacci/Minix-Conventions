package dev.racci.minix.gradle.extensions

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Input
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.utilities.cast
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

class MinixStandardExtension(private val project: Project) : Extension {

    @get:Input
    var enableKtlint: Boolean = true

    @get:Input
    var enableKotlinSerialization: Boolean = false

    override fun apply() {
        project.run {
            plugins.apply {
                apply("java")
                apply(KotlinPlatformJvmPlugin::class)
                if (enableKtlint) apply(KtlintPlugin::class)
                if (enableKotlinSerialization) apply(SerializationGradleSubplugin::class)
            }

            configureExtensions(this)
            configureTasks(this)
        }
    }

    private fun configureExtensions(project: Project) {
        project.extensions.configure<KotlinJvmProjectExtension> {
            jvmToolchain {
                cast<JavaToolchainSpec>().languageVersion.set(JavaLanguageVersion.of(17))
            }
        }

        project.extensions.configure<JavaPluginExtension>("java") { extension ->
            extension.sourceCompatibility = JavaVersion.VERSION_17
            extension.targetCompatibility = JavaVersion.VERSION_17
        }

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

    private fun configureTasks(project: Project) {
        project.tasks.withType<KotlinCompile> {
            kotlinOptions {
                languageVersion = "1.7"
                apiVersion = "1.7"
                jvmTarget = "17"
                useK2 = true
            }
        }
    }
}
