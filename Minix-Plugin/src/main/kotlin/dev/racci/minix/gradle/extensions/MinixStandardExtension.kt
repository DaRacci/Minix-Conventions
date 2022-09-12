package dev.racci.minix.gradle.extensions

import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainSpec
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.utilities.cast
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

class MinixStandardExtension(override val project: Project) : Extension {

    @Input
    var enableKotlin: Boolean = true

    @Input
    var enableKtlint: Boolean = true

    @Input
    var enableKotlinSerialization: Boolean = true

    override fun apply() {
        project.beforeEvaluate {
            addDependencies()
            applyPlugins()

            configureExtensions()
            configureTasks()
        }
    }

    private fun addDependencies() {
        val implementation = project.configurations.getByName("implementation")
        val compileClasspath = project.configurations.getByName("compileClasspath")

        project.dependencies {
            implementation(platform(kotlin("bom:1.7.10")))

            compileClasspath(KOTLIN_DEPENDENCY)
            compileClasspath(KTLINT_DEPENDENCY)
            compileClasspath(KOTLIN_SERIALIZATION_DEPENDENCY)
        }

//        project.gradle.addListener(object : DependencyResolutionListener {
//            override fun beforeResolve(dependencies: ResolvableDependencies) {
//                compileDeps.add(project.dependencies.create(KOTLIN_DEPENDENCY))
//                compileDeps.add(project.dependencies.create(KTLINT_DEPENDENCY))
//                compileDeps.add(project.dependencies.create(KOTLIN_SERIALIZATION_DEPENDENCY))
//
//                println("Added dependencies: $compileDeps")
//
//                project.gradle.removeListener(this)
//            }
//
//            override fun afterResolve(dependencies: ResolvableDependencies) = Unit
//        })
    }

    private fun applyPlugins() {
//        project.beforeEvaluate { prg ->
        project.pluginManager.apply {
//                if (enableKotlin) apply("kotlin")
//                if (enableKtlint) apply("ktlint-gradle")
//                if (enableKotlinSerialization) apply("org.jetbrains.kotlin.plugin.serialization")
            if (enableKotlin) apply(KotlinPlatformJvmPlugin::class)
            if (enableKtlint) apply(KtlintPlugin::class)
            if (enableKotlinSerialization) apply(SerializationGradleSubplugin::class)
//            }
        }
    }

    private fun configureExtensions() {
//        project.afterEvaluate {
        if (!enableKotlin) return

        (project.extensions["kotlin"] as KotlinJvmProjectExtension).jvmToolchain {
            cast<JavaToolchainSpec>().languageVersion.set(JavaLanguageVersion.of(17))
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
//            }
        }
    }

    private fun configureTasks() {
//        project.afterEvaluate {
        if (!enableKotlin) return

        project.tasks.withType<KotlinCompile> {
            kotlinOptions {
                languageVersion = "1.7"
                apiVersion = "1.7"
                jvmTarget = "17"
                useK2 = true
            }
        }
//        }
    }

    companion object {
        const val KOTLIN_DEPENDENCY = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10" // "org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:1.7.10"
        const val KTLINT_DEPENDENCY = "org.jlleitschuh.gradle:org.jlleitschuh.gradle.ktlint:11.0.0"
        const val KOTLIN_SERIALIZATION_DEPENDENCY = "org.jetbrains.kotlin.plugin.serialization:org.jetbrains.kotlin.plugin.serialization.gradle.plugin:1.7.10"
    }
}
