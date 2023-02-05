package dev.racci.minix.gradle.support

import dev.racci.minix.gradle.data.disambiguate
import dev.racci.minix.gradle.tasks.ShadowJarMPPTask
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

public object ShadowSupport : AbstractMultiplatformSupport(
    KotlinPlatformType.jvm,
    pluginId = "com.github.johnrengelman.shadow",
) {
    override fun configureTargetFiltered(target: KotlinTarget): Unit = with(target) {
        if (name.isEmpty()) return // This is the root target, we don't want to configure it.
        project.tasks.register<ShadowJarMPPTask>(
            disambiguate("shadowJar"),
            target
        )
    }
}
