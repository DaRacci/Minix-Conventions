package dev.racci.minix.gradle.support

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

public object KtlintPluginSupport : PluginSupport(
    "org.jlleitschuh.gradle.ktlint"
) {
    override fun configureRoot(project: Project): Unit = configureSub(project)

    override fun configureSub(project: Project): Unit = project.extensions.configure<KtlintExtension> {
        project.rootProject.layout.projectDirectory
            .dir("config/ktlint")
            .file("baseline-${project.name}.xml")
            .let(baseline::set)

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
