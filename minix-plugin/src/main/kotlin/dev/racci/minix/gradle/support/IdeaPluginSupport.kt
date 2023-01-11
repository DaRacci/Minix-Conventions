package dev.racci.minix.gradle.support

import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel

public object IdeaPluginSupport : PluginSupport(
    "idea",
    { IdeaPlugin::class }
) {
    override fun configureSub(project: Project): Unit = with(project.extensions.getByType<IdeaModel>().module) {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
