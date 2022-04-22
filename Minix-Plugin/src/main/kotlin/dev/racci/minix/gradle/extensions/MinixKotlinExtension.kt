package dev.racci.minix.gradle.extensions

import org.gradle.api.Project
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
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

class MinixKotlinExtension(private val project: Project) : Extension {

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

            extensions.configure<KtlintExtension> {
                version.set("0.45.2")
                enableExperimentalRules.set(false)
                reporters {
                    reporter(ReporterType.PLAIN)
                    reporter(ReporterType.HTML)
                    reporter(ReporterType.CHECKSTYLE)
                }
            }

            extensions.configure<KotlinJvmProjectExtension> {
                jvmToolchain {
                    cast<JavaToolchainSpec>().languageVersion.set(JavaLanguageVersion.of(17))
                }
            }

            tasks.withType<KotlinCompile> {
                kotlinOptions {
                    jvmTarget = "17"
                }
            }

            tasks.withType<BaseKtLintCheckTask> {
                workerMaxHeapSize.set("1024m")
            }
        }
    }
}
