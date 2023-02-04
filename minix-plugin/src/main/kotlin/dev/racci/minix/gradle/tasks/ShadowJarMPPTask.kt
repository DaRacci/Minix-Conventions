package dev.racci.minix.gradle.tasks

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.CacheableTask
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import javax.inject.Inject

@CacheableTask
@Suppress("UnnecessaryAbstractClass")
public abstract class ShadowJarMPPTask @Inject constructor(
    target: KotlinJvmTarget
) : ShadowJar() {
    init {
        val mainCompilation = target.compilations["main"]

        this.group = "Shadow"
        this.from(mainCompilation.output)
        this.configurations = listOf(project.configurations[mainCompilation.runtimeDependencyConfigurationName])
        this.archiveAppendix.set(target.targetName)
        this.archiveClassifier.set("all")
        this.mergeServiceFiles()

        project.tasks[target.artifactsTaskName].finalizedBy(this)
    }
}
