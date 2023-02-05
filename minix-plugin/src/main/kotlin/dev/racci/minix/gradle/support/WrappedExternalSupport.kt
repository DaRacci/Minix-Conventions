package dev.racci.minix.gradle.support

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginAware
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KFunction2

internal sealed interface WrappedSupport : SupportBase {
    val delegate: SupportBase

    companion object {
        fun of(
            backingId: String,
            actualSupport: SupportBase
        ): WrappedSupport {
            return when (backingId) {
                "dev.racci.minix" -> WrappedMinixSupport(actualSupport)
                else -> WrappedExternalSupport(backingId, actualSupport)
            }
        }
    }

    override fun getName(): String {
        return "WrappedSupport:[${delegate.name}]"
    }

    override fun canConfigureNow(project: Project): Boolean {
        return super.canConfigureNow(project) && delegate.canConfigureNow(project)
    }

    override fun <T : Any> registerLazySupport(
        project: Project,
        target: T,
        func: KFunction2<SupportBase, T, Unit>,
    ) = project.plugins.withId(delegate.pluginId) { invoke(target, func) }

    override fun <T : Any> invoke(
        target: T,
        func: KFunction2<SupportBase, T, Unit>
    ) = delegate(target, func)
}

internal class WrappedMinixSupport(
    override val delegate: SupportBase
) : WrappedSupport {
    override val pluginId: String
        get() = "dev.racci.minix"

    override fun getName(): String {
        return "Minix:-${super.getName()}"
    }
}

internal class WrappedExternalSupport(
    override val pluginId: String,
    override val delegate: SupportBase
) : WrappedSupport {
    override fun getName(): String {
        return "External:[$pluginId]-${super.getName()}"
    }

    override fun <T : Any> registerLazySupport(
        project: Project,
        target: T,
        func: KFunction2<SupportBase, T, Unit>,
    ) = project.plugins.withId(pluginId) {
        super.registerLazySupport(project, target, func)
    }

    data class WrappablePlugin(
        val elementFile: File,
        val pluginId: String,
        val classTarget: () -> KClass<out Plugin<out PluginAware>>
    )
}
