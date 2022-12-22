package dev.racci.minix.gradle.support

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

public object KtlintPluginSupport : PluginSupport {
    override fun configure(target: Project): Unit = target.extensions.configure<KtlintExtension> {
        target.rootDir.resolve("config/ktlint/").resolve("baseline-${target.name}.xml").let(baseline::set)
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
