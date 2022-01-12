@file:Suppress("UNUSED")
package dev.racci.minix.platforms

import org.bukkit.plugin.Plugin
import java.io.File
import java.util.function.Predicate

/**
 * Object for loading depends at runtime.
 */
object MinixPlatforms {

    /**
     * Loads a .platform file in the plugin's folder that starts with the platformName.
     */
    @Throws(ReflectiveOperationException::class)
    fun Plugin.loadPlatform(platformName: String) = load(this, platformName)

    /**
     * Loads a .platform file in the plugins' folder that starts with the platformName.
     */
    @Throws(ReflectiveOperationException::class)
    fun load(
        plugin: Plugin,
        platformName: String
    ) = load(plugin) {
        it.name.endsWith(".platform")
            && it.name.startsWith(platformName)
    }

    /**
     * Loads a file matching a predicate out of the plugin folder.
     */
    @Throws(ReflectiveOperationException::class)
    fun load(
        plugin: Plugin,
        predicate: Predicate<File>
    ) {
        val files = plugin.dataFolder.parentFile.listFiles() ?: return
        val injectFile = files.asSequence()
            .filter(predicate::test)
            .firstOrNull() ?: return
        LibraryLoaderInjector.inject(plugin, injectFile)
    }
}
