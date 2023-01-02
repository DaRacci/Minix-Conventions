package dev.racci.minix.gradle.support

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

public object KtlintPluginSupport : PluginSupport(
    id = "org.jlleitschuh.gradle.ktlint",
    target = { KtlintPlugin::class }
) {
    override fun configure(project: Project): Unit = configureSub(project)

    override fun configureSub(project: Project): Unit = project.extensions.configure<KtlintExtension> {
        project.rootDir.resolve("config/ktlint/").resolve("baseline-${project.name}.xml").let(baseline::set)
        version.set("0.45.2")
        coloredOutput.set(true)
        outputToConsole.set(true)
        enableExperimentalRules.set(false)
        reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.HTML)
            reporter(ReporterType.CHECKSTYLE)
        }
    }
}
