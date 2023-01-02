package dev.racci.minix.gradle.extensions

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import dev.racci.minix.gradle.Constants
import dev.racci.minix.gradle.MinixGradlePlugin
import dev.racci.minix.gradle.access
import dev.racci.minix.gradle.annotations.TopLevelDSLMarker
import dev.racci.minix.gradle.ex.disambiguateName
import dev.racci.minix.gradle.ex.recursiveSubprojects
import dev.racci.minix.gradle.ex.whenEvaluated
import dev.racci.minix.gradle.exceptions.MissingPluginException
import dev.racci.minix.gradle.support.PluginSupport
import dev.racci.minix.gradle.tasks.QuickBuildTask
import dev.racci.minix.gradle.tasks.ShadowJarMPPTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.tasks.Input
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KProperty0

public abstract class MinixBaseExtension(private val project: Project) {

    /**
     * A list of the subprojects and kotlin mpp targets that aren't touched by the plugin.
     */
    @Input
    public val ignoredTargets: MutableList<String> = mutableListOf("metadata")

    @get:Input
    public val minecraft: MinixMinecraftExtension by lazy { MinixMinecraftExtension(project) }

    @get:Input
    public val publishing: MinixPublishingExtension by lazy { MinixPublishingExtension(project) }

    @TopLevelDSLMarker
    public inline fun minecraft(block: MinixMinecraftExtension.() -> Unit) {
        block(minecraft)
    }

    @TopLevelDSLMarker
    public inline fun publishing(block: MinixPublishingExtension.() -> Unit) {
        block(publishing)
    }

    internal fun configure(): Unit = with(project) {
        with(getSupportType(project)) {
            logger.info("Applying support for kotlin-type: $this")
            configureProject(project)
        }

        project.recursiveSubprojects().forEach { subproject ->
            subproject.whenEvaluated {
                if (subproject.name in ignoredTargets) return@whenEvaluated logger.info("Ignoring subproject: ${subproject.name}")

                with(getSupportType(subproject)) {
                    logger.info("Applying support to subproject of ${subproject.name} with kotlin-type: $this")
                    configureProject(subproject)
                }

                PluginSupport.addPluginSupport(subproject)
            }
        }

        whenEvaluated {
            fun maybeLazyConfigure(prop: KProperty0<ExtensionBase>) {
                val lazy = prop.access { getDelegate() as Lazy<ExtensionBase> }
                if (!lazy.isInitialized()) {
                    return logger.info("Not configuring ${prop.name}.")
                }

                logger.info("Configuring ${prop.name}...")
                lazy.value.configure(project)
            }

            maybeLazyConfigure(::minecraft)
            maybeLazyConfigure(::publishing)
        }
    }

    internal fun getSupportType(project: Project): KotlinType = when {
        project.plugins.hasPlugin(KotlinMultiplatformPluginWrapper::class) -> KotlinType.MPP
        project.plugins.hasPlugin(KotlinPluginWrapper::class) -> KotlinType.JVM
        else -> KotlinType.NONE
    }

    public enum class KotlinType {
        NONE {
            override fun configureRootProject(project: Project): Unit = with(project) {
                plugins.maybeApply<JavaLibraryPlugin>()
            }

            override fun configureSubproject(project: Project): Unit = with(project) {
                super.configureSubproject(project)
                plugins.maybeApply<JavaLibraryPlugin>()
            }

            override fun configureExtension(project: Project): Unit = with(project) {
                extensions.getByType<JavaPluginExtension>().toolchain.languageVersion.set(
                    JavaLanguageVersion.of(Constants.JDK_VERSION)
                )
            }
        },
        JVM {
            override fun configureRootProject(project: Project): Unit = with(project) {
                NONE.configureRootProject(project)
                plugins.ensurePlugin<KotlinPlatformJvmPlugin>("kotlin.jvm")
            }

            override fun configureSubproject(project: Project): Unit = with(project) {
                NONE.configureSubproject(project)
                plugins.maybeApply<KotlinPlatformJvmPlugin>()
            }

            override fun configureExtension(project: Project): Unit = with(project) {
                commonKotlinConfiguration()

                dependencies {
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
                commonKotlinConfiguration()
            }

            override fun configureTasks(project: Project): Unit = with(project) {
                (kotlinExtension as KotlinMultiplatformExtension).targets.all {
                    if (this !is KotlinJvmTarget) return@all
                    tasks.register<QuickBuildTask>(disambiguateName("quickBuild"), this)
                    plugins.withType<ShadowPlugin> {
                        tasks.register<ShadowJarMPPTask>(
                            disambiguateName("shadowJar"),
                            this@all
                        )
                    }
                }
            }
        };

        protected val logger: Logger = LoggerFactory.getLogger(MinixGradlePlugin::class.java)

        protected abstract fun configureRootProject(project: Project)

        protected open fun configureSubproject(project: Project): Unit = Unit

        protected abstract fun configureExtension(project: Project)

        protected open fun configureTasks(project: Project): Unit = Unit

        internal open fun configureProject(project: Project) = with(project) {
            plugins.apply(IdeaPlugin::class).model.module {
                isDownloadJavadoc = true
                isDownloadSources = true
            }

            if (project == rootProject) {
                configureRootProject(project)
            } else {
                configureSubproject(project)
                buildDir = rootProject.buildDir.resolve(project.name.lowercase())
            }

            configureExtension(project)
            configureTasks(project)
        }

        protected fun PluginContainer.maybeApply(id: String): Unit = maybeApplyWrapper(
            { hasPlugin(id) },
            { apply(id) },
            id
        )

        protected inline fun <reified T : Plugin<*>> PluginContainer.maybeApply(): Unit = maybeApplyWrapper(
            { hasPlugin(T::class) },
            { apply(T::class) },
            "${T::class.simpleName}"
        )

        protected fun Project.commonKotlinConfiguration() {
            kotlinExtension.explicitApi() // TODO: Check for known error causers and change to warning
            kotlinExtension.jvmToolchain(Constants.JDK_VERSION)
            kotlinExtension.coreLibrariesVersion = KotlinVersion.CURRENT.toString()
            kotlinExtension.sourceSets.all {
                languageSettings.apiVersion = KotlinVersion.CURRENT.let { ver -> "${ver.major}.${ver.minor}" }
                languageSettings.languageVersion = languageSettings.apiVersion
            }
        }

        protected fun maybeApplyWrapper(
            hasPlugin: () -> Boolean,
            apply: () -> Unit,
            id: String
        ) {
            if (!hasPlugin()) {
                logger.info("Applying missing plugin: $id")
                apply()
            } else logger.info("Plugin already applied: $id")
        }

        @Throws(MissingPluginException::class)
        protected inline fun <reified T : Plugin<*>> PluginContainer.ensurePlugin(libString: String) {
            if (!hasPlugin(T::class)) return
            val message = """
                Make sure to apply the ${
            libString.replace(
                '.',
                '-'
            )
            } plugin before by adding the following to your build.gradle.kts:
                    plugins {
                        alias(libs.plugins.$libString)
                    }
            """.trimIndent()

            throw MissingPluginException(message)
        }
    }
}
