package dev.racci.minix.gradle.tasks

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.racci.minix.gradle.ex.nullableTargetTask
import io.papermc.paperweight.tasks.RemapJar
import org.gradle.api.DefaultTask
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.work.DisableCachingByDefault
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import javax.inject.Inject

@DisableCachingByDefault(because = "Not worth caching")
public open class QuickBuildTask @Inject constructor(
    target: KotlinTarget
) : DefaultTask() {
    init {
        group = "minix"
        description = "Builds the target [${target.name}] with minimal tasks (No testing or documentation, etc.)"

        dependsOn(
            target.nullableTargetTask<KotlinCompile>("compileKotlin"),
            target.nullableTargetTask<ProcessResources>("processResources"),
            target.nullableTargetTask<Jar>("jar"),
            target.nullableTargetTask<ShadowJar>("shadowJar"),
            target.nullableTargetTask<RemapJar>("reobfJar")
        )

        finalizedBy(target.nullableTargetTask<CopyJarTask>("copyJar"))
    }
}
