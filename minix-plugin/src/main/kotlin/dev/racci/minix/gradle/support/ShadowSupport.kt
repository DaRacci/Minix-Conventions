package dev.racci.minix.gradle.support

import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin
import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import dev.racci.minix.gradle.ex.disambiguateName
import dev.racci.minix.gradle.tasks.ShadowJarMPPTask
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

public object ShadowSupport : PluginSupport(
    "com.github.johnrengelman.shadow",
    { ShadowPlugin::class }
) {
    override fun configureTarget(target: KotlinTarget): Unit = with(target) {
        project.tasks.register<ShadowJarMPPTask>(
            disambiguateName(ShadowJavaPlugin.getSHADOW_JAR_TASK_NAME()),
            target
        )
    }
}
