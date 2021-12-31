package dev.racci.minix.platforms

import java.io.File
import java.net.MalformedURLException
import java.net.URLClassLoader
import java.util.function.Function
import java.util.logging.Level

internal interface PlatformProvider: Function<File, URLClassLoader>

internal class PlatformProviderImpl: PlatformProvider {

    private val alreadyLoaded: MutableMap<File, URLClassLoader> = HashMap()

    override fun apply(file: File): URLClassLoader {
        var cached = alreadyLoaded[file]
        if(cached == null) {
            try {
                cached = URLClassLoader(arrayOf(file.toURI().toURL()))
                alreadyLoaded[file] = cached
            } catch(e: MalformedURLException) {
                org.bukkit.plugin.PluginLogger.getAnonymousLogger().log(Level.FINEST, e.message)
            }
        }
        return cached!!
    }
}
