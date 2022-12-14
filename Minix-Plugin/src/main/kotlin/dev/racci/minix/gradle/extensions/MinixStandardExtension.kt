package dev.racci.minix.gradle.extensions

import arrow.core.Option
import dev.racci.minix.gradle.Constants
import dev.racci.minix.gradle.maybeExtend
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenRepositoryContentDescriptor
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

public class MinixStandardExtension(override val project: Project) : Extension {
    private val actualEnableKotlin: Boolean get() = enableKotlin || enableKtlint

    @Input
    public var enableKotlin: Boolean = true

    @Input
    public var enableKtlint: Boolean = true

    private val mpp: Option<KotlinMultiplatformExtension> by lazy {
        Option.fromNullable(project.kotlinExtension as? KotlinMultiplatformExtension)
    }

    private inline fun <reified E : KotlinProjectExtension> kotlinExtension(configure: E.() -> Unit): E {
        val ext = project.kotlinExtension as E
        ext.configure()
        return ext
    }

    override fun apply() {
        project.beforeEvaluate {
            configureGeneral()
            configureConfigurations()

            addRepositories()
            addDependencies()
        }

//        applyPlugins()

        configureKotlin()
        configureKtLint()
    }

    private fun configureGeneral() = project.allprojects {
        when (this) {
            this.rootProject -> "root"
            else -> this.name.toLowerCase()
        }.also { strDir -> this.buildDir = this.rootProject.buildDir.resolve(strDir) }

        configurations.all {
            val slim by this.compileClasspath.get().extendsFrom(slim)
            runtimeClasspath.get().extendsFrom(slim)
            apiElements.get().extendsFrom(slim)
        }
    }

    private fun addRepositories() = project.repositories {
        mavenCentral()
        maven(Constants.RACCI_REPO + "releases") { mavenContent(MavenRepositoryContentDescriptor::releasesOnly) }
        maven(Constants.RACCI_REPO + "snapshots") // No snapshot only since some content won't have it such as dev-bundle.
    }

    private fun addDependencies() = project.dependencies {
        val implementation = project.configurations.getByName("implementation")
        val compileClasspath = project.configurations.getByName("compileClasspath")

        compileClasspath(platform(kotlin("bom:${KotlinVersion.CURRENT}")))
        compileClasspath(kotlin("stdlib-jdk8")) // Don't allow consumers to shadow jar this by default.
    }

    private fun configureConfigurations() = project.configurations.whenObjectAdded {
        if (name == "testImplementation") { // In some situations, this dependency will cause and error unless removed.
            exclude(kotlinJunit.first, kotlinJunit.second)
        }

        // Sometimes these dependencies will refuse to resolve unless removed.
        exclude(simpleYAMLConfig.first, simpleYAMLConfig.second)
        exclude(simpleYAMLYaml.first, simpleYAMLYaml.second)
        exclude(flare.first, flare.second)
    }

    private fun applyPlugins() = project.pluginManager.apply {
        if (actualEnableKotlin) apply(KotlinPlatformJvmPlugin::class)
        if (enableKtlint) apply(KtlintPlugin::class)
    }

    private fun configureKotlin() {
        if (!actualEnableKotlin) return

        fun configureKt() {
            kotlinExtension<KotlinProjectExtension> {
                this.jvmToolchain(Constants.JDK_VERSION)
                this.explicitApiWarning() // Koin generated sources aren't complicit.

                sourceSets.forEach { set -> set.kotlin.maybeExtend(project, "${project.buildDir}/generated/ksp/main/kotlin") }
                (sourceSets.findByName("test") ?: sourceSets.getByName("commonTest")).apply { kotlin.maybeExtend(project, "${project.buildDir}/generated/ksp/main/kotlin") }
            }

            project.tasks.withType<KotlinCompile>().configureEach {
                kotlinOptions {
                    languageVersion = KotlinVersion.CURRENT.toString().substringBeforeLast(".")
                    apiVersion = languageVersion
                    jvmTarget = Constants.JDK_VERSION.toString()
                    useK2 = false // TODO -> Enable when stable enough
                }
            }
        }

        project.plugins.withId("kotlin-multiplatform") { configureKt() }
        project.plugins.withId("kotlin-jvm") { configureKt() }
    }

    private fun configureKtLint() {
        if (!enableKtlint) return

        project.extensions.configure<KtlintExtension> {
            version.set("0.45.2")
            enableExperimentalRules.set(false)
            reporters {
                reporter(ReporterType.PLAIN)
                reporter(ReporterType.HTML)
                reporter(ReporterType.CHECKSTYLE)
            }
        }
    }

    private companion object {
        val kotlinJunit = Pair("org.jetbrains.kotlin", "kotlin-test-junit")
        val simpleYAMLConfig = Pair("me.carleslc.Simple-YAML", "Simple-Configuration")
        val simpleYAMLYaml = Pair("me.carleslc.Simple-YAML", "Simple-Yaml")
        val flare = Pair("com.github.technove", "Flare")
    }
}
