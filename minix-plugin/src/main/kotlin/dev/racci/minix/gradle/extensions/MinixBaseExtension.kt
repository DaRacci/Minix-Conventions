package dev.racci.minix.gradle.extensions

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import dev.racci.minix.gradle.Constants
import dev.racci.minix.gradle.MinixGradlePlugin
import dev.racci.minix.gradle.ex.recursiveSubprojects
import dev.racci.minix.gradle.exceptions.MissingPluginException
import dev.racci.minix.gradle.support.DokkaPluginSupport
import dev.racci.minix.gradle.support.KtlintPluginSupport
import dev.racci.minix.gradle.tasks.QuickBuildTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.Input
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModule
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.targets
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public abstract class MinixBaseExtension(
    private val project: Project
) {
    /** Explicitly sets what type of support to apply, otherwise support will be added if the plugin is found. */
    @Input
    public var kotlinSupport: Option<KotlinType> = Option.fromNullable(project.property("minix.kotlinType")?.toString()?.let(KotlinType::valueOf)); private set

    /**
     * A list of the subprojects and kotlin mpp targets that aren't touched by the plugin.
     */
    @Input
    public val ignoredTargets: MutableList<String> = mutableListOf()

    @get:Input
    public val minecraft: MinixMinecraftExtension by lazy { MinixMinecraftExtension() }

    public fun configure(): Unit = with(project) {
        if (kotlinSupport is None) {
            logger.info("No kotlin support type was specified, attempting to detect kotlin support.")

            when {
                project.plugins.hasPlugin(KotlinMultiplatformPlugin::class) -> kotlinSupport = Some(KotlinType.MPP)
                project.plugins.hasPlugin(KotlinPlatformJvmPlugin::class) -> kotlinSupport = Some(KotlinType.JVM)
            }
        }

        val kotlinType = kotlinSupport.getOrElse { KotlinType.NONE }

        logger.info("Applying support for kotlin-type: $kotlinType")

        kotlinType.configureProject(project)
        addPluginSupport(project)

        project.recursiveSubprojects()
            .forEach { subproject ->
                subproject.beforeEvaluate {
                    if (subproject.name in ignoredTargets) return@beforeEvaluate logger.info("Ignoring subproject: ${subproject.name}")
                    logger.info("Applying support to subproject of ${project.name} with kotlin-type: $kotlinType")
                    kotlinType.configureProject(subproject)
                    addPluginSupport(subproject)
                }
            }
    }

    private fun addPluginSupport(target: Project) = with(target) {
        plugins.withType<KtlintPlugin> { KtlintPluginSupport.configure(target) }
        plugins.withType<DokkaPlugin> { DokkaPluginSupport.configure(target) }
    }

    public enum class KotlinType {
        NONE {
            override fun configureRootProject(project: Project): Unit = with(project) {
                plugins.apply(JavaLibraryPlugin::class)
                plugins.apply("kotlin-dsl")
            }

            override fun configureSubproject(project: Project): Unit = with(project) {
                super.configureSubproject(project)
                plugins.apply(JavaLibraryPlugin::class)
                plugins.apply("kotlin-dsl")
            }

            override fun configureExtension(project: Project): Unit = with(project) {
                extensions.getByType<JavaPluginExtension>().toolchain.languageVersion.set(JavaLanguageVersion.of(Constants.JDK_VERSION))
            }
        },
        JVM {
            override fun configureRootProject(project: Project): Unit = with(project) {
                NONE.configureRootProject(project)
                plugins.ensurePlugin<KotlinPlatformJvmPlugin>("kotlin.jvm")
            }

            override fun configureSubproject(project: Project): Unit = with(project) {
                NONE.configureSubproject(project)
                plugins.apply(KotlinPlatformJvmPlugin::class)
            }

            override fun configureExtension(project: Project): Unit = with(project) {
                kotlinExtension.explicitApi() // TODO: Check for known error causers and change to warning
                kotlinExtension.jvmToolchain(Constants.JDK_VERSION)
                kotlinExtension.coreLibrariesVersion = KotlinVersion.CURRENT.toString()
                kotlinExtension.sourceSets.map(KotlinSourceSet::languageSettings).forEach { settings ->
                    settings.apiVersion = KotlinVersion.CURRENT.let { ver -> "${ver.major}.${ver.minor}" }
                    settings.languageVersion = settings.apiVersion
                }

                project.dependencies {
                    "implementation"(platform(kotlin("bom", KotlinVersion.CURRENT.toString())))
                    "compileOnly"(kotlin("stdlib-jdk8"))
                }
            }
        },
        MPP {
            override fun configureRootProject(project: Project): Unit = with(project) {
                super.configureSubproject(project)
                plugins.ensurePlugin<KotlinMultiplatformPlugin>("kotlin.mpp")
            }

            override fun configureExtension(project: Project): Unit = with(project) {
                extensions.configure<KotlinProjectExtension> {
                    JVM.configureExtension(project)
                }
            }

            override fun configureTasks(project: Project): Unit = with(project) {
                kotlinExtension.targets.forEach { target ->
                    tasks.register<QuickBuildTask>("${target.name}QuickBuild", target)
                }
            }
        };

        protected val logger: Logger = LoggerFactory.getLogger(MinixGradlePlugin::class.java)

        protected abstract fun configureRootProject(project: Project)

        protected open fun configureSubproject(project: Project): Unit = Unit

        protected abstract fun configureExtension(project: Project)

        protected open fun configureTasks(project: Project): Unit = Unit

        internal open fun configureProject(project: Project) = with(project) {
            plugins.apply(IdeaPlugin::class)
            extensions.configure<IdeaModule> {
                isDownloadJavadoc = true
                isDownloadSources = true
            }

            if (project == rootProject) {
                configureRootProject(project)
            } else {
                configureSubproject(project)
                buildDir = rootDir.resolve(project.name.lowercase())
            }

            configureExtension(project)
            configureTasks(project)
        }

        @Throws(MissingPluginException::class)
        protected inline fun <reified T : Plugin<*>> PluginContainer.ensurePlugin(libString: String) {
            if (!hasPlugin(T::class)) return
            val message = """
                Make sure to apply the ${libString.replace('.', '-')} plugin before by adding the following to your build.gradle.kts:
                    plugins {
                        alias(libs.plugins.$libString)
                    }
            """.trimIndent()

            throw MissingPluginException(message)
        }
    }
}
