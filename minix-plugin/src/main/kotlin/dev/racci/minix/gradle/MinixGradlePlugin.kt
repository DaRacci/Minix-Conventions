package dev.racci.minix.gradle

import dev.racci.minix.gradle.ex.whenEvaluated
import dev.racci.minix.gradle.extensions.ExtensionBase
import dev.racci.minix.gradle.extensions.MinixBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import kotlin.reflect.KProperty0

public class MinixGradlePlugin : Plugin<Project> {
    override fun apply(project: Project): Unit = with(project) {
        require(project.rootProject == project) {
            "MinixGradlePlugin must be applied to the root project! (Currently)"
        }

        val baseExtension = project.extensions.create<MinixBaseExtension>("minix", project)
        baseExtension.configure()

        whenEvaluated {
            fun maybeLazyConfigure(prop: KProperty0<ExtensionBase>) {
                val lazy = prop.getDelegate() as Lazy<ExtensionBase>
                if (lazy.isInitialized()) return
                lazy.value.configure(project)
            }

            maybeLazyConfigure(baseExtension::minecraft)
        }
    }
}
