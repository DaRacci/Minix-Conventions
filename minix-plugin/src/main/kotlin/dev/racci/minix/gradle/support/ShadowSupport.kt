package dev.racci.minix.gradle.support

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import dev.racci.minix.gradle.ex.disambiguateName
import dev.racci.minix.gradle.tasks.ShadowJarMPPTask
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

public object ShadowSupport : AbstractMultiplatformSupport(
    KotlinPlatformType.jvm,
    id = "com.github.johnrengelman.shadow",
    target = { ShadowPlugin::class }
) {
    override fun configureTargetFiltered(target: KotlinTarget): Unit = with(target) {
        project.tasks.register<ShadowJarMPPTask>(
            disambiguateName("shadowJar"),
            target
        )
    }
}
