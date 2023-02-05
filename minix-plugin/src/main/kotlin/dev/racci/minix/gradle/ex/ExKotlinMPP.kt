package dev.racci.minix.gradle.ex

import TargetTask
import TargetTaskContainerScope
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.racci.minix.gradle.data.MCTarget
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.plugin.sources.DefaultKotlinSourceSet
import org.jetbrains.kotlin.gradle.utils.named

public val TargetTaskContainerScope.shadowJar: TargetTask<ShadowJar>
    get() = named<ShadowJar>("shadowJar")

public fun KotlinSourceSet.withMCTarget(
    platform: MCTarget.Platform,
    version: String? = null,
    applyDefaultDependencies: Boolean = true,
    applyMinix: Boolean = true,
    applyNMS: Boolean = false
) {
    project().highestOrderExtension().minecraft.mcTargets.add(
        MCTarget(
            this,
            platform,
            applyDefaultDependencies,
            applyMinix,
            applyNMS,
            version
        )
    )
}

public fun KotlinSourceSet.project(): Project = DefaultKotlinSourceSet::class.java
    .getDeclaredField("project").let { field ->
        field.isAccessible = true
        field.get(this) as Project
    }
