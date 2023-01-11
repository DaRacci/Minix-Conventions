package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.Constants
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.dokka.gradle.DokkaCollectorTask
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

// TODO: Target support.
public object DokkaPluginSupport : PluginSupport(
    id = "org.jetbrains.dokka",
    target = { DokkaPlugin::class }
) {
    override fun configureRoot(project: Project): Unit = with(project) {
        fun AbstractDokkaTask.setOutput() = outputDirectory.set(rootDir.resolve("docs"))
        tasks.withType<DokkaCollectorTask>().configureEach(AbstractDokkaTask::setOutput)
        tasks.withType<DokkaMultiModuleTask>().configureEach(AbstractDokkaTask::setOutput)
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
        }
}
