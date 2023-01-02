package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.Constants
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

public object DokkaPluginSupport : PluginSupport(
    id = "org.jetbrains.dokka",
    target = { DokkaPlugin::class }
) {

    override fun configure(project: Project): Unit = with(project) {
        tasks.withType<DokkaMultiModuleTask> { outputDirectory.set(buildDir.resolve("docs")) }
        configureSub(project) // TODO: Check if has sources
    }

    override fun configureSub(project: Project): Unit =
        project.tasks.withType<DokkaTask>().flatMap(DokkaTask::dokkaSourceSets).forEach { sourceSet ->
            sourceSet.suppressGeneratedFiles.set(false)
            sourceSet.reportUndocumented.set(true)
            sourceSet.skipEmptyPackages.set(true)
            sourceSet.skipDeprecated.set(true)
            sourceSet.platform.set(Platform.jvm)
            sourceSet.jdkVersion.set(Constants.JDK_VERSION)

            sourceSet.sourceLink {
                remoteLineSuffix.set("#L") // GitHub
            }
        }
}
